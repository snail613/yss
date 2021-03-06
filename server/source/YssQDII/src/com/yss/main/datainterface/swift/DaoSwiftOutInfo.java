package com.yss.main.datainterface.swift;

import com.yss.main.dao.IDataSetting;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.util.YssException;
import java.sql.*;

import com.yss.util.YssCons;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.util.YssFun;
import   java.util.*;
import java.util.Date;
import   java.text.*;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DaoSwiftOutInfo  extends BaseDataSettingBean implements
      IDataSetting{
    private String sFSwiftType="";//报文原类型
    private String sAppTag="";//应用标识
    private String sServerTag="";//服务标识
    private String sBic="";//BIC
    private String sCity="";//城市
    private String sTerminalCode="";//终端代码
    private String sGetSendTag="";//收发标识
    private String sMessageType="";//报文类型
    private String sPriorClass="";//优先等级
    private String sSendControl="";//传送监控
    private String sInvalTime="";//失效时间
    private String sUserRefer="";//用户参考
    private String sSwiftIndex="";

    private String sSwiftStatus="";//报文原状态
    private String sFOldSwiftType="";//原 报文类型
    private String sOldSwiftStatus="";//原 报文原状态
    private String sSwiftCode=""; //报文代码  by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    private String sOldSwiftCode="";//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    private String sSwiftDesc ="";// 添加备注信息 by leeyu 20100104
    
    public void setsSwiftDesc(String sSwiftDesc) {
		this.sSwiftDesc = sSwiftDesc;
	}
	//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    public String getsOldSwiftCode() {
		return sOldSwiftCode;
	}
    //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
	public void setsOldSwiftCode(String sOldSwiftCode) {
		this.sOldSwiftCode = sOldSwiftCode;
	}

	public DaoSwiftOutInfo() {
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
     Connection conn =null;
     PreparedStatement stm =null;
     String sqlStr="";
     String[] arrData =null;
     boolean bTrans = false;
     try{
        conn = dbl.loadConnection();
        conn.setAutoCommit(false);
        sqlStr = "delete from " + pub.yssGetTableName("Tb_Dao_SWIFT_OutInfo") +
              //" where FSwiftType=" + dbl.sqlString(this.sFOldSwiftType)+//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
              //" and FSwiftStatus= "+dbl.sqlString(this.sOldSwiftStatus);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        		" where FSwiftCode="+dbl.sqlString(this.sSwiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        dbl.executeSql(sqlStr);

        sqlStr="insert into "+pub.yssGetTableName("Tb_Dao_SWIFT_OutInfo")+
              "(FSwiftType,FAppTag,FServerTag,FBic,FCity,FTerminalCode,FGetSendTag,FMessageType,"+
              "FPriorClass,FSendControl,FInvalTime,FUserRefer,FSwiftStatus,FSwiftCode) Values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        stm =dbl.openPreparedStatement(sqlStr);
       // arrData = sMutilRowStr.split("\r\n"); //这里应该是\r\n了,原来是\r\f因为你压数据是这样的
       // for (int i = 0; i < arrData.length; i++) {
           this.parseRowStr(sMutilRowStr);
           stm.setString(1,this.sFSwiftType);
           stm.setString(2,this.sAppTag);
           stm.setString(3,this.sServerTag);
           stm.setString(4,this.sBic);
           stm.setString(5,this.sCity);
           stm.setString(6,this.sTerminalCode);
           stm.setString(7,this.sGetSendTag);
           stm.setString(8,this.sMessageType);
           stm.setString(9,this.sPriorClass);
           stm.setString(10,this.sSendControl);
           stm.setString(11,this.sInvalTime);
           stm.setString(12,this.sUserRefer);
           stm.setString(13,this.sSwiftStatus);
           stm.setString(14, this.sSwiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
           stm.addBatch();
      //  }
           bTrans = true;
           stm.executeBatch();
           conn.commit();
           bTrans = false;
           conn.setAutoCommit(true);

     }catch(Exception ex){
        throw new YssException("保存实体字段信息出错！",ex);
     }finally{
        dbl.closeStatementFinal(stm);
        dbl.endTransFinal(conn,bTrans);
     }

        return "";
    }

    public IDataSetting getSetting() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql =
                "select *" +
                " from " + pub.yssGetTableName("Tb_Dao_SWIFT_OutInfo")  +
                //" where FSwiftType= " + dbl.sqlString(this.sFSwiftType)+
                //" and FSwiftStatus= "+ dbl.sqlString(this.sSwiftStatus);//导出主键的更改
                " where FSwiftType="+dbl.sqlString(this.sFSwiftType)+
                " and FSwiftStatus="+dbl.sqlString(this.sSwiftStatus.equalsIgnoreCase("REDO")?"NEW":this.sSwiftStatus)+
                " and FSWIFTCODE="+dbl.sqlString(this.sSwiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                setResultSetAttr(rs);
            }
        } catch (Exception e) {
            throw new YssException("获取股指期权信息设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
    }

    public void deleteRecycleData() throws YssException {
    }

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
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

    public String getListViewData1() throws YssException {
        return "";
    }

    public String getListViewData2() throws YssException {
        return "";
    }

    public String getListViewData3() throws YssException {
        return "";
    }

    public String getListViewData4() throws YssException {
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
        return "";
    }

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
            reqAry = sTmpStr.split("\t");
            this.sFSwiftType= reqAry[0]; // 报文原类型
            this.sAppTag= reqAry[1]; // 应用标识
            this.sServerTag = reqAry[2]; // 服务标识
            this.sBic = reqAry[3]; // BIC
            this.sCity = reqAry[4]; // 城市
            this.sTerminalCode = reqAry[5]; // 终端代码
            this.sGetSendTag = reqAry[6]; // 收发标识
            this.sMessageType = reqAry[7]; // 报文类型
            this.sPriorClass = reqAry[8]; // 优先等级
            this.sSendControl = reqAry[9]; // 传送监控
            this.sInvalTime = reqAry[10]; // 失效时间
            this.sUserRefer = reqAry[11]; // 用户参考
            this.sSwiftStatus=reqAry[12];//报文原状态 add by libo 主键报文原状态的加入
            this.sSwiftIndex=reqAry[13];//报文编号
            this.sSwiftCode = reqAry[14];//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874

        } catch (Exception e) {
            throw new YssException("解析报文请求信息出错", e);
        }

    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.sFSwiftType).append("\t");
        buf.append(this.sAppTag).append("\t");
        buf.append(this.sServerTag).append("\t");
        buf.append(this.sBic).append("\t");
        buf.append(this.sCity).append("\t");
        buf.append(this.sTerminalCode).append("\t");
        buf.append(this.sGetSendTag).append("\t");
        buf.append(this.sMessageType).append("\t");
        buf.append(this.sPriorClass).append("\t");
        buf.append(this.sSendControl).append("\t");
        buf.append(this.sInvalTime).append("\t");
        buf.append(this.sUserRefer).append("\t");
        buf.append(this.sSwiftCode);//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
        return buf.toString();
    }

    public void setResultSetAttr(ResultSet rs) throws YssException, SQLException {
        this.sFSwiftType = rs.getString("FSwiftType");
        this.sAppTag=rs.getString("FAppTag");
        this.sServerTag=rs.getString("FServerTag");
        //this.sBic=rs.getString("FBic");
        this.sCity=rs.getString("FCity");
        this.sTerminalCode=rs.getString("FTerminalCode");
        this.sGetSendTag=rs.getString("FGetSendTag");
        this.sMessageType=rs.getString("FMessageType");
        this.sPriorClass=rs.getString("FPriorClass");
        this.sSendControl=rs.getString("FSendControl");
        this.sInvalTime=rs.getString("FInvalTime");
        this.sUserRefer=rs.getString("FUserRefer");
        this.sSwiftCode = rs.getString("FSwiftCode");//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
    }

    
	// add by jiangshichao 通过银行账户获取BIC码
	public void setBIC(String tradeNum, String operType,String date) throws YssException {
		String Bic = "";
		String sqlStr = "";
		Connection conn = null;
		ResultSet rs = null;
		
		String tabName = "";
		String filteSql = "";
		String field = "FCashAcccode";
		String brokerCode = "";
		try {
			//---------------------------------------------------------------------
			if (operType.equalsIgnoreCase("cash")) {
				tabName = pub.yssGetTableName("tb_cash_subtransfer");
				filteSql = " and finout='-1' ";
           //----------------------------------------------------------------------
			} else if (operType.equalsIgnoreCase("rate")) {
				//外汇业务报文，关联业务表是tb_data_ratetrade(primary FNUM),
				tabName = pub.yssGetTableName("tb_data_ratetrade");
				field = "FScashAcccode";
            //---------------------------------------------------------------------
			} else if (operType.equalsIgnoreCase("security")) {
				//证券业务报文,关联业务表是tb_data_subtrade(primary FNUM), 
				tabName = pub.yssGetTableName("tb_data_subtrade");
			} else if(operType.equalsIgnoreCase("pay")){
				tabName = pub.yssGetTableName("tb_data_cashpayrec");
			}
			conn = dbl.loadConnection();
			/********************************************************
			 * 通过业务表的现金账户和组合代码去关联现金连接设置表
			 */
			//---------------------------------------------------------------------------------------------------------------
			sqlStr = " select c.*,d.fcashaccname from "+pub.yssGetTableName("Tb_Para_cashaccount")+" d right join" +
			         " (select nvl(b.fcashacccode,a."+field+") as fcashacccode ,b.fstartdate as fstartdate,b.fbrokercode as fbrokercode from (select * from "+tabName+" where fnum='"+tradeNum+"' and fcheckstate=1 "+filteSql+")a"+
			//---------------------------------------------------------------------------------------------------------------
			         " left join (select * from "+ pub.yssGetTableName("Tb_Para_Cashacclink")+" where fcheckstate=1 )b "+
			//---------------------------------------------------------------------------------------------------------------         
			         "on a."+field+"= b.fcashacccode and a.fportcode = b.fportcode)c on c.fcashacccode = d.fcashacccode  order by c.fstartdate desc";
			//---------------------------------------------------------------------------------------------------------------//
			//rs = dbl.openResultSet(sqlStr);
			Statement  st  =  conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);  //创建可回滚结果集
			if (st == null)
			{
				return;
			}
			rs= st.executeQuery(sqlStr);
			rs.last();
			int i = rs.getRow();
			rs.beforeFirst();
			while (rs.next()) {

				if(rs.getDate("fstartdate")== null){
					String cashaccname= rs.getString("fcashaccname");
					throw new YssException("请到【业务参数-业务功能-现金账户链接设置】页面，添加【账户："+cashaccname+" 】的现金账户链接设置");
				}
				//设置通过由大到小排序，然后判断启用日期<当前日期。
				if (YssFun.dateDiff(YssFun.toDate(date), rs.getDate("fstartdate")) > 0) {
					i--;
					continue;
				} else {
					brokerCode = rs.getString("fbrokercode");
					if (brokerCode.equalsIgnoreCase(" ")) {
						if(i==1){
						String cashaccname= rs.getString("fcashaccname");
						Date startDate = rs.getDate("fstartdate");
						throw new YssException("请到【业务参数-业务功能-现金账户链接设置】页面，设置【账户："+cashaccname  + ",启用日期："+ startDate + "】的券商代码");
						}
						i--;
						continue;
					} else {
						break;
					}
				}
			}
			if(st != null){
				st.close();
				st = null;
			}

			sqlStr = "select * from "
					+ pub.yssGetTableName("Tb_Para_AffiliatedCorp")
					+ " where faffcorpcode='" + brokerCode
					+ "' and fcheckstate=1 ";
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				if (YssFun.dateDiff(YssFun.toDate(date), rs.getDate("fstartdate")) > 0) {
					continue;
				} else {
					this.sBic = rs.getString("forgcode");
					if (this.sBic == null) {
						Date startDate = rs.getDate("fstartdate");
						throw new YssException("请到【业务参数-机构设置-关联机构设置】页面，设置【关联公司代码："+ brokerCode + ",启用日期："+ startDate + "】的机构代码");
					} else {
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	
    public String getOperValue(String sType) throws YssException {
        if (sType.equalsIgnoreCase("setting")) {
           getSetting();
        return buildRowStr();
       }
       if(sType.equalsIgnoreCase("getHeadEndInfo"))
       {
            getSetting();
            String sHeadEndInfo=getHeadEndInfo();
            return sHeadEndInfo;
       }
        return "";
    }
    /*
    **得到导出时的信息头和尾字串
    **
    **
    //string txt1 = "{1:F01ZYBGHKH0AXXX0693006461}{2:O3000555090526MMMMXXXX4A0800000004070905261355U}{3:{108:MT300 003 OF 033}}{4:"+"\r\n";
    //string txt2 = "-}{5:{CHK:AB7E8BC3EF86}{TNG:}}{S:{SPD:}{COP:P}}";
    */
    public String getHeadEndInfo() throws YssException {
        String sTxt1="";//文首区段
        String sTxt2="";//应用文首 输入电文/输出电文
        String sTxt3="";//用户文首
        String sTxt4="{4:\r\n";//内容
        String sTxt5="\r\n-}{5:{}{}}";//文尾 ???
        String sTxt="";
        try{
        	getSetting();
			// *文首区段
			// 1区段标识 2应用标识 3服务标识 4逻辑终端 5 线程序号//???//不拼入 6电文连续编号//???//不拼入
			sTxt1 = "{1:" + this.sAppTag + this.sServerTag
					+ getInfoData("logfinal") +getInfoData("getIndex")+ "}"; //add by jiangshichao 2010.02.28 导出时逻辑终端代码后应有10位的序列号，其中前四位为发送批次（同次发送的多个报文认为是同一批次），后六位为当前批次中的文件序列号
			// + "0000"+ "000000"+ "}";
			// *应用文首 分两种情况，一种是选择的 1:输入电文的应用文首2:输出电文的应用文首
			/*if (this.sGetSendTag.equalsIgnoreCase("I"))// 输入报文
			{// 1区段标识 2发、收电文标识(输入) 3电文种类 4收报人地址//??? 5电文等级 6传输监控
				// 7监控期间//???//取失效时间"003"
				sTxt2 = "{2:" + this.sGetSendTag + (this.sMessageType.startsWith("MT")?sMessageType.substring(2):sMessageType)
						+ "CITIUS33XXXX" + this.sPriorClass + this.sSendControl
						+ getInfoData("getInvalTime") + "}";
			} else if (this.sGetSendTag.equalsIgnoreCase("O"))// 输出报文
			{// 1区段标识 2发、收电文标识(输出) 3电文种类 4输入时间//???//当前"0930"
				// 5电文输入参考号//???//不输入"020608CITIUS33AXXX5409095542" 6输出日期7输出时间
				// 8电文等级
				sTxt2 = "{2:" + this.sGetSendTag + (this.sMessageType.startsWith("MT")?sMessageType.substring(2):sMessageType)
						+ getInfoData("getInTime") + getInfoData("getOutDate")
						+ this.sPriorClass + "}";
			}*/
			//以下{2:代码为重复生成，by leeyu 20100104
			sTxt2="{2:";//区段标识(Block Identifier)
			if("I".equalsIgnoreCase(this.sGetSendTag)){ //当为导入电文时
				//格式：{2： I 202 CITIUS33XXXX U 3 003}
				sTxt2=sTxt2+
				"I"+  //发、收电文标识(Input / Output Identifier)
				(this.sMessageType.startsWith("MT")?sMessageType.substring(2):sMessageType)+//电文种类(Message Type)
				"CITIUS33XXXX"+   //收报人地址(Destination Address)
				this.sPriorClass+ //电文等级(Message Priority)
				this.sSendControl+ //传输监控(Delivery Monitoring) (Optional)
				getInfoData("getInvalTime")+//监控期间(Obsolescence Period) (optional)
				"}";
			}else{
				//格式：{2： O 103 0930 020608CITIUS33AXXX5409095542 020609 0755 U}
				sTxt2=sTxt2+
				"O"+//发、收电文标识(Input / Output Identifier)
				(this.sMessageType.startsWith("MT")?sMessageType.substring(2):sMessageType)+//电文种类(Message Type)
				getInfoData("getInTime")+   //输入时间(Input Time)
				getInfoData("getOutDate")+getInfoData("logfinal")+getInfoData("getIndex")+
				//电文输入参考号(Message Input Reference, 简称MIR) 
				//MIR是一个28位的字符，其中：前六位为日期，中间十二位是“BIC+城市+终端代码”小于等于12位，在实际应用中“BIC+城市+终端代码”不足12位则以“X”补足；后面的10位，请在SWIFT报文生成时产生一个唯一的参考号
				getInfoData("getOutDate")+ //输出日期(Output Date)
				getInfoData("getInTime")+ //输出时间(Output Time)
				this.sPriorClass+   	//电文等级(Message Priority)
				"}";
			}
			// *用户文首 ???
			sTxt3 = "{3:{108:" + this.sSwiftIndex + "}}";
			
			if(this.sSwiftStatus.equalsIgnoreCase("CANC")||sSwiftStatus.equalsIgnoreCase("REDO")){ //若为撤消或重发的报文，则添加备注信息 by leeyu 20100104
				sTxt5="\r\n-}{5:{REDO:"+sSwiftDesc+"}{}}";
				
			}
			sTxt = sTxt1 + sTxt2 + sTxt3 + sTxt4 + "\r\r\r\r" + sTxt5;//传到前台后,把|k|k|n|k变为内容即可
        }catch(Exception ex){
        	throw new YssException(ex.getMessage(),ex);
        }
        return sTxt;
    }

    /*
     **得到导出时的信息具体数据的处理s
     **
     ***/

     public String getInfoData(String sType)  throws YssException {
         if(sType.equalsIgnoreCase("logfinal"))//逻辑终端 //文首区段 //界面控制城市字串不得大于12字串,全是大写
         {
             //逻辑终端        BIC    城市   终端代码
             //String sLogdata="BIC"+"GHK"+"H0AXXX";
             String sLogdata=this.sBic+this.sCity+this.sTerminalCode;
             String sfillX="";//要填入的X
             if(sLogdata.length() <12)
             {
                 int count=12-sLogdata.length();//要填入的X的数目
                 for(int i=0;i<count;i++)
                 {
                     sfillX+="X";
                 }
             }
             if(sLogdata.length() >12){
            	 sLogdata = sLogdata.substring(0, 12);
             }
             return sLogdata+sfillX;
         }

         if(sType.equalsIgnoreCase("getOutDate"))//得到输出日期和时间的字串
         {
             Calendar cal = Calendar.getInstance();
             SimpleDateFormat formatterDay = new SimpleDateFormat("yyMMdd");//这里只返回６位数据 by leeyu 20100104
             String sDayTime = formatterDay.format(cal.getTime());
             return (sDayTime);
         }
         if(sType.equalsIgnoreCase("getInvalTime"))//界面控制只能输入3个数字
         {
             if(this.sInvalTime.length()==3){
             return  this.sInvalTime;
             }
             if(this.sInvalTime.length()!=0&&this.sInvalTime.length()<3){
                 int count=3-this.sInvalTime.length();
                 String showZero="";
                 for(int i=0;i<count;i++)
                 {
                     showZero+="0";
                 }
                 return showZero+this.sInvalTime;
             }else{
            	 return sInvalTime.substring(sInvalTime.length()-3);//返回后三位 by leeyu 20100104
             }
         }
         if(sType.equalsIgnoreCase("getInTime")){//应用文首 输出报文 输入时间 取当前时间
             Calendar cal = Calendar.getInstance();
             SimpleDateFormat formatterDay = new SimpleDateFormat("HHmm");
             String sDayTime = formatterDay.format(cal.getTime());
             return (sDayTime);
         }
         if(sType.equals("getSwiftIndexDate") ){







         }
         if(sType.equalsIgnoreCase("getIndex")){//返回10位唯一序号
        	 if(this.sSwiftIndex!=null&& this.sSwiftIndex.trim().length()>0){
        		 if(sSwiftIndex.trim().length()>10){
        			 return sSwiftIndex.trim().substring(sSwiftIndex.trim().length()-10);
        		 }else{
        			 String sTmpStr="";
        			 for(int i=sSwiftIndex.trim().length();i<10;i++){
        				 sTmpStr+="0";
        			 }
        			 return sTmpStr+sSwiftIndex;
        		 }
        	 }else{
        		 return YssFun.formatDate(new java.util.Date(),"MMddHHmmss"); //若序号为空，返回一个日期时间数做为唯一序号
        	 }
         }
         return "";
     }

    public String getSUserRefer() {
        return sUserRefer;
    }

    public String getSTerminalCode() {
        return sTerminalCode;
    }

    public String getSServerTag() {
        return sServerTag;
    }

    public String getSSendControl() {
        return sSendControl;
    }

    public String getSPriorClass() {
        return sPriorClass;
    }

    public String getSMessageType() {
        return sMessageType;
    }

    public String getSInvalTime() {
        return sInvalTime;
    }

    public String getSGetSendTag() {
        return sGetSendTag;
    }

    public String getSFSwiftType() {
        return sFSwiftType;
    }

    public String getSCity() {
        return sCity;
    }

    public String getSBic() {
        return sBic;
    }

    public void setSAppTag(String sAppTag) {
        this.sAppTag = sAppTag;
    }

    public void setSUserRefer(String sUserRefer) {
        this.sUserRefer = sUserRefer;
    }

    public void setSTerminalCode(String sTerminalCode) {
        this.sTerminalCode = sTerminalCode;
    }

    public void setSServerTag(String sServerTag) {
        this.sServerTag = sServerTag;
    }

    public void setSSendControl(String sSendControl) {
        this.sSendControl = sSendControl;
    }

    public void setSPriorClass(String sPriorClass) {
        this.sPriorClass = sPriorClass;
    }

    public void setSMessageType(String sMessageType) {
        this.sMessageType = sMessageType;
    }

    public void setSInvalTime(String sInvalTime) {
        this.sInvalTime = sInvalTime;
    }

    public void setSGetSendTag(String sGetSendTag) {
        this.sGetSendTag = sGetSendTag;
    }

    public void setSFSwiftType(String sFSwiftType) {
        this.sFSwiftType = sFSwiftType;
    }

    public void setSCity(String sCity) {
        this.sCity = sCity;
    }

    public void setSBic(String sBic) {
        this.sBic = sBic;
    }

    public void setSSwiftStatus(String sSwiftStatus) {
        this.sSwiftStatus = sSwiftStatus;
    }

    public void setSFOldSwiftType(String sFOldSwiftType) {
        this.sFOldSwiftType = sFOldSwiftType;
    }

    public void setSOldSwiftStatus(String sOldSwiftStatus) {
        this.sOldSwiftStatus = sOldSwiftStatus;
    }

    public void setSSwiftIndex(String sSwiftIndex) {
        this.sSwiftIndex = sSwiftIndex;
    }

    public String getSAppTag() {
        return sAppTag;
    }

    public String getSSwiftStatus() {
        return sSwiftStatus;
    }

    public String getSFOldSwiftType() {
        return sFOldSwiftType;
    }

    public String getSOldSwiftStatus() {
        return sOldSwiftStatus;
    }

    public String getSSwiftIndex() {
        return sSwiftIndex;
    }
    //by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
	public void setsSwiftCode(String sSwiftCode) {
		this.sSwiftCode = sSwiftCode;
	}
	//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
	public String getsSwiftCode() {
		return sSwiftCode;
	}
}
