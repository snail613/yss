/**
 * 
 */
package com.yss.main.syssetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * <p>Description: 密码复杂度设置 </p>
 * @author ysstech_xuxuming
 *
 */
public class PassComplexBean extends BaseDataSettingBean implements
		IDataSetting {
	private String sFNum = "D99981231000000";//自动编号，主键.赋默认值，以防止为空
	private int iFLenMin;//密码最小长度，默认为0,表示不限制最小长度
	private int iFLenMax;//密码最大长度，默认为0,表示不限制最 大长度
	private int iFValidTime;//密码有效天数，从启用日期算起。默认为0,永久有效
	private int iFNotSame;//用户名密码不许相同，1:不许相同；0:不作此判断。默认为0
	private int iFTimeLimit;//次数限制，不能与前N次密码相同.。默认为0
	private int iFLowChar;//必有小写字母，1:必须有；0:不作此判断。默认为0
	private int iFBigChar;//必有大写字母 1:必须有；0:不作此判断。默认为0
	private int iFHaveNum;//必有数字，1:必须有；0:不作此判断。默认为0
	private int iFSpecialChar;//必有特殊字符，1:必须有；0:不作此判断。默认为0
	private int iFRepeatChar;//禁用重复字符，1:不能重用字符；0:不作此判断。默认0
	private int iFExpirePrompt;//过期前提示，过期前N天进行提示。默认0,不提示
	private int iFLockLimit;//锁定限制，N天未登录，则锁定。默认0,从不锁定
	private int iFLockError;//输错锁定，N次输错，则锁定。默认0,从不锁定
	private int iFDayLimit;//N天之内不可重复修改密码.默认为０，不作限制
	
	private int iPassFaceShow = 0;//是否显示首次登录密码修改界面 xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B  
	
	//20110803 added by liubo.Story 1233.密码过期时是否显示密码修改窗体
	//*****************************
	private int iFShowPwdChange = 0;
	private int iFAllNoLimit=0;//针对所有用户有效,1:所有用户有效；0:不作此判断。默认为0
	//add by chenjianxin 20110810 需求：1336 QDV4华泰柏瑞2011年7月11日01_AB
	//---add by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB start---//
	private int iFResetPwdChange = 0;//add by zhaoxianlin 20120815 Story #2766 密码重置后强制修改密码
	
	/**Start 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001.
	 * 重置初始密码*/
	private String sPwdReset = "";
	
	public String getPwdReset() {
		return sPwdReset;
	}

	public void setPwdReset(String sPwdReset) {
		this.sPwdReset = sPwdReset;
	}
	/**End 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001.*/

	public int getiFResetPwdChange() {
		return iFResetPwdChange;
	}

	public void setiFResetPwdChange(int iFResetPwdChange) {
		this.iFResetPwdChange = iFResetPwdChange;
	}

	public String getsFNum() {
		return sFNum;
	}

	public void setsFNum(String sFNum) {
		this.sFNum = sFNum;
	}

	public int getiFAllNoLimit() {
		return iFAllNoLimit;
	}

	public void setiFAllNoLimit(int iFAllNoLimit) {
		this.iFAllNoLimit = iFAllNoLimit;
	}
	public int getiFShowPwdChange() {
		return iFShowPwdChange;
	}
	//---add by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB end---//
	public void setiFShowPwdChange(int iFShowPwdChange) {
		this.iFShowPwdChange = iFShowPwdChange;
	}
	//************end*****************
	
	public int getiFDayLimit() {
		return iFDayLimit;
	}

	public void setiFDayLimit(int iFDayLimit) {
		this.iFDayLimit = iFDayLimit;
	}

	public int getiFLenMin() {
		return iFLenMin;
	}

	public void setiFLenMin(int iFLenMin) {
		this.iFLenMin = iFLenMin;
	}

	public int getiFLenMax() {
		return iFLenMax;
	}

	public void setiFLenMax(int iFLenMax) {
		this.iFLenMax = iFLenMax;
	}

	public int getiFValidTime() {
		return iFValidTime;
	}

	public void setiFValidTime(int iFValidTime) {
		this.iFValidTime = iFValidTime;
	}

	public int getiFNotSame() {
		return iFNotSame;
	}

	public void setiFNotSame(int iFNotSame) {
		this.iFNotSame = iFNotSame;
	}

	public int getiFTimeLimit() {
		return iFTimeLimit;
	}

	public void setiFTimeLimit(int iFTimeLimit) {
		this.iFTimeLimit = iFTimeLimit;
	}

	public int getiFLowChar() {
		return iFLowChar;
	}

	public void setiFLowChar(int iFLowChar) {
		this.iFLowChar = iFLowChar;
	}

	public int getiFBigChar() {
		return iFBigChar;
	}

	public void setiFBigChar(int iFBigChar) {
		this.iFBigChar = iFBigChar;
	}

	public int getiFHaveNum() {
		return iFHaveNum;
	}

	public void setiFHaveNum(int iFHaveNum) {
		this.iFHaveNum = iFHaveNum;
	}

	public int getiFSpecialChar() {
		return iFSpecialChar;
	}

	public void setiFSpecialChar(int iFSpecialChar) {
		this.iFSpecialChar = iFSpecialChar;
	}

	public int getiFRepeatChar() {
		return iFRepeatChar;
	}

	public void setiFRepeatChar(int iFRepeatChar) {
		this.iFRepeatChar = iFRepeatChar;
	}

	public int getiFExpirePrompt() {
		return iFExpirePrompt;
	}

	public void setiFExpirePrompt(int iFExpirePrompt) {
		this.iFExpirePrompt = iFExpirePrompt;
	}

	public int getiFLockLimit() {
		return iFLockLimit;
	}

	public void setiFLockLimit(int iFLockLimit) {
		this.iFLockLimit = iFLockLimit;
	}

	public int getiFLockError() {
		return iFLockError;
	}

	public void setiFLockError(int iFLockError) {
		this.iFLockError = iFLockError;
	}
	
	/**
	 * 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
	 * 根据设置的密码复杂度规则，验证“重置初始密码”是否合法
	 * 判断依据是密码复杂度设置中的以下八项：
	 * “密码最大长度、密码最小长度、用户名密码不同、必有小写字母、必有大写字母、必有数字、必有特殊字符、禁用重复字符”
	 * @return
	 * @throws YssException
	 */
	private boolean checkPwdReset() throws YssException
	{
        String checkPass = "";
        int iLen; //密码最小长度
        try {
            StringBuffer bufComplex = new StringBuffer();
            if(this.getiFHaveNum()>0){//密码必须包含数字
            	bufComplex.append("number,");
            }
            if(this.getiFBigChar()>0){//密码必须包含大写字母
            	bufComplex.append("big_char,");
            }
            if(this.getiFLowChar()>0){//密码必须包含小写字母
            	bufComplex.append("low_char,");
            }
            if(this.getiFSpecialChar()>0){//密码必须包含特殊字符
            	bufComplex.append("special_char,");
            }
            bufComplex.append("");//以防buffer中没有字符
            checkPass = regexPass(this.sPwdReset, bufComplex.toString());//直接调用已经存在的方法来判断
            
            int iLenMax;//保存密码最大长度            
            iLen = this.getiFLenMin();//密码最小长度
            iLenMax = this.getiFLenMax();//密码最大长度
            if (checkPass != null && checkPass.length() > 0) {
                throw new YssException("新的初始密码须要包含" + checkPass);
            }

            if (iLen>0&&this.sPwdReset.length() < iLen) {//只有当大于０时才需要判断。默认为０时是不需要判断的。
                throw new YssException("新的初始密码位数小于规定的最小位数！");
            }
            //判断密码是否超过最大长度
            if (iLenMax>0&&this.sPwdReset.length() > iLenMax) {
                throw new YssException("新的初始密码位数超过规定的最大位数！");
            }
            //判断密码中是否有重复字符===========
            if(this.getiFRepeatChar()>0){
            	//此时需要判断　是否有重复字符。有就抛出异常。不让用户设置有重复字符的密码
            	for(int i=0;i<this.sPwdReset.length();i++){
            		char c = this.sPwdReset.charAt(i);
            		int lastFlag = this.sPwdReset.lastIndexOf(c);
            		if(lastFlag>=0&&lastFlag!=i){//有重复字符
            			throw new YssException("新的初始密码不能有重复字符！");
            		}
            	}
            }
        }
        catch(Exception ye)
        {
        	throw new YssException(ye);
        }
        
        return true;
	}
	

    /**
     * 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
     * 验证密码合法性(是否包含数字、大写字母、小写字母、特殊字符)
     * @param pass String
     * @return boolean
     */
    public String regexPass(String pass, String passLevel) throws YssException {
        boolean boolnum = false;
        boolean booldazimu = false;
        boolean boolxiaozimu = false;
        boolean boolspecialchar = false; // 判断是否含有特殊字符
        StringBuffer messageForPass = new StringBuffer();
        String str2 = "1234567890";
        String str3 = "abcdefghijklmnopqrstuvwxyz";
        String str4 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Pattern p = null; // 正则表达式
        Matcher m = null; // 操作的字符串
        p = java.util.regex.Pattern.compile("^[A-Za-z0-9]+$"); //\u4e00-\u9fa5
        try {
            m = p.matcher(pass);
            for (int i = 0; i < pass.length(); i++) {
                if (str2.indexOf(pass.charAt(i)) != -1) {
                    boolnum = true;
                }
                if (str3.indexOf(pass.charAt(i)) != -1) {
                    boolxiaozimu = true;
                }
                if (str4.indexOf(pass.charAt(i)) != -1) {
                    booldazimu = true;
                }
            }
            if (!m.find()) {
                boolspecialchar = true;
            }
            if (passLevel != null && !"".equalsIgnoreCase(passLevel)) {
                if (passLevel.indexOf("number") != -1 && !boolnum) {
                    messageForPass.append("数字、");
                }
                if (passLevel.indexOf("low_char") != -1 && !boolxiaozimu) {
                    messageForPass.append("小写字母、");
                }
                if (passLevel.indexOf("big_char") != -1 && !booldazimu) {
                    messageForPass.append("大写字母、");
                }
                if (passLevel.indexOf("special_char") != -1 && !boolspecialchar) {
                    messageForPass.append("特殊字符、");
                }
            }
            if (messageForPass.indexOf("、") != -1) {
                messageForPass.replace(messageForPass.lastIndexOf("、"),
                                       messageForPass.lastIndexOf("、") + 1, "！");
            }
        } catch (Exception ex) {
            throw new YssException("验证密码复杂度出错：" + ex.getMessage());
        }
        return messageForPass.toString();
    }


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IDataSetting#addSetting()
	 */
	public String addSetting() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsPwdChange = null;
		int iPwdChange = 0;
		PreparedStatement pstmt = null;
		String errorInfo = "保存密码复杂度信息设定时出错!"; // 定义错误提示信息
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		try {

			/**Start 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
			 * 验证“重置初始密码”的合法性。判断依据是当前设置的密码复杂度设置。*/
			if (this.sPwdReset != null && !this.sPwdReset.trim().equals(""))
			{
				checkPwdReset();
			}
			/**End 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001*/
			
//			if (this.sFNum.trim().length() == 0) { // 这里加上判断,防止有多次操作时每次都取.
//				this.sFNum = "D"
//						+ YssFun.formatDate(new java.util.Date(), "yyyyMMdd")
//						+ dbFun.getNextInnerCode("tb_sys_passcomplex", dbl
//								.sqlRight("FNUM", 6), "000001");// 自动生成编号，不能重复
//			}
			//20110817 added  by liubo.Story 1233
			//在此判断是否有FShowPwdChange字段，有该字段则进行插入语句字段一节的拼接
			//======================================
			rsPwdChange = dbl.getUserTabColumns(pub.yssGetTableName("tb_sys_passcomplex"), "FShowPwdChange");
			rsPwdChange.last();
			//================end======================
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "insert into " +pub.yssGetTableName("tb_sys_passcomplex")+
					"(FNum,FLenMin,FLenMax,FValidTime," +
					"FNotSame,FTimeLimit,FLowChar,FBigChar,FHaveNum,FSpecialChar," +
					"FRepeatChar,FExpirePrompt,FLockLimit,FLockError,FDayLimit,FPassFaceShow,FAllNoLimit" + (rsPwdChange.getRow() > 0 ? ",FShowPwdChange" : "") 
					+",FPWDRESET)"//20130702 added by liubo.Story #4135.重置初始密码
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";//xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B
			//20110804 added by liubo.Story #1233
			//当数据库“系统密码复杂度”表存在“FShowPwdChange”（密码过期强制修改密码）字段时，添加记录时要考虑添加该字段的记录；字段不存在时就不添加
			//*********************************************
			if (rsPwdChange.getRow() > 0)
			{
				iPwdChange = 1;
				//edit by songjie 2011.11.16 BUG 2520 QDV4赢时胜(测试)2011年8月23日02_B
				strSql = strSql + " ,?" ;
			}
			strSql = strSql + ",?";		//20130702 added by liubo.Story #4135.重置初始密码
			strSql = strSql + ")";
			//*******************end**************************
			pstmt = conn.prepareStatement(strSql);
			pstmt.setString(1, this.sFNum);
			pstmt.setInt(2, this.iFLenMin);
			pstmt.setInt(3, this.iFLenMax);
			pstmt.setInt(4, this.iFValidTime);
			pstmt.setInt(5, this.iFNotSame);
			pstmt.setInt(6, this.iFTimeLimit);
			pstmt.setInt(7, this.iFLowChar);
			pstmt.setInt(8, this.iFBigChar);
			pstmt.setInt(9, this.iFHaveNum);
			pstmt.setInt(10, this.iFSpecialChar);
			pstmt.setInt(11, this.iFRepeatChar);
			pstmt.setInt(12, this.iFExpirePrompt);
			pstmt.setInt(13, this.iFLockLimit);
			pstmt.setInt(14, this.iFLockError);
			pstmt.setInt(15, this.iFDayLimit);
			pstmt.setInt(16,this.iPassFaceShow);//xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B
			pstmt.setInt(17, this.iFAllNoLimit);//add by chenjianxin 20110810 需求：1336 QDV4华泰柏瑞2011年7月11日01_AB
			//20110804 added by liubo.Story #1233
			//*************************************
			if (iPwdChange == 1)
			{
				pstmt.setInt(18,this.iFShowPwdChange);//20110803 added by liubo.Story 1233.密码过期时是否显示密码修改窗体
			}
			//******************end*******************
			
			pstmt.setString(19,this.sPwdReset);	//20130702 added by liubo.Story #4135.重置初始密码
			
			pstmt.executeUpdate();
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException se) {
			throw new YssException(errorInfo, se);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs,rsPwdChange);
		}
		return "true";
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkInput(byte)
	 */
	public void checkInput(byte btOper) throws YssException {
//		dbFun.checkInputCommon(btOper,
//                pub.yssGetTableName("tb_sys_passcomplex"), "FNum,FLenMin,FLenMax,FValidTime,FNotSame,FTimeLimit,FLowChar,FBigChar,FHaveNum,FSpecialChar,FRepeatChar,FExpirePrompt,FLockLimit,FLockError",
//                this.sFNum + "," + this.iFLenMin + "," +
//                this.iFLenMax +
//                "," + this.iFValidTime + "," +
//                this.iFNotSame + "," +
//                this.iFTimeLimit + "," +                
//                this.iFLowChar + "," +
//                this.iFBigChar + "," +
//                this.iFHaveNum + "," +
//                this.iFSpecialChar + "," +
//                this.iFRepeatChar + "," +
//                this.iFExpirePrompt + "," +
//                this.iFLockLimit + "," +
//                this.iFLockError);

	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#checkSetting()
	 */
	public void checkSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#delSetting()
	 */
	public void delSetting() throws YssException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#deleteRecycleData()
	 */
	public void deleteRecycleData() throws YssException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IDataSetting#editSetting()
	 */
	public String editSetting() throws YssException {
		String strSql = "";
		ResultSet rs = null;
		ResultSet rsPwdChange = null;
		ResultSet rsResetChange = null;
		int iPwdChange = 0;
		int iResetChange = 0;
		PreparedStatement pstmt = null;
		String errorInfo = "保存密码复杂度信息设定时出错!"; // 定义错误提示信息
		Connection conn = dbl.loadConnection();
		boolean bTrans = false; // 代表是否开始了事务
		try {			

			/**Start 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
			 * 验证“重置初始密码”的合法性。判断依据是当前设置的密码复杂度设置。*/
			if (this.sPwdReset != null && !this.sPwdReset.trim().equals(""))
			{
				checkPwdReset();
			}
			/**End 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001*/
			
			conn.setAutoCommit(false);
			bTrans = true;
			strSql = "update " +pub.yssGetTableName("tb_sys_passcomplex") +
					" set FLenMin = ?,FLenMax = ?,FValidTime = ?," +
					"FNotSame = ?,FTimeLimit = ?,FLowChar = ?,FBigChar = ?," +
					"FHaveNum = ?,FSpecialChar = ?," +
					"FRepeatChar = ?,FExpirePrompt = ?,FLockLimit = ?,FLockError = ?,FDayLimit=? " +
					" ,FPassFaceShow = ?,FAllNoLimit=? ";//xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B
			//20110804 added by liubo.Story #1233
			//当数据库“系统密码复杂度”表存在“FShowPwdChange”（密码过期强制修改密码）字段时，修改记录时要考虑修改该字段的记录；字段不存在时就不进行改字段的修改
			//*********************************************
			rsPwdChange = dbl.getUserTabColumns(pub.yssGetTableName("tb_sys_passcomplex"), "FShowPwdChange");
			rsPwdChange.last();
			if (rsPwdChange.getRow() > 0)
			{
				iPwdChange = 1;
				strSql = strSql + " ,FShowPwdChange = ?" ;
			}
			//add by zhaoxianlin 20120815 Story #2766 QDV4海富通2012年06月28日01_A
			//当数据库“系统密码复杂度”表存在“FResetPwdChange”（密码重置后强制修改密码）字段时，修改记录时要考虑修改该字段的记录；字段不存在时就不进行改字段的修改
			//*********************************************
			rsResetChange = dbl.getUserTabColumns(pub.yssGetTableName("tb_sys_passcomplex"), "FResetPwdChange");
			rsResetChange.last();
			if (rsPwdChange.getRow() > 0)
			{
				iResetChange = 1;
				strSql = strSql + " ,FResetPwdChange = ?" ;
			}
			strSql = strSql + ", FPwdReset = ?";	//20130702 added by liubo.Story #4135.重置初始密码
			strSql = strSql + " where FNum ="+dbl.sqlString(this.sFNum);
			//*******************end**************************
			
			pstmt = conn.prepareStatement(strSql);			
			pstmt.setInt(1, this.iFLenMin);
			pstmt.setInt(2, this.iFLenMax);
			pstmt.setInt(3, this.iFValidTime);
			pstmt.setInt(4, this.iFNotSame);
			pstmt.setInt(5, this.iFTimeLimit);
			pstmt.setInt(6, this.iFLowChar);
			pstmt.setInt(7, this.iFBigChar);
			pstmt.setInt(8, this.iFHaveNum);
			pstmt.setInt(9, this.iFSpecialChar);
			pstmt.setInt(10, this.iFRepeatChar);
			pstmt.setInt(11, this.iFExpirePrompt);
			pstmt.setInt(12, this.iFLockLimit);
			pstmt.setInt(13, this.iFLockError);	
			pstmt.setInt(14, this.iFDayLimit);
			pstmt.setInt(15,this.iPassFaceShow);//xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B
			pstmt.setInt(16, this.iFAllNoLimit);//add by chenjianxin 20110810 需求：1336 QDV4华泰柏瑞2011年7月11日01_AB
			//20110804 added by liubo.Story #1233
			//****************************
			if (iResetChange == 1)
			{
				pstmt.setInt(17,this.iFShowPwdChange);//20110803 added by liubo.Story 1233.密码过期时是否显示密码修改窗体
			}
			//*************end****************
			//add by zhaoxianlin 20120815 Story #2766 QDV4海富通2012年06月28日01_A
			//****************************
			if (iPwdChange == 1&&iResetChange == 0)
			{
				pstmt.setInt(17,this.iFResetPwdChange);//20110803 added by liubo.Story 1233.密码过期时是否显示密码修改窗体
			}else if(iPwdChange == 1&&iResetChange == 1){
				pstmt.setInt(18,this.iFResetPwdChange);
			}
			//*************end****************
			
			pstmt.setString(19,this.sPwdReset);	//20130702 added by liubo.Story #4135.重置初始密码
			
			pstmt.executeUpdate();
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
		} catch (SQLException se) {
			throw new YssException(errorInfo, se);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
			dbl.closeResultSetFinal(rs,rsPwdChange,rsResetChange);
			//--- edit by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
		}
		return "true";
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
		ResultSet rs = null;
        String strSql = "";
        try {
			if (dbl.yssTableExist("tb_sys_passcomplex")) {
				strSql = "SELECT * FROM "
						+ pub.yssGetTableName("tb_sys_passcomplex")
						+ " a where a.FNum =" + dbl.sqlString(this.sFNum);
				rs = dbl.openResultSet(strSql);
				while (rs.next()) {
					this.setResultSetAttr(rs);
				}
			}
        } catch (Exception e) {
            throw new YssException("获取密码复杂度信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return this;
	}
	
	/**
	 * 当前台修改密码复杂性界面时，会调用此方法来保存数据
	 */
	public String doChange() throws YssException{
		if(getTotalNum()>0){//当库中有记录时，为更新操作
			return this.editSetting();
		}else{               //当库中没有记录时，为新增操作
			return this.addSetting();
		}
	}
	/**
	 * 得到密码复杂性表的记录条数
	 * @return iTotalNum,记录数
	 */
	public int getTotalNum() throws YssException {		
		ResultSet rs = null;
        String strSql = "";
        int iTotalNum = 0;
        try {
            strSql = "SELECT count(*) FROM " + pub.yssGetTableName("tb_sys_passcomplex");
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {//只循环一次
            	iTotalNum = rs.getInt(1);
            }
        } catch (Exception e) {
            throw new YssException("获取密码复杂度性条数出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return iTotalNum;
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
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IYssConvert#buildRowStr()
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        
        buf.append(this.iFLenMin).append("\t");
        buf.append(this.iFLenMax).append("\t");
        buf.append(this.iFValidTime).append("\t");
        buf.append(this.iFNotSame).append("\t");
        buf.append(this.iFTimeLimit).append("\t");
        buf.append(this.iFLowChar).append("\t");
        buf.append(this.iFBigChar).append("\t");
        buf.append(this.iFHaveNum).append("\t");
        buf.append(this.iFSpecialChar).append("\t");        
        buf.append(this.iFRepeatChar).append("\t");
        buf.append(this.iFExpirePrompt).append("\t");
        buf.append(this.iFLockLimit).append("\t");
        buf.append(this.iFLockError).append("\t"); 
        buf.append(this.iFDayLimit).append("\t");
        buf.append(this.iPassFaceShow).append("\t");//xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B
        buf.append(this.iFAllNoLimit).append("\t");//add by chenjianxin 20110810 需求：1336 QDV4华泰柏瑞2011年7月11日01_AB
        buf.append(this.iFShowPwdChange).append("\t");//20110803 added by liubo.Story 1233.密码过期时是否显示密码修改窗体
        buf.append(this.iFResetPwdChange).append("\t");//add by zhaoxianlin 20120815 Story #2766 密码重置后强制修改密码
        
        buf.append(this.sPwdReset);	//20130702 added by liubo.Story #4135.重置初始密码
//        buf.append(super.buildRecLog());
        return buf.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IYssConvert#getOperValue(java.lang.String)
	 */
	public String getOperValue(String sType) throws YssException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.dao.IYssConvert#parseRowStr(java.lang.String)
	 */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
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
			reqAry = sTmpStr.split("\t");

			this.iFLenMin = Integer.parseInt(reqAry[0]);
			this.iFLenMax = Integer.parseInt(reqAry[1]);
			this.iFValidTime = Integer.parseInt(reqAry[2]);
			this.iFNotSame = Integer.parseInt(reqAry[3]);
			this.iFTimeLimit = Integer.parseInt(reqAry[4]);
			this.iFLowChar = Integer.parseInt(reqAry[5]);
			this.iFBigChar = Integer.parseInt(reqAry[6]);
			this.iFHaveNum = Integer.parseInt(reqAry[7]);
			this.iFSpecialChar = Integer.parseInt(reqAry[8]);
			this.iFRepeatChar = Integer.parseInt(reqAry[9]);
			this.iFExpirePrompt = Integer.parseInt(reqAry[10]);
			this.iFLockLimit = Integer.parseInt(reqAry[11]);
			this.iFLockError = Integer.parseInt(reqAry[12]);
			this.iFDayLimit = Integer.parseInt(reqAry[13]);
			this.iPassFaceShow = Integer.parseInt(reqAry[14]);//xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B
             this.iFAllNoLimit=Integer.parseInt(reqAry[15]);
			this.iFShowPwdChange = Integer.parseInt(reqAry[16]);//20110803 added by liubo.Story 1233.密码过期时是否显示密码修改窗体
			this.iFResetPwdChange = Integer.parseInt(reqAry[17]);//add by zhaoxianlin 20120815 Story #2766 密码重置后强制修改密码
			
			this.sPwdReset = reqAry[18];		//20130702 added by liubo.Story #4135.重置初始密码
		} catch (Exception ex) {
			throw new YssException("解析密码复杂度信息出错", ex);
		}
	}

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData1()
	 */
	public String getListViewData1() throws YssException {
		ResultSet rs = null;
		String strSql = "";
		StringBuffer bufAll = new StringBuffer();
		String sResult = "";
		try {
			strSql = "select * from "+pub.yssGetTableName("tb_sys_passcomplex") +
			" a where a.FNum ="+dbl.sqlString(this.sFNum);
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				setResultSetAttr(rs);
				bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
			}
			if (bufAll.toString().length() > 2) {
				sResult = bufAll.toString().substring(0, bufAll.toString().length() - 2);
            }
 
			return sResult;
		} catch (Exception e) {
			throw new YssException("获取密码复杂度数据出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	private void setResultSetAttr(ResultSet rs) throws SQLException, YssException{
		this.sFNum = rs.getString("FNum")+"";
		this.iFLenMin = rs.getInt("FLenMin");
		this.iFLenMax = rs.getInt("FLenMax");
		this.iFValidTime = rs.getInt("FValidTime");
		this.iFNotSame = rs.getInt("FNotSame");
		this.iFTimeLimit = rs.getInt("FTimeLimit");
		this.iFLowChar = rs.getInt("FLowChar");
		this.iFBigChar = rs.getInt("FBigChar");
		this.iFHaveNum = rs.getInt("FHaveNum");
		this.iFSpecialChar = rs.getInt("FSpecialChar");
		this.iFRepeatChar = rs.getInt("FRepeatChar");
		this.iFExpirePrompt = rs.getInt("FExpirePrompt");
		this.iFLockLimit = rs.getInt("FLockLimit");
		this.iFLockError = rs.getInt("FLockError");
		this.iFDayLimit = rs.getInt("FDayLimit");
		 if(dbl.isFieldExist(rs,"FPassFaceShow")){
			 this.iPassFaceShow = rs.getInt("FPassFaceShow");//xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B
         }
		 if(dbl.isFieldExist(rs,"FShowPwdChange")){
			 this.iFShowPwdChange = rs.getInt("FShowPwdChange");//20110803 added by liubo.Story 1233.密码过期时是否显示密码修改窗体
         }
		 //FAllNoLimit字段在43sp1版本中添加，之前版本会在登录更新版本时报错（密码输错的情况下），因此需要先判断该字段是否存在。
		 //panjunfang modify 20111018
		 if(dbl.isFieldExist(rs,"FAllNoLimit")){
			 this.iFAllNoLimit=rs.getInt("FAllNoLimit");         
		 }
		 if(dbl.isFieldExist(rs,"FResetPwdChange")){ //modify huangqirong 2012-11-23 bug 登陆报错
			 this.iFResetPwdChange=rs.getInt("FResetPwdChange");//add by zhaoxianlin 20120815 Story #2766 QDV4海富通2012年06月28日01_A
		 }

		/**Start 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001.
		 * 重置初始密码*/
		 if(dbl.isFieldExist(rs,"FPwdReset")){ 
			 this.sPwdReset=(rs.getString("FPwdReset") == null ? "" : rs.getString("FPwdReset"));
		 }
		/**End 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001*/
		 
	   }

	/* (non-Javadoc)
	 * @see com.yss.main.dao.IClientListView#getListViewData2()
	 */
	public String getListViewData2() throws YssException {
		// TODO Auto-generated method stub
		return null;
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
	//----------xuqiji 20100331 MS01055 QDV4建行2010年03月26日01_B ----//
	public int getIPassFaceShow() {
		return iPassFaceShow;
	}

	public void setIPassFaceShow(int passFaceShow) {
		iPassFaceShow = passFaceShow;
	}
	//----------------------------end-------------------------------//
}
