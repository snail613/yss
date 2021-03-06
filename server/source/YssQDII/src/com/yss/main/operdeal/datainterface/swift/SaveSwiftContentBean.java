package com.yss.main.operdeal.datainterface.swift;

import java.sql.*;
import java.util.*;
import oracle.sql.CLOB;

import com.yss.dsub.BaseBean;
import com.yss.util.*;

/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * 保存SWIFT原文信息的POJO类
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SaveSwiftContentBean
    extends BaseBean {
    private Connection conn; //从外面一起传进来用,注意这里不能创建,为的是可以使数据同步更新回滚

    private String portCode = "";
    private String fileName = "";
    private String swiftDate = "";
    private String swiftType = "";
    private String baseHead = "";
    private String AppHead = "";
    private String swiftText = "";
    private String swiftEnd = "";
    private String reflow = "";
    private String swiftNum = "";
    private String relaNum = "";
    private String swiftIndex = ""; //报文序号，报文唯一序号，是从报文中获取的号码
    private String standard = "";
    private String operType = "";
    private String swiftStatus =""; //新增字段 ，报文状态，如NEWS、CANC 等 by leeyu 20091110
    private int checkState = 0;
    private String swiftCode="";//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    private String swiftDesc ="";//添加备注信息 by leeyu 20100104
    public String getSwiftDesc() {
		return swiftDesc;
	}

	public void setSwiftDesc(String swiftDesc) {
		this.swiftDesc = swiftDesc;
	}
	private ArrayList alList;
    public SaveSwiftContentBean() {
    }

    /**
     * 保存SWIFT原文的方法
     * 此方法也不执行数据插入的事务，事务统一在调用方法里执行处理
     * @throws YssException
     */
    public void insert(boolean bDel) throws YssException {
        //1:先删除原数据
        //2:插入新数据
        String sqlStr = "";
        PreparedStatement pst = null;
        PreparedStatement subPst = null;
        ResultSet rs = null;
        SaveSwiftContentBean content;
        boolean bTrans =false;
        try {
        	conn =dbl.loadConnection();
        	conn.setAutoCommit(bTrans);
        	bTrans =true;
            if (alList == null || alList.size() == 0) {
                return;
            }
        //---------------MS00970 modify by jiangshichao 2010.02.25 --------------------------------    
        //把这段迁到循环外，否则，每次循环都会创建PrepareStatement,造成游标越界 
            sqlStr = "update " +
            pub.yssGetTableName("Tb_Data_OriginalSWIFT") +
            " set FText = ? " +
            " where FSwiftIndex= ? " +
            " and FFullFileName= ? "+
            " and FSwiftType=? "+
            " and FReflow=?"+
            " and FSwiftStatus=?"+
            " and FDate =?"+
            " and FSwiftCode=?";//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        subPst = conn.prepareStatement(sqlStr);
        sqlStr = "insert into " +
            pub.yssGetTableName("Tb_Data_OriginalSWIFT") +
            " (FDate,FReflow,FFullFileName,FSwiftType,FPortCode,FSwiftIndex,FSwiftNum,FRelaNum,FCheckState,FSwiftStatus,FText,FSwiftCode) " +//edited by libo 加入FSwiftStatus字段//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            " values(?,?,?,?,?,?,?,?,?,?,?,?)";//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        pst = conn.prepareStatement(sqlStr);
        //----------------------------------------------------------------------------------//
            for (int i = 0; i < alList.size(); i++) {
                content = (SaveSwiftContentBean) alList.get(i);
                if(bDel){
                	delete(content);
                }
                if (dbl.getDBType() == YssCons.DB_ORA) {
                    pst.setDate(1, YssFun.toSqlDate(content.getSwiftDate()));
                    pst.setString(2, content.getReflow());
                    pst.setString(3, content.getFileName());
                    pst.setString(4, content.getSwiftType());
                    pst.setString(5, content.getPortCode());
                    pst.setString(6, content.getWwiftIndex());
                    pst.setString(7, content.getSwiftNum());
                    pst.setString(8, content.getRelaNum());
                    pst.setInt(9, content.getCheckState());
                    pst.setString(10, content.getSwiftStatus()); //新字段 by leeyu 20091110
                    pst.setString(11, "EMPTY_CLOB()");
                    pst.setString(12, content.getSwiftCode());//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                    pst.executeUpdate();
                    sqlStr = "select FText from " +
                        pub.yssGetTableName("Tb_Data_OriginalSWIFT") +
                        " where FSwiftIndex=" + dbl.sqlString(content.getWwiftIndex()) +
                        " and FFullFileName=" + dbl.sqlString(content.getFileName())+
                        " and FSwiftType="+dbl.sqlString(content.getSwiftType())+
                        " and FReflow="+dbl.sqlString(content.getReflow())+
                        " and FSwiftStatus="+dbl.sqlString(content.getSwiftStatus())+
                        (content.getSwiftCode().trim().length()>0?(" and FSwiftCode="+dbl.sqlString(content.getSwiftCode())):"")+//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                        " and FDate="+dbl.sqlDate(content.getSwiftDate())+" for update";
                    rs = dbl.openResultSet(sqlStr);
                    if (rs.next()) {
                    	  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
                    	  // modify by jsc 20120809 连接池对大对象的特殊处理
                    	CLOB clob = dbl.CastToCLOB(rs.getClob("FText"));
                        //CLOB clob = ( (oracle.jdbc.OracleResultSet) rs).getCLOB("FText");
                        clob.putString(1, content.getSwiftText());
                        subPst.setClob(1, clob);
                        subPst.setString(2, content.getWwiftIndex());
                        subPst.setString(3, content.getFileName());
                        subPst.setString(4, content.getSwiftType());
                        subPst.setString(5, content.getReflow());
                        subPst.setString(6, content.getSwiftStatus());
                        subPst.setDate(7, YssFun.toSqlDate(content.getSwiftDate()));
                        subPst.setString(8, content.getSwiftCode());//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                        subPst.executeUpdate();
                    }
                    rs.getStatement().close();
                } else if (dbl.getDBType() == YssCons.DB_DB2) {
                    pst.setDate(1, YssFun.toSqlDate(content.getSwiftDate()));
                    pst.setString(2, content.getReflow());
                    pst.setString(3, content.getFileName());
                    pst.setString(4, content.getSwiftType());
                    pst.setString(5, content.getPortCode());
                    pst.setString(6, content.getWwiftIndex());
                    pst.setString(7, content.getSwiftNum());
                    pst.setString(8, content.getRelaNum());
                    pst.setInt(9, content.getCheckState());
                    pst.setString(10, content.getSwiftStatus());//新字段 by leeyu 20091110
                    pst.setString(11, content.getSwiftText());
                    pst.setString(12, content.getSwiftCode());//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                    pst.executeUpdate();
                } else {

                }
            }
            pst.close();
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans =false;
        } catch (Exception ex) {
            throw new YssException("插入数据出错", ex);
        } finally {
           dbl.closeStatementFinal(pst,subPst); 
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }



	/**
     * 删除表中原有的原文数据
     * 这里不用添加事务处理，统一在调用方法中执行事务处理
     * @throws YssException
     */
    public void delete(SaveSwiftContentBean content) throws YssException {
        String sqlStr = "";
        try {
            sqlStr = "delete from " + pub.yssGetTableName("Tb_Data_OriginalSWIFT") +
                " where FSwiftIndex=" + dbl.sqlString(content.getWwiftIndex()) +
                " and FFullFileName=" + dbl.sqlString(content.getFileName())+
                " and FSwiftStatus="+dbl.sqlString(content.getSwiftStatus())+
                " and FReflow= "+dbl.sqlString(content.getReflow())+
                " and FSwiftType="+dbl.sqlString(content.getSwiftType())+
                " and FDate ="+dbl.sqlDate(content.getSwiftDate())+
                (content.getSwiftCode().trim().length()>0?(" and FSwiftCode="+dbl.sqlString(content.getSwiftCode())):"");////by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            dbl.executeSql(sqlStr);
        } catch (Exception ex) {
            throw new YssException("删除数据出错！", ex);
        }
    }
    
    /**
     * 审核反审核报文数据
     * @param alContent
     * @throws YssException
     */
    public void audit(ArrayList alContent) throws YssException{
    	PreparedStatement stm = null;
    	Connection conn =dbl.loadConnection();
    	boolean bTrans =false;
    	try{
    		conn.setAutoCommit(bTrans);
    		bTrans =true;
    		String sqlStr="update "+pub.yssGetTableName("Tb_Data_OriginalSWIFT")+
    		" set FCheckState=? where FRelaNum=? and FDate=? and FSwiftIndex=? "+
    		" and FSwiftNum=? and FPortCode=? and FSwiftStatus=? and FSwiftType=? and FReflow=? and FFullFileName=? and FSwiftCode=?";//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    		stm =conn.prepareStatement(sqlStr);
    		for(int i=0;i<alContent.size();i++){
    			SaveSwiftContentBean content =(SaveSwiftContentBean)alContent.get(i);
    			stm.setInt(1, content.getCheckState());
    			stm.setString(2, content.getRelaNum());
    			stm.setDate(3, YssFun.toSqlDate(YssFun.formatDate(content.getSwiftDate(),"yyyy-MM-dd")));
    			stm.setString(4, content.getSwiftStatus().equalsIgnoreCase("NEW")?(content.getWwiftIndex().indexOf("|")>0?content.getWwiftIndex().substring(content.getWwiftIndex().indexOf("|")+6, content.getWwiftIndex().length()):content.getWwiftIndex()):content.getWwiftIndex());
    			stm.setString(5, content.getSwiftNum());
    			stm.setString(6, content.getPortCode());
    			stm.setString(7, content.getSwiftStatus());
    			stm.setString(8, content.getSwiftType());
    			stm.setString(9, content.getReflow());
    			stm.setString(10, content.getFileName());
    			stm.setString(11, content.getSwiftCode());//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    			stm.executeUpdate();
    		}
    		conn.commit();
    		conn.setAutoCommit(bTrans);
    		bTrans =false;
    	}catch(Exception ex){
    		throw new YssException("审核数据出错！",ex);
    	}finally{
    		dbl.closeStatementFinal(stm);
    		dbl.endTransFinal(conn,bTrans);
    	}
    }
    
    /**
     * 用于在导出时执行SWIFT撤消报文的更新
     * @throws YssException
     */
    public void update()throws YssException{
    	Connection conn =null;
    	String sqlStr="";
    	ResultSet rs =null;
    	boolean bTrans =false;
    	try{
    		conn =dbl.loadConnection();
    		conn.setAutoCommit(bTrans);
    		bTrans =true; 
    		CLOB clob =null;
    		if(swiftType.startsWith("MT1")|| swiftType.startsWith("MT2")){
    			PreparedStatement stm =null;
    			String sSession="",sIsn="",s79="",sCopyField="";
    			String sText=null;
    			String[] arrPara =swiftText.split("\r");
    			sSession = arrPara[0];
    			sIsn = arrPara[1];
    			s79 = arrPara[2];
    			sCopyField = arrPara[3];
    			sqlStr = "select FText from " + pub.yssGetTableName("Tb_Data_OriginalSWIFT") +
                " where FSwiftIndex like '%" + swiftIndex + "'"+
                //" and FFullFileName=" + dbl.sqlString(fileName)+
                " and FSwiftStatus="+dbl.sqlString("CANC")+
                " and FReflow= "+dbl.sqlString(reflow)+
                " and FSwiftType="+dbl.sqlString(swiftType.substring(0,3)+"92")+
                " and FDate ="+dbl.sqlDate(swiftDate)+
                 ((portCode!=null&&portCode.trim().length()>0)?(" and FPortCode="+dbl.sqlString(portCode)):" ")+
                 " and FSwiftCode<>"+dbl.sqlString(swiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    			if(dbl.dbType==YssCons.DB_ORA)
    				sqlStr+=" for update";
        		rs =dbl.openResultSet(sqlStr);
        		if(rs.next()){
        			sText =dbl.clobStrValue(rs.getClob("FText"));
        			  //STORY #2341 使用数据库连接池机制，以避免数据库连接堵塞
        			  // modify by jsc 20120809 连接池对大对象的特殊处理
        			clob =dbl.CastToCLOB(rs.getClob("FText"));
        			 //clob =( (oracle.jdbc.OracleResultSet) rs).getCLOB("FText");
        		}
        		if(sText==null) return;
        		sText=sText.replaceAll("<Session>", sSession);
        		sText=sText.replaceAll("<Session>", "");
        		sText=sText.replaceAll("<ISN>", sIsn);
        		sText=sText.replaceAll("<ISN>", "");
        		sText=sText.replaceAll("<79>", s79);
        		sText=sText.replaceAll("<79>", "");
        		sText=sText.replaceAll("<CopyField>", sCopyField);
        		sText=sText.replaceAll("<CopyField>", "");
        		sqlStr="update "+ pub.yssGetTableName("Tb_Data_OriginalSWIFT")+
        		" set FText=? ,FCheckState=? "+
        		" where FSwiftIndex like '%" +swiftIndex+"'"+
                //" and FFullFileName=" + dbl.sqlString(fileName)+
                " and FSwiftStatus="+dbl.sqlString("CANC")+
                " and FReflow= "+dbl.sqlString(reflow)+
                " and FSwiftType="+dbl.sqlString(swiftType.substring(0,3)+"92")+
                " and FDate ="+dbl.sqlDate(swiftDate)+
                 ((portCode!=null&&portCode.trim().length()>0)?(" and FPortCode="+dbl.sqlString(portCode)):" ")+
                 " and FSwiftCode<>"+dbl.sqlString(swiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        		stm =conn.prepareStatement(sqlStr);
        		if(dbl.getDBType() ==YssCons.DB_ORA){	        		
	        		clob.putString(1, sText);
	        		stm.setClob(1, clob);
        		}else{
        			stm.setString(1, sText);
        		}
        		stm.setInt(2, 1);
        		stm.executeUpdate();
    		}else{
    			sqlStr="update "+ pub.yssGetTableName("Tb_Data_OriginalSWIFT")+
        		" set FCheckState=1 "+
        		" where FSwiftIndex like '%" + swiftType+swiftIndex+"'"+
                //" and FFullFileName=" + dbl.sqlString(fileName)+
                " and FSwiftStatus="+dbl.sqlString("CANC")+
                " and FReflow= "+dbl.sqlString(reflow)+
                " and FSwiftType="+dbl.sqlString(swiftType)+
                " and FDate ="+dbl.sqlDate(swiftDate)+
                 ((portCode!=null&&portCode.length()>0)?(" and FPortCode="+dbl.sqlString(portCode)):" ")+
                 " and FSwiftCode<>"+dbl.sqlString(swiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    			dbl.executeSql(sqlStr);
    		}
    		conn.commit();
    		conn.setAutoCommit(bTrans);
    		bTrans =false;
    	}catch(Exception ex){
    		throw new YssException("更新撤消报文出错",ex);
    	}finally{
    		dbl.closeResultSetFinal(rs);
    		dbl.endTransFinal(conn, bTrans);
    	}
    }
    //导出成功后，将后台报文数据表本条记录的FExcuteTag字段写入“1”
    public void updateFExcuteTag() throws YssException{
    	Connection conn =null;
    	String sqlStr="";
    	boolean bTrans =false;
    	try{
    		conn =dbl.loadConnection();
    		conn.setAutoCommit(bTrans);
    		bTrans =true; 
    		sqlStr="update "+ pub.yssGetTableName("Tb_Data_OriginalSWIFT")+
    		" set FExcuteTag=1"+
    		" where FSwiftNUM = " +dbl.sqlString(swiftNum)+
            " and FSwiftStatus="+dbl.sqlString(swiftStatus)+
            " and FReflow= "+dbl.sqlString(reflow)+
            " and FSwiftType="+dbl.sqlString(swiftType)+
            " and FDate ="+dbl.sqlDate(swiftDate)+
             ((portCode!=null&&portCode.length()>0)?(" and FPortCode="+dbl.sqlString(portCode)):" ")+
             " and FSwiftCode="+dbl.sqlString(swiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    		dbl.executeSql(sqlStr);
    		conn.commit();
    		conn.setAutoCommit(bTrans);
    		bTrans =false;
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage(),ex);
    	}finally{
    		dbl.endTransFinal(conn, bTrans);
    	}
    }
    
    public String getMaxNum() throws YssException{
    	String Result ="";
    	try{
    		Result= dbFun.getNextInnerCode(
    				pub.yssGetTableName("Tb_Data_OriginalSWIFT"),
    				dbl.sqlSubStr("FSwiftNum", "17"), "0000001",
    				" where FSwiftNum like 'EXP"+swiftType+
    				YssFun.formatDate(YssFun.toDate(swiftDate),"yyyyMMdd")+"%'",1);    		
    	}catch(Exception ex){
    		throw new YssException("获取最大编号出错！");
    	}
    	return Result;
    }
    
    public void parseRowStr(String sReqStr,String refType) throws YssException{
    	String[] arrReq=null;
    	if(sReqStr==null || sReqStr.length()==0) return;
    	if(refType.equalsIgnoreCase("out")){
    		arrReq = sReqStr.split("\t");
    		this.relaNum = arrReq[0];
    		this.swiftDate = YssFun.formatDate(arrReq[1]);
    		this.portCode = arrReq[2];
    		this.swiftType = arrReq[3];
    		this.swiftNum = arrReq[4];
    		this.fileName = arrReq[5];
    		this.swiftIndex = arrReq[6];
    		this.swiftStatus = arrReq[7];
    		this.reflow = arrReq[8];
    		this.checkState = YssFun.toInt(arrReq[9]);
    		this.standard = arrReq[10];
    		this.operType = arrReq[11];
    		this.swiftText = arrReq[12];
    		this.swiftCode = arrReq[13];//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    		this.swiftDesc = arrReq[14];// 备注信息　by leeyu 20100104
    	}else{
    		
    	}
    }

    public void set(ArrayList list) {
        alList = list;
    }

    public void setSwiftIndex(String swiftIndex) {
        this.swiftIndex = swiftIndex;
    }

    public void setCheckState(int checkState) {
        this.checkState = checkState;
    }

    public void setSwiftType(String swiftType) {
        this.swiftType = swiftType;
    }

    public void setSwiftText(String swiftText) {
        this.swiftText = swiftText;
    }

    public void setSwiftNum(String swiftNum) {
        this.swiftNum = swiftNum;
    }

    public void setSwiftDate(String swiftDate) {
        this.swiftDate = swiftDate;
    }

    public void setRelaNum(String relaNum) {
        this.relaNum = relaNum;
    }

    public void setReflow(String reflow) {
        this.reflow = reflow;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSwiftType() {
        return swiftType;
    }

    public String getSwiftText() {
        return swiftText;
    }

    public String getSwiftNum() {
        return swiftNum;
    }

    public String getSwiftDate() {
        return swiftDate;
    }

    public String getRelaNum() {
        return relaNum;
    }

    public String getReflow() {
        return reflow;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getFileName() {
        return fileName;
    }

    public int getCheckState() {
        return checkState;
    }

    public String getWwiftIndex() {
        return swiftIndex;
    }

	public void setSwiftStatus(String swiftStatus) {
		this.swiftStatus = swiftStatus;
	}

	public String getSwiftStatus() {
		return swiftStatus;
	}
	//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
	public String getSwiftCode() {
		return swiftCode;
	}
    public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}
}
