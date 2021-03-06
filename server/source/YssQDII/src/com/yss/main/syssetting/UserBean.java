package com.yss.main.syssetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.dsub.UserRight;
import com.yss.main.dao.IDataSetting;
import com.yss.pojo.sys.YssTrans;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 *
 * <p>Title: UserBean </p>
 * <p>Description: 用户设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * 1.添加上次修改日期、密码修改间隔天数、不能与前N次密码相同
 * add by xuxuming,20090923.MS00707,要求在密码策略中增加2个功能,QDV4交银施罗德2009年9月18日01_A
 * @author not attributable
 * @version 1.0
 */
public class UserBean
    extends BaseDataSettingBean  implements IDataSetting { //chenyibo 20071226  要把用户管理记入日志，一定要实现IDataSetting 这个接口

    private String FUserCode = "";
    private String FUserName = "";
    private String FPass = "";
    private int FPassLen;
    private int FPassCount;
    private String FPassDate = "";
    private int FLocked;
    private int FIsCancel; //注销
    private String FMenusCode = "";
    private String FMenubarsCode = "";
    private String FPortGroupCode = "";
    private String FDeptCode = "";
    private String FPositionCode = "";
    private String FDESC = "";
    private String oldUserCode = "";
    private String FDeptName = "";
    private String FPositionName = "";
    private String FuserTypeCode = ""; //by caocheng 2009.03.03 MS00001 用户类型

    private int FDayLimit;						//密码修改间隔天数
    private int FTimeLimit;						//不能相同的密码次数
    private java.util.Date FCheckDate = null;	//上次密码修改日期
    private int iPageShow = 100;//xuqiji 20100318 MS00884  QDV4赢时胜上海2009年12月21日06_B  证券应收应付数据量超过某一行数，查询报错（3月25日发）
    private int FUserID;//add by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B
    PreparedStatement pstmt=null;
    /**
     * 设置密码修改间隔天数
     * @param FDayLimit
     */
    public void setFDayLimit(int FDayLimit) {
        this.FDayLimit = FDayLimit;
    }

    /**
     * 获取密码修改间隔天数
     * @return
     */
    public int getFDayLimit() {
        return FDayLimit;
    }

    /**
     * 设置密码不能相同的次数
     * @param FTimeLimit
     */
    public void setFTimeLimit(int FTimeLimit) {
        this.FTimeLimit = FTimeLimit;
    }
    
    /**
     * 获取密码不能相同的次数
     * @return
     */
    public int getFTimeLimit() {
        return FTimeLimit;
    }
    
    /**
     * 设置密码修改日期
     * @param FCheckDate
     */
    public void setFCheckDate(Date FCheckDate) {
        this.FCheckDate = FCheckDate;
    }

    /**
     * 获取密码修改日期
     * @return
     */
    public java.util.Date getFCheckDate() {
        return FCheckDate;
    }   
    
    /**
     * 获取用户类型代码
     * @return
     */
    public String getUserTypeCode() {
    	return this.FuserTypeCode;
    }
    
   /**
     * 设置用户类型代码 
     * @param FUserTypeCode
     */
    public void setUserTypeCode(String FUserTypeCode){
    	this.FuserTypeCode = FUserTypeCode;
    }
    

    public UserBean() {
    }

    public String getListViewData() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
        	
            checkField();  //add by zhaoxianlin 20120918 story #2766
            sHeader = "用户代码\t用户名称\t部门名称\t职位名称";
            strSql = "select a.*, b.FDeptName, c.fpositionname from Tb_Sys_UserList a left join Tb_Sys_Department b on a.fdeptcode=b.fdeptcode ";
            strSql = strSql +
                " left join Tb_Sys_Position c on a.fpositioncode=c.fpositioncode ";
            strSql = strSql + " where a.FIsCancel!=1 order by a.FDeptCode,a.FPositionCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FUserCode")).append("\t");
                bufShow.append(rs.getString("FUserName")).append("\t");
                bufShow.append(rs.getString("FDeptName")).append("\t");
                bufShow.append(rs.getString("fpositionname")).append(YssCons.
                    YSS_LINESPLITMARK);

                bufAll.append(rs.getString("FUserCode")).append("\t");
                bufAll.append(rs.getString("FUserName")).append("\t");
                bufAll.append(rs.getString("FDeptCode")).append("\t");
                bufAll.append(rs.getString("FDeptName")).append("\t");
                bufAll.append(rs.getString("FPositionCode")).append("\t");
                bufAll.append(rs.getString("fpositionname")).append("\t");
                //--------------2007.11.29 修改 蒋锦----考虑使用 DB2 -----------//
                if (dbl.getDBType() == YssCons.DB_DB2) {
                    bufAll.append(" ").append("\t");
                } else if (dbl.getDBType() == YssCons.DB_ORA) {
                    bufAll.append(rs.getString("FPass")).append("\t");
                } else {
                    throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
                }
                //-----------------------------------------------------------//
                bufAll.append(rs.getString("FPassLen")).append("\t");
                bufAll.append(rs.getString("FPassCount")).append("\t");
                bufAll.append(rs.getString("FPassDate")).append("\t");
                bufAll.append(rs.getBoolean("FLocked")).append("\t");
                bufAll.append(rs.getString("FMemo")).append("\t");
                bufAll.append(rs.getString("FPortGroupCode")).append("\t");
                bufAll.append(rs.getString("FMenusCode")).append("\t");
                bufAll.append(rs.getString("FMenuBarsCode")).append("\t").append(
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
            throw new YssException("获取用户数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /*
     * add by zhaoxianlin 20120918 story #2766
     * 判断是否存在字段FIsCancel，不存在添加
     */
    private void checkField()throws YssException{
    	String sql="";
    	ResultSet rs1=null;
    	try{
    		sql="select * from Tb_Sys_UserList";
    		rs1=dbl.openResultSet(sql);
    		if(!dbl.isFieldExist(rs1, "FIsCancel")){
    			String strSql="alter table Tb_Sys_UserList add FIsCancel number(1) default 0 ";
    			dbl.openResultSet(strSql);
    		}
    	}catch(Exception e){
    		throw new YssException("更新用户表字段失败",e);
    	}finally{
    		dbl.closeResultSetFinal(rs1);
    	}
    }
    //Story #1509
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "用户代码\t用户名称\t部门名称\t职位名称";
            strSql = "select a.*, b.FDeptName, c.fpositionname from Tb_Sys_UserList a left join Tb_Sys_Department b on a.fdeptcode=b.fdeptcode ";
            strSql = strSql +
                " left join Tb_Sys_Position c on a.fpositioncode=c.fpositioncode where a.fdeptcode = 'D002'";
            strSql = strSql + " order by a.FDeptCode,a.FPositionCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FUserCode")).append("\t");
                bufShow.append(rs.getString("FUserName")).append("\t");
                bufShow.append(rs.getString("FDeptName")).append("\t");
                bufShow.append(rs.getString("fpositionname")).append(YssCons.
                    YSS_LINESPLITMARK);

                bufAll.append(rs.getString("FUserCode")).append("\t");
                bufAll.append(rs.getString("FUserName")).append("\t");
                bufAll.append(rs.getString("FDeptCode")).append("\t");
                bufAll.append(rs.getString("FDeptName")).append("\t");
                bufAll.append(rs.getString("FPositionCode")).append("\t");
                bufAll.append(rs.getString("fpositionname")).append("\t");
                //--------------2007.11.29 修改 蒋锦----考虑使用 DB2 -----------//
                if (dbl.getDBType() == YssCons.DB_DB2) {
                    bufAll.append(" ").append("\t");
                } else if (dbl.getDBType() == YssCons.DB_ORA) {
                    bufAll.append(rs.getString("FPass")).append("\t");
                } else {
                    throw new YssException("数据库访问错误。数据库类型不明，或选择了非系统兼容的数据库！");
                }
                //-----------------------------------------------------------//
                bufAll.append(rs.getString("FPassLen")).append("\t");
                bufAll.append(rs.getString("FPassCount")).append("\t");
                bufAll.append(rs.getString("FPassDate")).append("\t");
                bufAll.append(rs.getBoolean("FLocked")).append("\t");
                bufAll.append(rs.getString("FMemo")).append("\t");
                bufAll.append(rs.getString("FPortGroupCode")).append("\t");
                bufAll.append(rs.getString("FMenusCode")).append("\t");
                bufAll.append(rs.getString("FMenuBarsCode")).append("\t").append(
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
            throw new YssException("获取用户数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    public YssTrans getUserInfo(String sUserCode) throws YssException {
        YssTrans yt = new YssTrans();
        ResultSet rs = null;
        String[] tmpStrAry = null;
        String tmpStr = "";
        String strSql = "";
        StringBuffer sb = new StringBuffer();
        try {
            strSql = "select * from Tb_Sys_UserList where FUserCode = '" +
                sUserCode + "'";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {

                tmpStrAry = rs.getString("FMenusCode").split(",");
                for (int i = 0; i < tmpStrAry.length; i++) {
                    tmpStr = "'" + tmpStrAry[i] + "',";
                }
                tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
                sb.append(tmpStr).append(YssCons.YSS_LINESPLITMARK);

                tmpStrAry = rs.getString("FMenubarsCode").split(",");
                for (int i = 0; i < tmpStrAry.length; i++) {
                    tmpStr = "'" + tmpStrAry[i] + "',";
                }
                tmpStr = tmpStr.substring(0, tmpStr.length() - 1);
                sb.append(tmpStr).append(YssCons.YSS_LINESPLITMARK);
                yt.setStrAry(tmpStr.toString().split(YssCons.YSS_LINESPLITMARK));

            }
            return yt;
        } catch (Exception e) {
            throw new YssException("获取用户数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * 删除或新增用户记录
     * @param frmValue String[][]：传入二维数组，里面为需要保存的记录数据
     * @param blDelete boolean：如果为true，则表示为删除操作，否则为保存操作
     * @throws YssException
     */
    public void saveUser(String[][] frmValue, boolean blDelete) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        PreparedStatement pstmt = null; //2007.11.28 添加 蒋锦
        String errorInfo = "保存用户信息设定时出错!"; //定义错误提示信息
        String checkPass = ""; //add by huangqirong 2012-09-27 bug #5804
        
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        if (frmValue.length > 0) {
            try {
                conn.setAutoCommit(false);
                bTrans = true;
                for (int i = 0; i < frmValue.length; i++) {
                    if (!blDelete) {
                    	
                    	 /**
                         * add by huangqirong 2012-09-27 bug #5804 验证密码复杂度
                         * */
                        StringBuffer bufComplex = new StringBuffer();//保存密码复杂性验证所需要的字符
                        PassComplexBean passComplex = new PassComplexBean();
                        passComplex.setYssPub(pub);
                        passComplex.getSetting();//取出密码复杂性数据
                        
                      //20121015 deleted by liubo.Story #2904
                      //重置密码更改为以按钮形式，自动给新密码赋值为1。因此取消密码复杂度的判断
                      //*******************************************
                        
//                        if(passComplex.getiFHaveNum()>0){//密码必须包含数字
//                        	bufComplex.append("number,");
//                        }
//                        if(passComplex.getiFBigChar()>0){//密码必须包含大写字母
//                        	bufComplex.append("big_char,");
//                        }
//                        if(passComplex.getiFLowChar()>0){//密码必须包含小写字母
//                        	bufComplex.append("low_char,");
//                        }
//                        if(passComplex.getiFSpecialChar()>0){//密码必须包含特殊字符
//                        	bufComplex.append("special_char,");
//                        }
//                        bufComplex.append("");//以防buffer中没有字符
//                        checkPass = new UserRight(this.pub).regexPass(frmValue[i][2], bufComplex.toString());//直接调用已经存在的方法来判断
//                        int iLenMax;//保存密码最大长度            
//                        int iLen = passComplex.getiFLenMin();//密码最小长度
//                        iLenMax = passComplex.getiFLenMax();//密码最大长度
//                        if (iLen>0&&frmValue[i][2].length() < iLen) {//只有当大于０时才需要判断。默认为０时是不需要判断的。
//                            throw new YssException("密码位数小于规定的最小位数！");
//                        }
//                        //判断密码是否超过最大长度=========
//                        if (iLenMax>0&&frmValue[i][2].length() > iLenMax) {
//                            throw new YssException("密码位数超过规定的最大位数！");
//                        }                    
//                        //管理员在用户设置里修改用户密码时，作如下限制：１.数字、大小写字母、特殊字符；
//                        //============2.密码最大、最小长度;３.用户名密码不同；４.禁用重复字符=========
//                        if(passComplex.getiFNotSame()>0){
//                        	if(this.FUserCode.equalsIgnoreCase(frmValue[i][2])){
//                        		throw new YssException("密码不能和用户名相同！");
//                        	}
//                        }
//                        if(passComplex.getiFRepeatChar()>0){
//                        	String FNewPass = frmValue[i][2];
//                        	for(int j=0;j<FNewPass.length();j++){
//                        		char c = FNewPass.charAt(j);
//                        		int lastFlag = FNewPass.lastIndexOf(c);
//                        		if(lastFlag>=0&&lastFlag!=j){//有重复字符
//                        			throw new YssException("密码不能有重复字符！");
//                        		}
//                        	}
//                        }
//                          String strOldPass = getOldPass(FUserCode);               
//                       if(passComplex.getiFTimeLimit()>0){
//    	               //大于０说明，用户设置了需要进行些判断		
//                    		String[] reqAry = strOldPass
//    							.split(YssCons.YSS_ITEMSPLITMARK1);
//           					if (reqAry.length > 0) {// 表明有历史密码
//           						
//           						for (int j = reqAry.length - 1; j >= (reqAry.length
//           								- passComplex.getiFTimeLimit() >= 0 ? reqAry.length - passComplex.getiFTimeLimit()
//           								: 0); j--) { // 取出之前 N 次的密码，判断密码是否与前N次相同
//           							if (frmValue[i][2].equalsIgnoreCase(reqAry[j])) { // 如果与前N次中某一次相同，就不能成功修改
//           								throw new YssException("不能与前" + passComplex.getiFTimeLimit()
//           										+ "次密码相同!");	
//           							}
//           						}
//           					}
//                       }
//                       
//                       if (checkPass != null && checkPass.length() > 0) {
//                           throw new YssException("你的密码还须包含" + checkPass);
//                       }
                       /**---end---*/

                       //********************end***********************
                        
                        strSql = "select " + dbl.sqlStar("Tb_Sys_UserList") +
                            " from Tb_Sys_UserList where FUserCode = '" +
                            frmValue[i][0] + "'";
                        rs = dbl.openResultSet(strSql);
                        if (rs.next()) {
                            throw new YssException("已经存在代码为【" + frmValue[i][0] +
                                "】的用户！");
                        }
                        //增加了2个字段：“N天之内不可重复修改密码”的天数限制(FDayLimit),修改密码“不能与前N次密码相同”的次数限制(FTimeLimit)
                        //最近一次改密码的时间(FCheckDate):只有用户自己改密码时，才会更新这个字段。
                        //MS00707,要求在密码策略中增加2个功能,QDV4交银施罗德2009年9月18日01_A edit by xuxuming,20090923.
                        strSql = "insert into Tb_Sys_UserList(FUserCode, FUserName, FPass, FPassLen, FPassCount, FPassDate, FLocked,FIsCancel," + //增加了FIsCancel字段 add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
                            //edit by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B 添加FUserID
                            " FMenusCode, FMenubarsCode, FPortGroupCode, FDeptCode, FPositionCode, FMemo,FUserTypeCode,FDayLimit,FTimeLimit, FUserID)" +//xuqiji 20100318 MS00884  QDV4赢时胜上海2009年12月21日06_B  证券应收应付数据量超过某一行数，查询报错（3月25日发）
                            //edit by songjie 2011.07.18 BUG 2274 QDV4建信2011年7月14日01_B FUserID根据sequence SEQ_sys_USERID赋值
                            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, SEQ_sys_USERID.NextVal)";//xuqiji 20100318 MS00884  QDV4赢时胜上海2009年12月21日06_B  证券应收应付数据量超过某一行数，查询报错（3月25日发）
                        pstmt = conn.prepareStatement(strSql);
                        pstmt.setString(1, frmValue[i][0]);
                        pstmt.setString(2, frmValue[i][1]);
                        pstmt.setBytes(3, UserRight.encodePass(frmValue[i][2]));
                        pstmt.setInt(4, Integer.parseInt(frmValue[i][3]));
                        pstmt.setInt(5, Integer.parseInt(frmValue[i][4]));
                        pstmt.setDate(6, new java.sql.Date(YssFun.
                            parseDate(frmValue[i][5]).
                            getTime()));
                        pstmt.setInt(7, Integer.parseInt(frmValue[i][6]));
                        pstmt.setInt(8, Integer.parseInt(frmValue[i][7]));//之后赋值序号相应作了修改以匹配相应字段add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
                        pstmt.setString(9, " ");
                        pstmt.setString(10, " ");
                        pstmt.setString(11, frmValue[i][10]);
                        pstmt.setString(12, frmValue[i][11]);
                        pstmt.setString(13, frmValue[i][12]);
                        pstmt.setString(14, frmValue[i][13]);
                        pstmt.setString(15, frmValue[i][16]); // by caocheng 2009.03.03 MS00001
                        //==add by xuxuming,20090923.MS00707,要求在密码策略中增加2个功能,QDV4交银施罗德2009年9月18日01_A
                        pstmt.setInt(16, Integer.parseInt(frmValue[i][17]));//“N天之内不可重复修改密码”的天数限制(FDayLimit)
                        pstmt.setInt(17, Integer.parseInt(frmValue[i][18]));//修改密码“不能与前N次密码相同”的次数限制(FTimeLimit)
                       //========end================================================================================
                       // pstmt.setInt(17,Integer.parseInt(frmValue[i][18]));//xuqiji 20100318 MS00884  QDV4赢时胜上海2009年12月21日06_B  证券应收应付数据量超过某一行数，查询报错（3月25日发）
                        pstmt.executeUpdate();
                       
                        strSql = "update Tb_Sys_UserList set FMenusCode = " +
                            dbl.sqlString(frmValue[i][8]) + ", FMenubarsCode = " +
                            dbl.sqlString(frmValue[i][9]) +
                            " where FUserCode = " + dbl.sqlString(frmValue[i][0]);
                        dbl.executeSql(strSql);
                    } else {
                        //--------判断用户是否为系统唯一管理员 by caocheng 2009.03.08 MS00001 QDV4.1----//
                        String usercode = frmValue[i][0];
                        if (beforeDelete(usercode)) {
                            throw new YssException("您要删除的用户'" + usercode +
                                "'是系统唯一管理员用户,不可删除!");
                        }
                        //------------------------------------------------------------------//
                        strSql = "delete from Tb_Sys_UserList where FUserCode='" +
                            frmValue[i][0] + "'";
                        dbl.executeSql(strSql);

                        //同时删除用户权限等相关信息
                        strSql = "delete from Tb_Sys_UserRight where FUserCode='" +
                            frmValue[i][0] + "'";
                        dbl.executeSql(strSql);
                        //==add by xuxuming,20090923.MS00707,要求在密码策略中增加2个功能,QDV4交银施罗德2009年9月18日01_A
                        //同时删除用户历史密码等相关信息
                        strSql = "delete from "+ pub.yssGetTableName("Tb_Sys_UserOldPass") +" where FUserCode='" +
                            frmValue[i][0] + "'";
                        dbl.executeSql(strSql);
                        //===end===================================================================================
                    }
                }
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } catch (SQLException se) {
                throw new YssException(errorInfo, se);
            } finally {
                dbl.endTransFinal(conn, bTrans);
                dbl.closeResultSetFinal(rs);
            }

        }
    }

    /**
     * beforeDelete
     *删除用户前检查数据有效性,如果是最后一个管理员禁止删除
     * by caocheng 2009.03.03 MS00001 《QDV4.1赢时胜上海2009年2月1日01_A》
     * @param usercode String
     */
    private boolean beforeDelete(String usercode) throws YssException,
        YssException {
        ResultSet rs = null;
        boolean bOne = true; //用来标识是否为唯一的管理员，默认是唯一管理员
        StringBuffer bufSql = new StringBuffer();
        //查询所有管理员个数
        bufSql.append("select count(*) as managerCount from")
            .append(" (select fusertypecode FROM TB_SYS_USERLIST WHERE FUSERCODE=")
            .append(dbl.sqlString(usercode))
            .append(" and fusertypecode='1') a")
            .append(" join tb_sys_userlist b on a.fusertypecode = b.fusertypecode");
        try {
            rs = dbl.openResultSet(bufSql.toString()); //因为前台传来的字段不包括用户类型,所以根据用户代码获取用户类型
            if (rs.next()) {
                //如果个数等于1,是唯一的管理员,不可以可以删除,返回true
                if (rs.getInt("managerCount") == 1) {
                    bOne = true;
                } else {
                    bOne = false; //用户不是唯一管理员(有多个管理员或者此用户不是管理员)可以删除
                }
            } else {
                throw new YssException("用户不存在或已被删除！");
            }
        } catch (SQLException ex) {
            throw new YssException("检查用户是否为系统唯一管理员出错!" + "\n", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bOne;
    }

    /**
     * 修改用户信息
     * @param frmValue String[][]
     * @throws YssException
     */
    public void updateUser(String[][] frmValue) throws
        YssException {
        String strSql = "", checkPass = "";
        ResultSet rs = null;
        UserRight ur = new UserRight(pub);
        String errorInfo = "修改用户信息时出错！"; //定义错误提示信息
        Boolean ResetPwChange = false;     //密码重置后更新历史表，默认为false；
        Connection conn = dbl.loadConnection();
        boolean bTrans = true; //代表是否回滚事物
        //-----------------判断用户类型 by caocheng 2009.03.20 MS00001 QDV4.1--------------------//
        boolean IsOnlyAdmin = this.beforeDelete(this.FUserCode); //判断用户是否为系统唯一管理员
        if (IsOnlyAdmin) { //用户是系统唯一管理员,判断修改后用户类型是否仍为管理员
            for (int i = 0; i < frmValue.length; i++) {
                if (!frmValue[i][16].equals("1")) { //当用户是唯一管理员是,撤销其管理员权限报错//modified by yeshenghong bug7275 20130308
                    throw new YssException("修改的用户为系统唯一管理员,不能取消管理员权限!");
                }
            }
        }
        //----------------------------------------------------------------------------------//
        if (frmValue.length > 0) {
            try {
                conn.setAutoCommit(false);
                for (int i = 0; i < frmValue.length; i++) {
                    ////在用户设置的修改用户状态下的密码修改，添加安全性的判断 linjunyun 2008-11-17 bug:Ms00016
//                    checkPass = ur.regexPass(frmValue[i][2], ur.getPassLevel());
                	//=====add by xuxuming,20091110.MS00776.从密码复杂性表中取数据，以此来作以下验证==
                    StringBuffer bufComplex = new StringBuffer();//保存密码复杂性验证所需要的字符
                    PassComplexBean passComplex = new PassComplexBean();
                    passComplex.setYssPub(pub);
                    passComplex.getSetting();//取出密码复杂性数据
                    
                    //20121015 deleted by liubo.Story #2904
                    //重置密码更改为以按钮形式，自动给新密码赋值为1。因此取消密码复杂度的判断
                    //*******************************************
                    
//                    if(passComplex.getiFHaveNum()>0){//密码必须包含数字
//                    	bufComplex.append("number,");
//                    }
//                    if(passComplex.getiFBigChar()>0){//密码必须包含大写字母
//                    	bufComplex.append("big_char,");
//                    }
//                    if(passComplex.getiFLowChar()>0){//密码必须包含小写字母
//                    	bufComplex.append("low_char,");
//                    }
//                    if(passComplex.getiFSpecialChar()>0){//密码必须包含特殊字符
//                    	bufComplex.append("special_char,");
//                    }
//                    bufComplex.append("");//以防buffer中没有字符
//                    checkPass = ur.regexPass(frmValue[i][2], bufComplex.toString());//直接调用已经存在的方法来判断
//                    int iLenMax;//保存密码最大长度            
//                    int iLen = passComplex.getiFLenMin();//密码最小长度
//                    iLenMax = passComplex.getiFLenMax();//密码最大长度
//                    if (iLen>0&&frmValue[i][2].length() < iLen) {//edit by xuxuming,只有当大于０时才需要判断。默认为０时是不需要判断的。
//                        throw new YssException("密码位数小于规定的最小位数！");
//                    }
//                    //====add by xuxuming,20091110.MS00776.判断密码是否超过最大长度=========
//                    if (iLenMax>0&&frmValue[i][2].length() > iLenMax) {
//                        throw new YssException("密码位数超过规定的最大位数！");
//                    }
//                    //========end=================================
//                    //===add by xuxuming,20091120.管理员在用户设置里修改用户密码时，作如下限制：１.数字、大小写字母、特殊字符；
//                    //============2.密码最大、最小长度;３.用户名密码不同；４.禁用重复字符=========
//                    if(passComplex.getiFNotSame()>0){
//                    	if(this.FUserCode.equalsIgnoreCase(frmValue[i][2])){
//                    		throw new YssException("密码不能和用户名相同！");
//                    	}
//                    }
//                    if(passComplex.getiFRepeatChar()>0){
//                    	String FNewPass = frmValue[i][2];
//                    	for(int j=0;j<FNewPass.length();j++){
//                    		char c = FNewPass.charAt(j);
//                    		int lastFlag = FNewPass.lastIndexOf(c);
//                    		if(lastFlag>=0&&lastFlag!=j){//有重复字符
//                    			throw new YssException("密码不能有重复字符！");
//                    		}
//                    	}
//                    }
//                      String strOldPass = getOldPass(FUserCode);               
//                   if(passComplex.getiFTimeLimit()>0){
//	               //大于０说明，用户设置了需要进行些判断		
//                		String[] reqAry = strOldPass
//							.split(YssCons.YSS_ITEMSPLITMARK1);
//       					if (reqAry.length > 0) {// 表明有历史密码
//       						
//       						for (int j = reqAry.length - 1; j >= (reqAry.length
//       								- passComplex.getiFTimeLimit() >= 0 ? reqAry.length - passComplex.getiFTimeLimit()
//       								: 0); j--) { // 取出之前 N 次的密码，判断密码是否与前N次相同
//       							if (frmValue[i][2].equalsIgnoreCase(reqAry[j])) { // 如果与前N次中某一次相同，就不能成功修改
//       								throw new YssException("不能与前" + passComplex.getiFTimeLimit()
//       										+ "次密码相同!");	
//       						}
//       					}
//       				}
//                	   
//                	   
//                }
                    

               //*******************end************************
               //add by huangqirong 2012-11-13 bug #6223
               boolean isAlter = false;               
               if(!dbl.isFieldExist(dbl.openResultSet("select * from Tb_Sys_UserList where 1=0"), "FalreadyReset")){ //不存在字段则增加
            	   dbl.executeSql("alter table Tb_Sys_UserList add FalreadyReset NUMBER(1) default 0");
               }
               isAlter = frmValue[i][19].equalsIgnoreCase("true") ? true : false ;
                    
               byte[] bPass;
               bPass =UserRight.encodePass(FPass);
               strSql = "update " + pub.yssGetTableName("Tb_Sys_UserList") + " set FPass = ?,FCheckDate = ? "+(isAlter ? ",FalreadyReset = 1 " : "")+" where FUserCode = '" + //modify by huangqirong 2012-11-13 bug #6223 增加是否重置密码
               FUserCode + "'";//此处更新密码 和 修改密码的时间 两个字段。
               pstmt = conn.prepareStatement(strSql);
               pstmt.setBytes(1, bPass);
               pstmt.setDate(2, new java.sql.Date(new java.util.Date().getTime()));//将当前日期作为修改密码的时间
               pstmt.executeUpdate();
               
               if (frmValue[i][19].equalsIgnoreCase("true"))

                   //===============end=================
                   {
                	   String SqlStr = "";
                       ResetPwChange=true;
                       try {
                      	 //管理员重置密码后，清除客户密码历史及登录错误次数等信息，全部从零计数
                    	   SqlStr = "delete from  " + pub.yssGetTableName("Tb_Sys_UserOldPass") +" where FUserCode = '"+this.FUserCode+"'";
                      	 dbl.executeSql(SqlStr); 
                      	 conn.commit();
                           bTrans = false;
                           conn.setAutoCommit(true);
                       } catch (Exception se) {
                           throw new YssException("密码重置后清零所有密码记录出错！", se);
                       } finally {
                      	 dbl.endTransFinal(conn, bTrans);
                       }
                   }
                   //-----------end-----------
            	   //系统新增加了一张表，用来保存用户的历史密码。编号为主键
                   strSql = "insert into " + pub.yssGetTableName("Tb_Sys_UserOldPass") + "(FNUM,FUserCode,FOldPass) " +
                       " values(?,?,?)";
                   
                   String sNum = "D" +
                           YssFun.formatDate(new java.util.Date(), "yyyyMMdd") +
                           dbFun.getNextInnerCode("Tb_Sys_UserOldPass", dbl.sqlRight("FNUM", 6), "000001");//自动生成编号，不能重复

                   pstmt = conn.prepareStatement(strSql);
                   pstmt.setString(1, sNum);
                   pstmt.setString(2, FUserCode);
                   pstmt.setBytes(3, bPass);
                   pstmt.executeUpdate();
                   
                   conn.commit();
                   bTrans = false;
                   conn.setAutoCommit(true);  
                   // ---------------add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
                   getConstant(); //重置密码后，在清空历史密码之前将此次修改密码的动作反应到常量中，以供登录时判断用户是否修改了密码

                   //20121015 modified by liubo.Story #2904
                   //清空密码历史密码记录，判断条件更改为是否有重置密码。
                   //================================
//                   if(passComplex.getiFResetPwdChange()>0){
                   //-----------end-----------

                    //==========end,密码复杂性验证完毕==============================================
                    if (checkPass != null && checkPass.length() > 0) {
                        throw new YssException("你的密码还须包含" + checkPass);
                    }
					//edit by songjie 2011.09.26 BUG 2644 QDV4兴业银行2011年9月05日05_B
                    if (!frmValue[i][0].trim().equals(frmValue[i][15].trim())) {
                        strSql = "select " + dbl.sqlStar("Tb_Sys_UserList") +
                            " from Tb_Sys_UserList where FUserCode = '" +
                            frmValue[i][0].trim() + "'";
                        rs = dbl.openResultSet(strSql);
                        if (rs.next()) {
                            throw new YssException("已经存在代码为【" + frmValue[i][0].trim() +
                                "】的用户！");
                        }
                    }
                    strSql = "select " + dbl.sqlStar("Tb_Sys_UserList") +
                        " from Tb_Sys_UserList where FUserCode='" +
                        //edit by songjie 2011.10.09 修改用户设置无效
                        frmValue[i][15].trim() + "'";
                    rs = dbl.openResultSet(strSql, true);
                    if (rs.next()) {
                    	

                    	
                    	/************************************************************************************
                         * #1205 由于密码复杂度设置，用户长期不登陆，登陆后会被锁  
                         *  add by jiangshichao 2011.03.24
                         *  
                         * 系统通过判断当前系统日期和用户上次登录系统的间隔天数与设定锁定日期天数进行比较，超过则锁定。
                         * 管理员给用户解锁的时候，应该把登录时间给更新为9998-12-31，否则即使解锁了，
                         * 被锁定用户还是登录不了系统。
                         */

                        //20121015 deleted by liubo.Story #2904
                        //重置密码更改为以按钮形式，自动给新密码赋值为1。因此取消密码复杂度的判断
                        //===========================================
//                    	if(passComplex.getiFLockLimit()>0){//判断是否设置了锁定日期
//                    		if(YssFun.dateDiff(rs.getDate("FLASTLOGINDATE"),YssFun.toSqlDate(new Date()))- passComplex.getiFLockLimit() > 0 
//                             	   &&rs.getInt("FLocked")==1 ){//判断用户是否因超过日期，而被锁定。
//                    			if(Integer.parseInt(frmValue[i][6])==0){//判断是进行解锁操作
//                    				rs.updateDate("FLASTLOGINDATE", YssFun.toSqlDate("9998-12-31"));//对最后登录日期进行赋值。在下次被锁定登录时保存的是用户的登录时间
//                    			}
//                    		}
//                    	}

                        //==================end=========================
                    	
                        rs.updateString("FUserCode", frmValue[i][0]);
                        rs.updateString("FUserName", frmValue[i][1]);
                        rs.updateBytes("FPass", UserRight.encodePass(frmValue[i][2]));
                        rs.updateInt("FPassLen", Integer.parseInt(frmValue[i][3]));
                        rs.updateInt("FPassCount", Integer.parseInt(frmValue[i][4]));
                        rs.updateDate("FPassDate",
                                      YssFun.toSqlDate(YssFun.toDate(frmValue[i][5])));
                        rs.updateInt("FLocked", Integer.parseInt(frmValue[i][6]));
                        rs.updateInt("FIsCancel", Integer.parseInt(frmValue[i][7]));//之后字段赋值序号相应+1做修改add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
                        rs.updateString("FMenusCode", " ");
                        rs.updateString("FMenubarsCode", " ");
                        rs.updateString("FPortGroupCode", frmValue[i][10]);
                        rs.updateString("FDeptCode", frmValue[i][11]);
                        rs.updateString("FPositionCode", frmValue[i][12]);
                        rs.updateString("FMemo", frmValue[i][13]);
                        rs.updateString("FuserTypeCode", frmValue[i][16]);
                        rs.updateInt("FDayLimit", Integer.parseInt(frmValue[i][17]));//“N天之内不可重复修改密码”的天数限制(FDayLimit)
                        rs.updateInt("FTimeLimit", Integer.parseInt(frmValue[i][18]));//修改密码“不能与前N次密码相同”的次数限制(FTimeLimit)
                      //  rs.updateInt("FPageShow",Integer.parseInt(frmValue[i][18]));//xuqiji 20100318 MS00884  QDV4赢时胜上海2009年12月21日06_B  证券应收应付数据量超过某一行数，查询报错（3月25日发）
                        rs.updateRow();

                        strSql = "update Tb_Sys_UserList set FMenusCode = " +
                            dbl.sqlString(frmValue[i][8]) + ", FMenubarsCode = " +
                            dbl.sqlString(frmValue[i][9]) +
                            " where FUserCode = " + dbl.sqlString(frmValue[i][0]);
                        dbl.executeSql(strSql);
                    }
                }
                conn.commit();
                bTrans = false;
                conn.setAutoCommit(true);
            } catch (SQLException se) {
                throw new YssException(errorInfo, se);
            } finally {
            	dbl.endTransFinal(bTrans);
            }
        }
    }

    //==add by xuxuming,20090923.MS00707,要求在密码策略中增加2个功能,QDV4交银施罗德2009年9月18日01_A
    /**
     * 根据用户代码获取用户实体
     * @param userCode String
     * @throws YssException
     * @return UserBean
     */
    public UserBean getUserById(String userCode) throws YssException{
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "select * from " + pub.yssGetTableName("Tb_Sys_UserList") +
                " a where a.FUserCode=" + dbl.sqlString(userCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.FUserCode = rs.getString("FUserCode");
                this.FuserTypeCode = rs.getString("FUserTypeCode");
                this.FDayLimit = rs.getInt("FDayLimit");
                this.FTimeLimit = rs.getInt("FTimeLimit");
                this.FCheckDate = rs.getDate("FCheckDate");
            }
            return this;
        } catch (SQLException se) {
            throw new YssException("获取用户实体信息出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /*
     * 将此次修改密码的动作反应到YssCons.YSS_USER_OLDPASS_COUNT常量中
     */
    private void getConstant()throws YssException{
        ResultSet rs = null;
        String strSql = "";
        try {
            strSql = "select count(*) from Tb_Sys_UserOldPass where FuserCode='"+this.FUserCode+"'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                YssCons.YSS_USER_OLDPASS_COUNT = 1;
            }
        } catch (SQLException se) {
            throw new YssException("获取用户实体信息出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
//获取原来的密码
    public String getOldPass(String userCode) throws YssException {
    	
        ResultSet rs = null;
        String strSql = "";
        String strReturn = "";
        try {
            strSql = "select FOldPass from " + pub.yssGetTableName("Tb_Sys_UserOldPass") +
                " a where a.FUserCode='" + userCode + "'";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                strReturn += UserRight.decodePass(rs.getBytes("FOldPass"))+YssCons.YSS_ITEMSPLITMARK1;// 历史密码用 '\t'隔开
            }
            return strReturn;
        } catch (SQLException se) {
            throw new YssException("获取用户历史密码出错！", se);
        } finally {
        	dbl.closeResultSetFinal(rs);
        }

    }
    /**
     * 获得用户的记录
     * @param userCode String：如果userCode不为空，则返回指定的用户记录
     * @throws YssException
     * @return String
     */
    public String getUser(String userCode) throws YssException {
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        ResultSet rsIsCancel = null;
        String strSql = "", strReturn = "";
        try {
            strSql =
                "select a.*,b.FDeptname,c.FPositionname from Tb_Sys_userlist a ";
            strSql = strSql + " left join (select fdeptcode,fdeptname from Tb_Sys_Department) b on a.fdeptcode=b.fdeptcode";
            strSql = strSql + " left join (select fpositionname,fpositioncode from Tb_Sys_Position) c on a.fpositioncode=c.fpositioncode";
            
            if (userCode != null && userCode.length() > 0) {
                strSql = strSql + " where a.FUserCode='" + userCode + "'";
            }
            strSql = strSql + " order by a.FUserCode,a.FDeptCode";
            
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            
            while (rs.next()) {
                buf.append(rs.getString("FUserCode"));
                buf.append("\t");
                buf.append(rs.getString("FUserName"));
                buf.append("\t");
                buf.append(rs.getString("FDeptCode") != null ?
                           rs.getString("FDeptCode") : "");
                buf.append("\t");
                buf.append(rs.getString("FDeptName") != null ?
                           rs.getString("FDeptName") : "");
                buf.append("\t");
                buf.append(rs.getString("FPositionCode") != null ?
                           rs.getString("FPositionCode") : "");
                buf.append("\t");
                buf.append(rs.getString("FPositionName") != null ?
                           rs.getString("FPositionName") : "");
                buf.append("\t");
                if (userCode != null && userCode.length() > 0) {
                    buf.append(UserRight.decodePass(rs.getBytes("FPass")));
                    buf.append("\t");
                    buf.append(rs.getString("FPassLen"));
                    buf.append("\t");
                    buf.append(rs.getString("FPassCount"));
                    buf.append("\t");
                    //==============add by yangheng MS01621 QDV4赢时胜（深圳）2010年8月20日01_B 20100827
                    //buf.append(rs.getString("FPassDate"));
                    buf.append(rs.getDate("FPassDate"));
                    //============
                    buf.append("\t");
                    buf.append(rs.getString("Flocked"));
                    buf.append("\t");
                    buf.append(rs.getString("FMemo"));
                    buf.append("\t");
                    buf.append(rs.getString("FPortGroupCode"));
                    buf.append("\t");
                    buf.append(rs.getString("FMenusCode"));
                    buf.append("\t");
                    buf.append(rs.getString("FMenubarsCode"));
                    buf.append("\t");
                    buf.append(rs.getString("FUsertypeCode"));
                    buf.append("\t");
                  //==add by xuxuming,20090923.MS00707,要求在密码策略中增加2个功能,QDV4交银施罗德2009年9月18日01_A
                  //此处增加两个字段：密码间隔天数；不同于前N次
                    buf.append(rs.getString("FDayLimit")!=null?
                    		rs.getString("FDayLimit"):"0");
                    buf.append("\t");
                    buf.append(rs.getString("FTimeLimit")!=null?
                    		rs.getString("FTimeLimit"):"3");
                    buf.append("\t");
                  //===end=============================================  
                    //xuqiji 20100318 MS00884  QDV4赢时胜上海2009年12月21日06_B  证券应收应付数据量超过某一行数，查询报错（3月25日发）
                    if(dbl.isFieldExist(rs,"FPageShow")){
                    	buf.append(rs.getInt("FPageShow")).append("\t");
                    }
                    //--------------------------end 20100318--------------------//
                    //当数据库“系统用户信息设置表存在“FIsCancel”字段时，获得该记录；字段不存在时就不取
        			//********//add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
                    rsIsCancel = dbl.getUserTabColumns(pub.yssGetTableName("Tb_Sys_userlist"), "FIsCancel");
                    rsIsCancel.last();
        			if (rsIsCancel.getRow() > 0)
        			{
        				buf.append(rs.getString("FIsCancel"));  
                        buf.append("\t");
        			}
        			//*******************end**************************
                    
        			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        			dbl.closeResultSetFinal(rsIsCancel);
        			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
        			
                    buf.append(YssCons.YSS_LINESPLITMARK);
                }
                buf.append(YssCons.YSS_LINESPLITMARK); //行间用crlf间隔
            }
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }
            
            //------ add by wangzuochun 2010.04.19  MS00977    登陆系统时，当“组合群代码”和“用户代码”输入错误时报错    QDV4赢时胜（测试）2010年04月14日03_B
            if (buf.toString().length() == 0) {
                throw new Exception();
            }
            //--------MS00977--------//

            return strReturn;
        } catch (SQLException se) {
            throw new YssException("获取用户信息出错！", se); //注意这里抛出异常的方式
        } 
        //------ add by wangzuochun 2010.04.19  MS00977    登陆系统时，当“组合群代码”和“用户代码”输入错误时报错    QDV4赢时胜（测试）2010年04月14日03_B  
        catch (Exception e) {
            throw new YssException("用户不存在！", e); //注意这里抛出异常的方式
        } 
        //------------------MS00977-------------------//
        finally {
        	//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbl.closeResultSetFinal(rs,rsIsCancel);
            //--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
        }
    }

    /**
     * 获得用户的菜单的记录
     * @param type String：
     * @throws YssException
     * @return String
     */
    public String getData_menu(String type) throws YssException {
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        String strSql = "", strReturn = "";
        try {
            strSql = "select * from " + type + " order by fordercode";
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (type.equalsIgnoreCase("Tb_Fun_Menu")) {
                while (rs.next()) {
                    buf.append(rs.getString("FMenuCode"));
                    buf.append("\t");
                    buf.append(rs.getString("FMenuName"));
                    buf.append("\t");
                    buf.append(rs.getString("FParentCode"));
                    buf.append("\t");
                    buf.append(rs.getString("FIconPath"));
                    buf.append("\t");
                    buf.append(rs.getString("FEnabled"));
                    buf.append("\t");
                    buf.append(rs.getString("FCheck"));
                    buf.append("\t");
                    buf.append(rs.getString("FShortCutKey"));
                    buf.append(YssCons.YSS_LINESPLITMARK); //行间用crlf间隔
                }
            } else {
                while (rs.next()) {
                    buf.append(rs.getString("FBarCode"));
                    buf.append("\t");
                    buf.append(rs.getString("FBarName"));
                    buf.append("\t");
                    buf.append(rs.getString("FBarGroupCode"));
                    buf.append("\t");
                    buf.append(rs.getString("FIconPath"));
                    buf.append("\t");
                    buf.append(rs.getString("FEnabled"));
                    buf.append(YssCons.YSS_LINESPLITMARK);
                }
            }
            buf.append(YssCons.YSS_LINESPLITMARK);
            if (buf.toString().length() > 2) {
                strReturn = buf.toString().substring(0, buf.toString().length() - 2);
            }

            return strReturn;

        } catch (SQLException se) {
            throw new YssException("获取用户信息出错！", se); //注意这里抛出异常的方式
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * by caocheng 2009.03.02 MS00001 QDV4.1
     * 从词汇表中获取用户类型记录
     * @return String
     */
    public String getUserType() throws YssException {
        String sql =
            "select * from Tb_fun_vocabulary where FVocTypeCode='FuserType'";
        StringBuffer buf = new StringBuffer();
        ResultSet rs = null;
        try {
            rs = dbl.openResultSet(sql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                buf.append(rs.getString("FVocCode")); //获取用户类型代码
                buf.append(rs.getString("FVocName")); //获取用户类型名称
                buf.append(YssCons.YSS_LINESPLITMARK);
            }
            return buf.toString();
        } catch (SQLException ex) {
            throw new YssException("获取用户类型信息出错!\n", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }


    /**
     * getListViewData2
     *
     * @return String
     */
    public String getListViewData2() {
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
     * getListViewData4
     *
     * @return String
     */
    public String getListViewData4() {
        return "";
    }

    /**
     * addSetting
     *
     * @return String
     */
    public String addSetting() {
        return "";
    }

    /**
     * checkInput
     *
     * @param btOper byte
     */
    public void checkInput(byte btOper) {
    }

    /**
     * checkSetting
     */
    public void checkSetting() {
    }

    /**
     * delSetting
     */
    public void delSetting() {
    }

    /**
     * editSetting
     *
     * @return String
     */
    public String editSetting() {
        return "";
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
     * getSetting
     *
     * @return IDataSetting
     */
    public IDataSetting getSetting() {
        return null;
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
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() throws YssException {
        RightBean rb = new RightBean();
        try {
            rb.setYssPub(pub);

            StringBuffer buffer = new StringBuffer();
            buffer.append(this.FUserCode).append("\t");
            buffer.append(this.FUserName).append("\t");
            buffer.append( (this.FDeptCode == null ? "" : this.FDeptCode)).append(
                "\t");
            buffer.append( (this.FDeptName == null ? "" : this.FDeptName)).append(
                "\t");
            buffer.append( (this.FPositionCode == null ? "" : this.FPositionCode)).
                append("\t");
            buffer.append( (this.FPositionName == null ? "" : this.FPositionName)).
                append("\t");
            buffer.append(this.FPass).append("\t");
            buffer.append(this.FPassLen).append("\t");
            buffer.append(this.FPassCount).append("\t");
            buffer.append(this.FPassDate).append("\t");
            buffer.append(this.FLocked).append("\t");
            buffer.append(this.FDESC).append("\t");
            buffer.append(this.FPortGroupCode).append("\t");
            buffer.append(this.FMenusCode).append("\t");
            buffer.append(this.FMenubarsCode).append("\t");
            buffer.append(this.FuserTypeCode).append("\t"); // by caocheng 2009.03.03 MS00001
            return buffer.toString();
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    /**
     * getOperValue
     *
     * @param sType String
     * @return String
     */
    public String getOperValue(String sType) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select a.* ,b.FDeptname,c.FPositionname from Tb_Sys_userlist a ";
            strSql = strSql + " left join (select fdeptcode,fdeptname from Tb_Sys_Department) b on a.fdeptcode=b.fdeptcode";
            strSql = strSql + " left join (select fpositionname,fpositioncode from Tb_Sys_Position) c on a.fpositioncode=c.fpositioncode";
            strSql = strSql + " where a.FUserCode=" + dbl.sqlString(this.FUserCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                this.FUserCode = rs.getString("FUserCode");
                this.FUserName = rs.getString("FUserName");
                this.FPass = rs.getString("FPass");
                this.FPassLen = rs.getInt("FPassLen");
                this.FPassCount = rs.getInt("FPassCount");
                this.FPassDate = YssFun.formatDate(rs.getDate("FPassDate"));
                this.FLocked = rs.getInt("FLocked");
                this.FMenusCode = rs.getString("FMenusCode");
                this.FMenubarsCode = rs.getString("FMenubarsCode");
                this.FPortGroupCode = rs.getString("FPortGroupCode");
                this.FDeptCode = rs.getString("FDeptCode");
                this.FDeptName = rs.getString("FDeptName");
                this.FPositionCode = rs.getString("FPositionCode");
                this.FPositionName = rs.getString("FPositionName");
                this.iPageShow = rs.getInt("FPageShow");//xuqiji 20100318 MS00884  QDV4赢时胜上海2009年12月21日06_B  证券应收应付数据量超过某一行数，查询报错（3月25日发）
            }
            return null;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) throws YssException {

        try {
            String reqAry[] = null;

            reqAry = sRowStr.split("\t");
            this.FUserCode = reqAry[0];
            if (reqAry.length > 10) {
                this.FUserName = reqAry[1];
                this.FPass = reqAry[2];
                this.FPassLen = Integer.parseInt(reqAry[3]);
                this.FPassCount = Integer.parseInt(reqAry[4]);
                this.FPassDate = reqAry[5];
                this.FLocked = Integer.parseInt(reqAry[6]);
                this.FIsCancel=Integer.parseInt(reqAry[7]);//add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
                this.FMenusCode = reqAry[8];
                this.FMenubarsCode = reqAry[9];
                this.FPortGroupCode = reqAry[10];
                this.FDeptCode = reqAry[11];
                this.FPositionCode = reqAry[12];
                this.FDESC = reqAry[13];
                this.oldUserCode = reqAry[15];
                this.FuserTypeCode = reqAry[16]; //获取用户类型 by caocheng MS0001 QDV4.1
            }
        } catch (Exception e) {
            throw new YssException("解析请求信息出错", e);
        }

    }

    /**
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() throws YssException {
        UserBean befEditBean = new UserBean();
        befEditBean.setYssPub(pub);
        String strSql = "";
        ResultSet rs = null;
        try {

            strSql =
                "select a.* ,b.FDeptname,c.FPositionname from Tb_Sys_userlist a ";
            strSql = strSql + " left join (select fdeptcode,fdeptname from Tb_Sys_Department) b on a.fdeptcode=b.fdeptcode";
            strSql = strSql + " left join (select fpositionname,fpositioncode from Tb_Sys_Position) c on a.fpositioncode=c.fpositioncode";
            strSql = strSql + " where a.FUserCode=" +
                dbl.sqlString(this.oldUserCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                befEditBean.FUserCode = rs.getString("FUserCode");
                befEditBean.FUserName = rs.getString("FUserName");
                befEditBean.FPass = rs.getString("FPass");
                befEditBean.FPassLen = rs.getInt("FPassLen");
                befEditBean.FPassCount = rs.getInt("FPassCount");
                befEditBean.FPassDate = YssFun.formatDate(rs.getDate("FPassDate"));
                befEditBean.FLocked = rs.getInt("FLocked");
                befEditBean.FMenusCode = rs.getString("FMenusCode");
                befEditBean.FMenubarsCode = rs.getString("FMenubarsCode");
                befEditBean.FPortGroupCode = rs.getString("FPortGroupCode");
                befEditBean.FDeptCode = rs.getString("FDeptCode");
                befEditBean.FDeptName = rs.getString("FDeptName");
                befEditBean.FPositionCode = rs.getString("FPositionCode");
                befEditBean.FPositionName = rs.getString("FPositionName");
                befEditBean.FuserTypeCode = rs.getString("FuserTypeCode"); //by caocheng 2009.03.03 MS00001
            }

            return befEditBean.buildRowStr();
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
    }

    /**
     * 更新密码有效时间和安全性 2008-11-12 linjunyun 修改 Bug:MS00016
     * @param checkPass String
     * @throws YssException
     */
    public void updatePassLevelAndTime(String checkPass) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errorInfo = "更新密码有效时间和安全性时出错！"; //定义错误提示信息
        String[] PassLevelAndTime = checkPass.split("\r\f");

        Connection conn = dbl.loadConnection();
        if (PassLevelAndTime.length > 0) {
            try {
                conn.setAutoCommit(false);
                strSql = "select " + dbl.sqlStar("Tb_Sys_UserList") +
                    " from Tb_Sys_UserList";
                rs = dbl.openResultSet(strSql, true);
                while (rs.next()) {
                    rs.updateString("FValidTime", PassLevelAndTime[0]);
                    if (PassLevelAndTime.length > 1 &&
                        !"".equalsIgnoreCase(PassLevelAndTime[1])) {
                        rs.updateString("FPassLevel", PassLevelAndTime[1]);
                    } else {
                        rs.updateString("FPassLevel", null);
                    }
                    rs.updateRow();
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException se) {
                throw new YssException(errorInfo, se);
            } finally {
                dbl.closeResultSetFinal(rs);
            }
        }
    }

    /**
     * 查询密码有效时间和安全性 2008-11-12 linjunyun 修改 Bug:MS00016
     * @param userCode String
     * @return String
     * @throws YssException
     */
    public String getPassLevelAndTime(String userCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String errorInfo = "查询密码有效时间和安全性时出错！"; //定义错误提示信息
        StringBuffer buf = new StringBuffer();

        try {
            strSql = "select " + dbl.sqlStar("Tb_Sys_UserList") +
                " from Tb_Sys_UserList where FUserCode = '" + userCode + "'";
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                buf.append(rs.getString("FValidTime"));
                buf.append("\r\f");
                buf.append(rs.getString("FPassLevel") != null ?
                           rs.getString("FPassLevel") : "");
            }
        } catch (SQLException se) {
            throw new YssException(errorInfo, se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return buf.toString();
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

    public String getTreeViewGroupData1() throws YssException {
        return "";
    }

    public String getTreeViewGroupData2() throws YssException {
        return "";
    }

    public String getTreeViewGroupData3() throws YssException {
        return "";
    }

	public void saveUserLoginDate(String strUserCode) throws YssException{
		PreparedStatement pstmt = null; 
        Connection conn = dbl.loadConnection();
        boolean bTrans = false;        
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
		  //下面开始修改数据库中密码
        strSql = "update " + pub.yssGetTableName("Tb_Sys_UserList") + " set FLastLoginDate = ? where FUserCode = '" +
        strUserCode + "'";//此处更新登录时间 。
        pstmt = conn.prepareStatement(strSql);        
        pstmt.setDate(1, new java.sql.Date(new java.util.Date().getTime()));//将当前日期作为登录时间
        pstmt.executeUpdate();
        conn.commit();
        bTrans = false;
        conn.setAutoCommit(true);        
    } catch (SQLException sqle) {
        throw new YssException("更新登录时间出错！", sqle);
    } finally {
        //dbLink.closeResultSetFinal(rs);
    	dbl.closeStatementFinal(pstmt);//关闭命令行语句 by leeyu 20100909
        dbl.endTransFinal(conn, bTrans);
    }
	}

	/**MS01536    在系统设置中增加一【在线用户管理】模块    QDV4赢时胜上海2010年07月30日01_A  
	 * 2010.09.29
	 * @方法名：parseLog
	 * @参数：
	 * @返回类型：void
	 * @说明：用来初始化在线用户管理的日志信息，
	 */
	public void parseLog() {
		super.parseRecLog();
		
	}

	/**shashijie 2012-5-28 STORY 2620 删除权限 */
	public void deleteUserRight(String UserCode) throws YssException {
		Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        try {
        	conn.setAutoCommit(false);
            bTrans = true;
            
            String query = " Delete From Tb_SYS_UserRight Where FUserCode = "+dbl.sqlString(UserCode);
            dbl.executeSql(query);
            //备份表,可删可不删
            query = " Delete From tb_sys_userright_bak Where FUserCode = "+dbl.sqlString(UserCode);
            dbl.executeSql(query);
            
            query = " Delete From tb_sys_userright_bak40 Where FUserCode = "+dbl.sqlString(UserCode);
            dbl.executeSql(query);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);        
		} catch (Exception e) {
			throw new YssException("删除用户权限出错！", e);
		} finally {
	        dbl.endTransFinal(conn, bTrans);
		}
	}

	/**shashijie 2012-5-28 STORY 2620 删除权限 */
	public String getFUserCode() {
		return FUserCode;
	}

	/**shashijie 2012-5-28 STORY 2620 删除权限 */
	public void setFUserCode(String fUserCode) {
		FUserCode = fUserCode;
	}
	/**
	 * add by huangqirong 2012-11-13 bug #6223
	 * */
	public int getAlreadyReset(String userCode){
		ResultSet rs = null;
		String  sql = "";
		int isReset = 0 ;
		try {
			if(dbl.yssTableExist("Tb_Sys_UserList")){
				
				sql = "SELECT FalreadyReset FROM " + pub.yssGetTableName("Tb_Sys_UserList") + " where fusercode = " + dbl.sqlString(userCode);
				rs = dbl.openResultSet(sql);
				if (rs.next()) {
					isReset = rs.getInt("FalreadyReset");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			try {
				if(rs!= null)
					rs.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return isReset;
	}
	
	/**
	 * 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
	 * 新建用户或者在修改用户时点击重置用户密码按钮时，获取初始密码
	 * 若密码复杂度设置中没有设置“重置初始密码”，则返回一个[null]，这个值将在前台被转换为1.
	 * @return 当前的初始密码
	 * @throws YssException
	 */
	public String getCurrentResetPwd() throws YssException
	{
		String sReturn = "";
		
        PassComplexBean passComplex = new PassComplexBean();
        passComplex.setYssPub(pub);
        passComplex.getSetting();
        
        if (passComplex.getPwdReset() == null || passComplex.getPwdReset().trim().equals(""))
        {
        	sReturn = "[null]";
        }
        else
        {
        	sReturn = passComplex.getPwdReset();
        }
		
		return sReturn;
	}
}
