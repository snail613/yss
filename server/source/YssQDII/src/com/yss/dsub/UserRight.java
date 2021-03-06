package com.yss.dsub;

import java.sql.*;
import java.util.Date;
import java.util.regex.*;

import com.yss.main.dao.*;
import com.yss.util.*;
import com.yss.main.syssetting.PassComplexBean;
import com.yss.main.syssetting.UserBean;
import com.yss.vsub.YssDbFun;

/**
 * 用户安全性信息的维护
 * 
 * 1.密码策略中增加2个功能 QDV4交银施罗德2009年9月18日01_A MS00707 xuxuming@Modify 20090925
 * 
 * @author ysstech
 *
 */
public class UserRight
    extends BaseDataSettingBean implements IDataSetting { //为了记入日志继承 IDataSetting 蒋锦 2007.12.27
    public UserRight() {
    }

    public UserRight(DbBase link, YssPub pub) {
        dbLink = link;
        pubVar = pub;
    }

    public UserRight(YssPub pub) {
        dbLink = pub.getDbLink();
        pubVar = pub;
        dbFun = new YssDbFun(pubVar);
    }

    //辅助变量
    private DbBase dbLink = null; //DbBase类实例
    private YssPub pubVar = null;
    String strError;

    public final void setDbLink(DbBase link) { //不能通过ysspub设，因为登录检查密码时还没有ysspub
        dbLink = link;
    }

    public final void setYssPub(YssPub pub) {
        pubVar = pub;
    }

    /**
     * 检查密码
     * @param sUser String      用户名
     * @param sPass String      密码
     * @param bExpired YssType  用来返回密码是否过期
     * @throws YssException
     * @return boolean
     */
    public boolean CheckPassword(String sUser, String sSet, String sPass, YssType bExpired) throws
        YssException {
        java.util.Date ddate = new java.util.Date();
        if (sUser == null || sUser.length() == 0 || sPass == null ||
            sPass.length() == 0) {
            return false;
        }
        if (sPass.compareTo(getPassword(sUser, sSet, ddate)) == 0) {
            return true;
        }
        return false;
    }

    public boolean CheckPasswordAllUsee(String sUser, String sPass,
                                        YssType bExpired) throws
        YssException {

        return false;
    }

    /**
     * 返回密码设置，用字符串数组形式
     * 0:密码长度；1:密码有效天数
     * @throws YssException
     * @return String[]
     */
    public String[] getPassSetting(String user) throws YssException {
        ResultSet rs = null;
        String[] result = {
            "0", "0", "0"};
        String sTmp;
        int blen;
        int bCount;
        try {
            rs = dbLink.openResultSet(
                "select fpassdate,FpassLen,fpasscount from TB_SYS_userList where FUserCode='" + user + "'");
            if (rs.next()) {
                sTmp = YssFun.formatDate(rs.getDate("fpassdate"), "yyyy-MM-dd");
                result[0] = sTmp;
                blen = rs.getInt("FpassLen");
                result[1] = String.valueOf(blen);
                bCount = rs.getInt("fpasscount");
                result[2] = String.valueOf(bCount);

            }
            return result;
        } catch (SQLException se) {
            throw new YssException("获取安全设置出错！", se);
        } finally {
            dbLink.closeResultSetFinal(rs);
        }
    }

    //获取密码，并得到密码设置日期
    //也可直接比较二进制，但是怕加密方式改变以后，同一密码生成不同字节表
    //dDate返回密码设置日期
    public String getPassword(String sUser, String sSet, java.util.Date dDate) throws
        YssException {
        ResultSet rs = null;
        ResultSet rsRight = null;
        ResultSet rsUserType = null;	//20120713 added by liubo.Bug #4939
        ResultSet rsInhen = null;		//20120926 added by liubo.Story #2737
        byte[] bpass;
        String sql = "";
        String strSql = "";		//20120713 added by liubo.Bug #4939

        //获取密码
        try {
        	
//            if (sSet.trim().length() != 0) {
//                sql = " and FPortGroupCode like '%" + sSet + ",%'";
//            }  delete by guolongchao 20111125 STORY 1572 权限复制优化扩展
        	/**add---shashijie 2013-4-3 BUG 7466 ,56sp3升级到59sp2版本之后，用户登录时提示无权限
        	 * 经检查发现这里只查询了用户是否有权限而未查询用户关联的角色是否有权限,这里加上*/
			String query = getUserrightQuery(sUser,sSet);
			/**end---shashijie 2013-4-3 BUG 7466*/
        	rsRight= dbLink.openResultSet(query);
        	if(!rsRight.next())
        	{
        		//20120713 added by liubo.Bug #4939
        		//某个用户登录系统，发现该用户在登录的组合群下没有设置任何权限（组合级和组合群级）
        		//这时需要判断该用户的用户类型是否为管理员（Tb_Sys_UserList表中字段FUserTypeCode的值为1）
        		//若是管理员，直接进入组合群。若不是，则抛出该组合群无权限的异常
        		//此BUG修改到了1572所涉及的登录逻辑
        		//=====================================
        		
        		
        		strSql = "select * from Tb_Sys_UserList where FUserCode = " + dbLink.sqlString(sUser);
        		rsUserType = dbLink.queryByPreparedStatement(strSql);
        		while(rsUserType.next())
        		{
        			if (rsUserType.getString("FUserTypeCode").trim().equals("1"))
        			{
        				
        			}
        			else
        			{
                		//20120926 added by liubo.Story #2737
                		//某个用户，在申请登录的组合群中无任何权限，除了判断该用户的用户类型，还需要判断该用户的权限继承情况。
                		//**********************************************
        				java.util.Date dCurDate = new Date();	//当前日期
        				
        				boolean bInhenRights = false;			//当前用户是否有继承到该组合群的权限
        				
        				strSql = "select * from tb_sys_perinheritance where FTRUSTEE like '%" + sUser + "%'" +
        						 " and FSTARTDATE <= " + dbLink.sqlDate(dCurDate) + " and FENDDATE >= " + dbLink.sqlDate(dCurDate) + " and FCheckState = 1";
        				rsInhen = dbLink.queryByPreparedStatement(strSql);
        				
        				while(rsInhen.next())
        				{
        					if (rsInhen.getString("FPORTCODELIST") != null && !rsInhen.getString("FPORTCODELIST").trim().equals(""))
        					{
        						//若该用户在权限继承表中有记录，则取出该用户的被分配到的组合与组合群的记录。记录格式为组合群>>组合
        						//判断组合群一项，是否存在于当前登录组合群一致的记录。若不存在，表示该用户未继承到该组合群的权限
        						String[] strPortList = rsInhen.getString("FPORTCODELIST").split(",");
        						for (int i = 0; i < strPortList.length; i++)
        						{
        							String[] strDetail = strPortList[i].split(">>");
        							if (strDetail[0].trim().equalsIgnoreCase(sSet))
        							{
        								bInhenRights = true;
        								break;
        							}
        						}
        					}
        					if (bInhenRights)
        					{
        						break;
        					}
        				}
        				
        				dbLink.closeResultSetFinal(rsInhen);

                		//**********************end************************
        				
        				if (!bInhenRights)
        				{

            				throw new YssException("对不起，您没有访问组合群【" + sSet + "】的权限，请联系管理员！");//BUG7121 yeshenghong 
        				}
        				
        			}
        		}
        		dbLink.closeResultSetFinal(rsUserType);
        		//==================end===================
        	}
        	
            rs = dbLink.openResultSet(
                "select FUserName,FPass,Fpassdate,Flocked from tb_sys_UserList where FUserCode='" + sUser + "'" + sql);
            if (rs.next()) {
                if (rs.getString("Flocked") != null &&
                    rs.getString("Flocked").equalsIgnoreCase("1")) {
                    throw new YssException("用户【" + sUser + "_" + rs.getString("FUserName") + "】已经被锁定，无法使用，请与管理员联系！");

                }
                bpass = rs.getBytes("FPass");
                if (dDate != null) { //如果dDate参数提供了，则返回密码设置日期（如果无效则返回当前）
                    if (!YssFun.isDate(rs.getString("Fpassdate"), dDate)) {
                        dDate.setTime(System.currentTimeMillis());
                    }
                }
                return decodePass(bpass);
            } else {
                throw new YssException("对不起，您没有访问组合群【" + sSet + "】的权限，请联系管理员！");//BUG7121 yeshenghong 
            }
        } catch (SQLException se) {
            throw new YssException("用户验证出错！", se);
        } finally {
            dbLink.closeResultSetFinal(rs);
            dbLink.closeResultSetFinal(rsRight,rsInhen);
        }
    }

    /**shashijie 2013-4-3 BUG 7466 查询用户是否有任何权限以及关联的角色是否有任何权限*/
	private String getUserrightQuery(String sUser, String sSet) {
		String SQL = " Select a.Fusercode,"+
			" a.Frighttype,"+
			" a.Fassetgroupcode,"+
			" a.Frightcode,"+
			" a.Fportcode,"+
			" a.Frightind,"+
			" a.Fopertypes from tb_sys_userright a where fusercode='"+sUser+
			"'and (frighttype='port'or frighttype='group') " +
        	" and fassetgroupcode='"+sSet+"'and fopertypes is not null"+
        	" Union"+
        	" Select Distinct a.Fusercode,"+
        	" a.Frighttype,"+
        	" a.Fassetgroupcode,"+
        	" a.Frightcode,"+
        	" a.Fportcode,"+
        	" a.Frightind,"+
        	" a.Fopertypes"+
        	" From Tb_Sys_Userright a"+
        	" Join (Select B1.Frolecode, B1.Frightcode, B1.Fopertypes"+
        	" From Tb_Sys_Roleright B1) b On a.Frightcode = b.Frolecode"+
        	" Where Fusercode = '"+sUser+"'"+
        	" And (Frighttype = 'port' Or Frighttype = 'group')"+
        	" And Frightind = 'Role'";
		return SQL;
	}

	public String getPassword(String sUser, String sSet) throws YssException {
        return getPassword(sUser, sSet, null);
    }

    public String getPassword(String sUser) throws YssException {
        return getPassword(sUser, "", null);
    }

    /**
     * //密码编码过程
     * @param pass String
     * @return byte[]
     */
    public static byte[] encodePass(String pass) {
        byte[] bpass;
        byte[] bpass1 = new byte[200];
        int ltmp, i;

        //todo应该先用随即数填充bpass1
        bpass = pass.getBytes();
        ltmp = bpass.length - 1;
        for (i = 0; i <= ltmp; i++) {
            bpass1[ltmp + 2 - i] = bpass[i];
        }

        bpass1[1] = (byte) (ltmp / 2);
        bpass1[198] = (byte) (ltmp + 1 - bpass1[1]);

        ltmp = 0;
        for (i = 0; i <= 198; i++) {
            ltmp += bpass1[i];
            if ( (i % 2) == 0) {
                bpass1[i] = (byte) (255 - bpass1[i]);
                ltmp += bpass1[i];
            }
        }
        bpass1[i] = (byte) ( (ltmp + bpass1[0] + bpass1[i - 1]) % 256);

        return bpass1;
    }

    /**
     * //密码解密
     * @param bpass byte[]
     * @return String
     */
    public static String decodePass(byte[] bpass) {
        byte[] bpass1;
        int i, ltmp;

        ltmp = (int) bpass[0] + bpass[bpass.length - 2];
        for (i = 0; i <= bpass.length - 2; i++) {
            if ( (i % 2) == 0) {
                ltmp += bpass[i];
                bpass[i] = (byte) (255 - bpass[i]);
            }
            ltmp += bpass[i];
        }
        if ( (byte) (ltmp % 256) != bpass[i]) {
            return ""; //校验错误
        }

        ltmp = bpass[1] + bpass[i - 1] - 1;

        bpass1 = new byte[ltmp + 1];
        for (i = 0; i <= ltmp; i++) {
            bpass1[ltmp - i] = bpass[i + 2];
        }

        return new String(bpass1); //不能用bpass1.toString！
    }

    /**
     * 获得角色名称
     * @param shRight short  角色编码
     * @throws YssException
     * @return String
     */
    public String YssRoleName(short shRight) throws YssException {
        return null;
    }

    /**
     * 获得序号
     * @param strSql String    查询SQL语句
     * @param iStart int       默认值
     * @param boolMax boolean  是否为最大值
     * @throws YssException
     * @return int
     */
    public int GetSequence(String strSql, int iStart, boolean boolMax) throws
        YssException {
        ResultSet Rs = null;
        int iReturn;
        boolean boolState = true;

        try {
            Rs = dbLink.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            if (!Rs.next()) {
                iReturn = iStart;
            } else {
                if (boolMax) {
                    Rs.last();
                    iReturn = Rs.getInt(1) + 1;
                } else {
                    iReturn = iStart;
                    while (Rs.next() && boolState) {
                        if (Rs.getInt(1) > iReturn) {
                            boolState = false;
                        } else {
                            iReturn++;
                        }
                    }
                }
            }
            Rs.getStatement().close();
            return iReturn;

        } catch (SQLException sqle) {
            throw new YssException("获取顺序号出错！", sqle);
        } finally {
            dbLink.closeResultSetFinal(Rs);
        }

    }

    public int GetSequence(String strSql) throws
        YssException {
        return GetSequence(strSql, 1, true);
    }

    public int GetSequence(String strSql, int iStart) throws
        YssException {
        return GetSequence(strSql, iStart, true);
    }

    /**
     * 判断有无用户，没有建超级用户。每个系统在登录之前都要调用
     * @throws YssException
     * @return boolean
     */
    public boolean InitUser() throws YssException {
        return false;
    }

    /**
     * 获取密码容错次数
     * @param user String
     * @throws YssException
     * @return int
     */
    public int getLogCount(String user) throws YssException {
        String[] strTmp = getPassSetting(user);
        if (strTmp.length >= 2) {
            return YssFun.toInt(strTmp[2]);
        } else {
            return 0;
        }
    }

    /**
     * 锁定登录用户
     * @paam FUser String
     * @throws YssException
     * @return String
     */
    public String locklogUser(String FUser) throws YssException {
        ResultSet rs = null;
        boolean bTrans = false;
        Connection conn = dbLink.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            rs = dbLink.openResultSet(
                "select FUserCode,FUserName,flocked  from TB_SYS_UserList where FUserCode='" + FUser + "'", true);
            if (rs.next()) {
                rs.updateString("flocked", "1");
                rs.updateRow();
            } else {
                throw new YssException("锁定用户出错，没有对应的用户！");
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "true";
        } catch (SQLException sqle) {
            throw new YssException("锁定用户出错！", sqle);
        } finally {
            dbLink.closeResultSetFinal(rs);
            dbLink.endTransFinal(conn, bTrans);
        }
    }

    /**linjunyun 需求MS00016 2008-11-12
     * 得到当前组合群密码合法性的级别
     * @return String
     * @throws YssException
     */
    public String getPassLevel() throws YssException {
        ResultSet rs = null;
        String passlevel = "";
        Connection conn = dbLink.loadConnection();
        try {
            conn.setAutoCommit(false);
            rs = dbLink.openResultSet(
                "select FPassLevel from Tb_Sys_Userlist where FUserCode='" + pubVar.getUserCode() + "'");
            if (rs.next()) {
                passlevel = rs.getString("FPassLevel");
            }
            conn.commit();
            conn.setAutoCommit(true);
            return passlevel;
        } catch (SQLException sqle) {
            throw new YssException("查询密码安全级别出错！", sqle);
        } finally {
            dbLink.closeResultSetFinal(rs);
        }
    }

    /**linjunyun 需求MS00016 2008-11-12
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
            throw new YssException("密码比对安全性出错！", ex);
        }
        return messageForPass.toString();
    }

    /**
     * 修改密码
     * @param oldPass String
     * @param Pass String
     * @throws YssException
     * @return String
     */
    public String midPass(String FOldPass, String FNewPass, String FUser) throws YssException {
        String sOldPass;
        String checkPass = "";
        PreparedStatement pstmt = null; 
        Connection conn = dbLink.loadConnection();
        boolean bTrans = false;
        //String[] passSet;//无用注释
        byte[] bPass;
        int iLen; //密码最小长度
        String strSql = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //判断新密码的合法性 2008.11.07 linjunyun
//            checkPass = regexPass(FNewPass, getPassLevel());//edit by xuxuming,20091110.MS00776.密码复杂性验证从单独的表中取数据　
            //=====add by xuxuming,20091110.MS00776.从密码复杂性表中取数据，以此来作以下验证==
            StringBuffer bufComplex = new StringBuffer();//保存密码复杂性验证所需要的字符
            PassComplexBean passComplex = new PassComplexBean();
            passComplex.setYssPub(pubVar);
            passComplex.getSetting();//取出密码复杂性数据
            if(passComplex.getiFHaveNum()>0){//密码必须包含数字
            	bufComplex.append("number,");
            }
            if(passComplex.getiFBigChar()>0){//密码必须包含大写字母
            	bufComplex.append("big_char,");
            }
            if(passComplex.getiFLowChar()>0){//密码必须包含小写字母
            	bufComplex.append("low_char,");
            }
            if(passComplex.getiFSpecialChar()>0){//密码必须包含特殊字符
            	bufComplex.append("special_char,");
            }
            bufComplex.append("");//以防buffer中没有字符
            checkPass = regexPass(FNewPass, bufComplex.toString());//直接调用已经存在的方法来判断
            int iLenMax;//保存密码最大长度            
            iLen = passComplex.getiFLenMin();//密码最小长度
            iLenMax = passComplex.getiFLenMax();//密码最大长度
            //=====end======================================================
            if (checkPass != null && checkPass.length() > 0) {
                throw new YssException("你的密码还须包含" + checkPass);
            }
            //判断旧密码的正确性
            //无法修改密码
            //edit by songjie 2012.01.10 传入组合群代码
            sOldPass = getPassword(FUser,pubVar.getPrefixTB()); //暂时性修改 蒋锦 2007-11-26
            if (!sOldPass.equalsIgnoreCase(FOldPass)) {
                throw new YssException("旧密码不正确！");
            }

            //判断密码长度
//            passSet = getPassSetting(FUser);//edit by xuxuming,20091110.MS00776.密码长度不再从用户表里面取。而是从单独的表中取
//            iLen = Integer.parseInt(passSet[1]);
            if (iLen>0&&FNewPass.length() < iLen) {//edit by xuxuming,只有当大于０时才需要判断。默认为０时是不需要判断的。
                throw new YssException("密码位数小于规定的最小位数！");
            }
            //====add by xuxuming,20091110.MS00776.判断密码是否超过最大长度=========
            if (iLenMax>0&&FNewPass.length() > iLenMax) {
                throw new YssException("密码位数超过规定的最大位数！");
            }
            //======end===========================
            //=========add by xuxuming,20091110.MS00776.判断密码中是否有重复字符===========
            if(passComplex.getiFRepeatChar()>0){
            	//此时需要判断　是否有重复字符。有就抛出异常。不让用户设置有重复字符的密码
            	for(int i=0;i<FNewPass.length();i++){
            		char c = FNewPass.charAt(i);
            		int lastFlag = FNewPass.lastIndexOf(c);
            		if(lastFlag>=0&&lastFlag!=i){//有重复字符
            			throw new YssException("密码不能有重复字符！");
            		}
            	}
            }
            if(passComplex.getiFNotSame()>0){//用户名密码不允许相同
            	if(FUser!=null&&FUser.equalsIgnoreCase(FNewPass)){//如果用户名和密码相同，就抛出异常。
            		throw new YssException("密码不能和用户名相同！");
            	}
            }
            //===========end===========================
            bPass = encodePass(FNewPass);
            
            //==== MS00707,要求在密码策略中增加2个功能,QDV4交银施罗德2009年9月18日01_A xuxuming 200925===============
            //获取用户信息
            UserBean userData = new UserBean();
            userData.setYssPub(pubVar);
            userData.getUserById(FUser);
            /*           //天数限制　和　次数限制　现在都是从　单独的表中取数据，不再从用户表取了。
            int iDayLimit = userData.getFDayLimit();	//得到“N天不能修改密码”的天数限制
            int iTimeLimit = userData.getFTimeLimit();	//得到“不能与前N次密码相同”的次数限制
            */
            int iDayLimit = passComplex.getiFDayLimit();
            int iTimeLimit = passComplex.getiFTimeLimit();
            // 获取用户历史代码和上次修改密码日期
			String strOldPass = this.getOldPass(FUser); 			// 历史密码
			java.util.Date dCheckDate = userData.getFCheckDate();	//上次修改密码日期
            
			//---edit by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB start---//
			// 用户类型代码为 “0”，表明是 普通用户。需要进行以下验证
			if ("0".equalsIgnoreCase(userData.getUserTypeCode())) {
				String[] reqAry = strOldPass
				.split(YssCons.YSS_ITEMSPLITMARK1);

				if (reqAry.length > 0) {// 表明有历史密码						
					for (int j = reqAry.length - 1; j >= (reqAry.length
							- iTimeLimit >= 0 ? reqAry.length - iTimeLimit
									: 0); j--) { // 取出之前 N 次的密码，判断密码是否与前N次相同
						if (FNewPass.equalsIgnoreCase(reqAry[j])) { // 如果与前N次中某一次相同，就不能成功修改
							throw new YssException("不能与前" + iTimeLimit
									+ "次密码相同!");
						}
					}
				}
				// 如果dCheckdate为null表示为第一次修改密码，要过滤掉；当前日期-上次修改密码日期小于用户设定的天数，应给予提示
				if (iDayLimit > 0) {//大于０说明，用户设置了需要进行些判断
					if (dCheckDate != null
							&& iDayLimit > YssFun.dateDiff(dCheckDate,
									new java.util.Date())) {
						throw new YssException(iDayLimit + "天之内不可重复修改密码!");
					}
				}
			}
			if(passComplex.getiFAllNoLimit()>0){
				String[] reqAry = strOldPass
				.split(YssCons.YSS_ITEMSPLITMARK1);
				if (reqAry.length > 0) {// 表明有历史密码						
					for (int j = reqAry.length - 1; j >= (reqAry.length
							- iTimeLimit >= 0 ? reqAry.length - iTimeLimit
							: 0); j--) { // 取出之前 N 次的密码，判断密码是否与前N次相同
						if (FNewPass.equalsIgnoreCase(reqAry[j])) { // 如果与前N次中某一次相同，就不能成功修改
							throw new YssException("不能与前" + iTimeLimit
									+ "次密码相同!");
						}
					}
				}
				if (iDayLimit > 0) {//大于０说明，用户设置了需要进行些判断
					if (dCheckDate != null
							&& iDayLimit > YssFun.dateDiff(dCheckDate,
									new java.util.Date())) {
						throw new YssException(iDayLimit + "天之内不可重复修改密码!");
					}
				}
			}
			//---edit by chenjianxin 2011.08.10 需求 1336 QDV4华泰柏瑞2011年7月11日01_AB end---//
            //下面开始修改数据库中密码
            strSql = "update " + pubVar.yssGetTableName("Tb_Sys_UserList") + " set FPass = ?,FCheckDate = ? , FalreadyReset = 0 where FUserCode = '" + //modify huangqirong 2012-11-14 bug #6223 增加修改密码时
                FUser + "'";//此处更新密码 和 修改密码的时间 两个字段。
            pstmt = conn.prepareStatement(strSql);
            pstmt.setBytes(1, bPass);
            pstmt.setDate(2, new java.sql.Date(new java.util.Date().getTime()));//将当前日期作为修改密码的时间
            pstmt.executeUpdate();

            //系统新增加了一张表，用来保存用户的历史密码。编号为主键
            strSql = "insert into " + pubVar.yssGetTableName("Tb_Sys_UserOldPass") + "(FNUM,FUserCode,FOldPass) " +
                " values(?,?,?)";
            
            String sNum = "D" +
                    YssFun.formatDate(new java.util.Date(), "yyyyMMdd") +
                    dbFun.getNextInnerCode("Tb_Sys_UserOldPass", dbLink.sqlRight("FNUM", 6), "000001");//自动生成编号，不能重复

            pstmt = conn.prepareStatement(strSql);
            pstmt.setString(1, sNum);
            pstmt.setString(2, FUser);
            pstmt.setBytes(3, bPass);
            pstmt.executeUpdate();
            //----------add by zhaoxianlin 20120817 Story #2766 QDV4海富通2012年06月28日01_A
            //管理员密码重置后提示修改密码后，将常量赋为0，以免登录时再次提示
            YssCons.YSS_USER_OLDPASS_COUNT = 0;
            //---------end--------
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            return "true";
            /**add---huhuichao 2013-10-17 BUG #81153 登录时密码过期修改密码提示空指针异常 */
        } catch (Exception e) {
        	/**add---huhuichao 2013-10-24 BUG  82002 当修改后的密码不符合密码的设置条件时，提示修改出错*/
            throw new YssException("修改密码出错！"+e.getMessage());
            /**end---huhuichao 2013-10-24 BUG  82002*/	
        } finally {
            //dbLink.closeResultSetFinal(rs);
            dbLink.endTransFinal(conn, bTrans);
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B start---//
            dbLink.closeStatementFinal(pstmt);//edit by huhuichao 2013-10-17 BUG #81153 登录时密码过期修改密码提示空指针异常
			//--- add by songjie 2013.08.23 BUG 9161 QDV4赢时胜（北京）2013年08月21日01_B end---//
			/**end---huhuichao 2013-10-17 BUG #81153*/
        }
    }

    public String midPass(String FOldPass, String FNewPass) throws YssException {
        return midPass(FOldPass, FNewPass, pubVar.getUserName());
    }

    /**
     * getOldPass
     * 根据用户代码得到用户的历史密码
     * MS00707:QDV4交银施罗德2009年9月18日01_A xuxuming@Modify 20090923
     * @param string String
     * @return String
     */
    public String getOldPass(String userCode) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        String strReturn = "";
        try {
            strSql = "select FOldPass from " + pubVar.yssGetTableName("Tb_Sys_UserOldPass") +
                //edit by songjie 2011.09.02 BUG 2542 QDV4华泰柏瑞2011年8月24日01_B 根据FNum排序 按密码的修改时间依次获取数据
                " a where a.FUserCode='" + userCode + "' order by FNum ";
            rs = dbLink.openResultSet(strSql);
            while (rs.next()) {
                strReturn += UserRight.decodePass(rs.getBytes("FOldPass"))+YssCons.YSS_ITEMSPLITMARK1;// 历史密码用 '\t'隔开
            }
            return strReturn;
        } catch (SQLException se) {
            throw new YssException("获取用户历史密码出错！", se);
        } finally {
            dbLink.closeResultSetFinal(rs);
        }

    }

    public void checkInput(byte btOper) throws YssException {

    }

    public String addSetting() throws YssException {
        return "";
    }

    public String editSetting() throws YssException {
        return "";
    }

    public void delSetting() throws YssException {

    }

    public void checkSetting() throws YssException {

    }

    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        return "";
    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    /**
     * getListViewData1
     *
     * @return String
     */
    public String getListViewData1() {
        return "";
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
     * parseRowStr
     *
     * @param sRowStr String
     */
    public void parseRowStr(String sRowStr) {
    }

    /**
     * buildRowStr
     *
     * @return String
     */
    public String buildRowStr() {
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
     * getBeforeEditData
     *
     * @return String
     */
    public String getBeforeEditData() {
        return "";
    }

    /**
     * deleteRecycleData
     */
    public void deleteRecycleData() {
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

}
