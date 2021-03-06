package com.yss.main.cashmanage;

import java.sql.*;
import java.util.*;
import java.util.Date;

import com.yss.commeach.*;
import com.yss.dsub.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.parasetting.*;
import com.yss.manager.*;
import com.yss.util.*;
import com.yss.vsub.YssFinance;


public class CommandTypeBean
    extends BaseDataSettingBean implements IDataSetting {
	private String FTranType = "";      //划款方式
	private String FSellWay = "";		//销售渠道
	private String FPortCode = "";		//投资组合代码
	private String FStatus = "";				//判别字
	private String FDataSource = ""; 	//数据来源
	private String FAlgFormula = "";	//计算公式
	private String FAlgDesc = "";		//计算描述
	
    private String sNum = "";
    private String sCommandDate = "";
    private String sCommandTime = "";
    private String order = "";
    private String accountDate = "";
    private String accountTime = "";
    private double dRate = 0;
    //private String receiverCode = "";
    private String receiverName = "";
    private String reOperBank = "";
    private String reAccountNO = "";
    private String reCuryCode = "";
    private String reCuryName = "";
    private double reMoney = 0;
    private String reChinese = "";
    private String cashUsage = "";
    private String desc = "";
    private String oldNum = "";
    private String oldOrder;

    private String payOperBank = "";
    private String payAccountNO = "";
    private String payCuryCode = "";
    private String payCuryName = "";
    private double payMoney = 0;
    //private String payCode = "";
    private String payName = "";
    private String payChinese = "";
    private String sRelaType = ""; //关联类型,新增字段 by ly 080220
    private String sRelaNum = ""; //关联编号,新增字段 by ly 080220

    private String portCode = "";
    private String portName = "";
    private String bShow = "0";
    private boolean bTransfer = false;
    private CommandTypeBean filterType = null;

    //-----------------------2008-4-30  单亮----------------
    private String sellData; //销售日期
    private String sellWayIndex; //销售渠道的索引
    private String payMoneyQuomodoIndex; //划款方式的索引
    private String payMoneyQuomodoValue; //销售渠道的值
    private String sellWayValue; //划款方式的值
    private double dMoneyTotal = 0; //销售总金额
    private double dbRollIn = 0; 	//转入金额
    private double dbRollOut = 0;	//转出金额
    private int flag = 0; // 一个标识，判断第几中类型的计算方式
    private String sSellType; //销售类型：01  申购 ,02  赎回,03  分红,04  转入,05  转出,06 赎回费,07 赎回后端费,08 转出费,09 转出后端费
    //------------------------------------------------------
    
    //edited by zhouxiang MS01628    关于招商基金需求之电子指令功能    20100917------
    private int orderType;			//指令类型
    private String transferType;	//划拨类型代码
    private String transferName;	//划拨类型名称
    private String eleDesc;			//电子划拨备注
    private String result;			//托管行结果
    private String remark;			//托管行备注
    private String fbwtype;			//报文类型
    //end------------------------------------------------------------------------
	//story 1645 by zhouwei 20111129 QDII工银2011年9月13日10_A
	private String feState="0";//默认“0” 外汇交收标示（不勾选）
	private String otherAccountDate="1900-01-01";//对方划款日期
	private String otherAccountTime="";//对方划款时间
	private String otherpayName="";//对方付款人
	private String otherpayOperBank="";//对方付款银行
	private String otherpayAccountNO="";//对方付款账户 
	private String otherReceiverName="";//对方收款人
	private String otherreOperBank = "";//对方收款人银行
	private String otherreAccountNO = "";//对方收款人账号
	//-------------end----story 1645 QDII工银2011年9月13日10_A
    //BugNo:0000389 edit by jc
    private double dSellMoney; //销售金额(净金额)

    private String sRecycled = ""; //回收站

    private double DSellMoney;
    
    private String fDS="0"; //add by wuweiqi 

    private String batchStr;//story 1842 by zhouwei 20111115 存放批量操作的数据

    //add by huangqirong 2012-04-13 story #2326
    private String gCSState =" " ; 
    
    private String tradeUsageCode = " ";
    
    private String numType = "";
    
    private String gcsNum = " " ;
    
    //add by huangqirong 2013-01-30 story #3510
    private String cashAccountCodes = ""; 
    
    public String getCashAccountCodes() {
		return cashAccountCodes;
	}

	public void setCashAccountCodes(String cashAccountCodes) {
		this.cashAccountCodes = cashAccountCodes;
	}
	//---end---

	public String getGcsNum() {
		return gcsNum;
	}

	public void setGcsNum(String gcsNum) {
		this.gcsNum = gcsNum;
	}

	public String getNumType() {
		return numType;
	}

	public void setNumType(String numType) {
		this.numType = numType;
	}

	public String getgCSState() {
		return gCSState;
	}
    
    public void setgCSState(String gCSState) {
		this.gCSState = gCSState;
	}
    
    public String getTradeUsageCode (){
    	return this.tradeUsageCode;
    }
    
    public void setTradeUsageCode (String tradeUsageCode){
    	this.tradeUsageCode = tradeUsageCode ;
    }
    
    public String getFTranType() {
		return FTranType;
	}

	public void setFTranType(String fTranType) {
		FTranType = fTranType;
	}

	public String getFAlgFormula() {
		return FAlgFormula;
	}
    //---end---

	public void setFAlgFormula(String fAlgFormula) {
		FAlgFormula = fAlgFormula;
	}

	public String getFAlgDesc() {
		return FAlgDesc;
	}

	public void setFAlgDesc(String fAlgDesc) {
		FAlgDesc = fAlgDesc;
	}
	
	
	public String getFSellWay() {
		return FSellWay;
	}

	public void setFSellWay(String fSellWay) {
		FSellWay = fSellWay;
	}

	public String getFPortCode() {
		return FPortCode;
	}

	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}
	
	public String getFStatus() {
		return FStatus;
	}

	public void setFStatus(String fStatus) {
		FStatus = fStatus;
	}

	public String getFDataSource() {
		return FDataSource;
	}

	public void setFDataSource(String fDataSource) {
		FDataSource = fDataSource;
	}


	public CommandTypeBean() {
    }

    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Cash_Command"),
                               "FNum", this.sNum, this.oldNum);
    }

    /**
     * add by songjie
     * 2011.05.13
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * 资产估值生成净值统计数据后，根据费用划款指令设置_通用参数
     * 生成费用对应的划款指令数据
     * @throws YssException
     */
    public void addCommandOfValFee()throws YssException{
    	try{
    		
    	}catch(Exception e){
    		throw new YssException("新增划款指令出错", e);
    	}finally{
    		
    	}
    }
    
    public String addSetting() throws YssException {
        Connection conn = null;
        String sqlStr = "";
        ResultSet rs = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            sqlStr = "insert into " + pub.yssGetTableName("TB_Cash_Command_Type") +
                " (FTranType,FSellWay,FPortCode,FStatus,FDataSource,FAlgFormula,FAlgDesc" +
                ") values(" +
                dbl.sqlString(this.FTranType) + "," +
                dbl.sqlString(this.FSellWay) + "," +
                dbl.sqlString(this.FPortCode) + "," +
                '0'+ "," +
                dbl.sqlString(this.FDataSource) + "," +
                dbl.sqlString(this.FAlgFormula) + "," +
                dbl.sqlString(this.FAlgDesc) + 
                 ")";
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
			e.printStackTrace();
        }
        finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return "";
    }

	public String editSetting() throws YssException {
        Connection conn = null;
        ResultSet rs = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            //sqlStr = "select * from " + pub.yssGetTableName("Tb_Cash_Command") +
            //" where FNum=" + dbl.sqlString(this.sNum) +
            //" and FOrder =" + this.order + " and FOrder !=" + this.oldOrder;
            sqlStr = "select * from " + pub.yssGetTableName("Tb_Cash_Command") +
                // " where FNum=" + dbl.sqlString(Nums);
                " where FOrder =" + dbl.sqlString(order) + " and FNum like '" +
                YssFun.left(sCommandDate, 4) + "%'" + //这里的编号按年来区分,与二版保持一致 by liyu 080530
                " and FOrder !=" + this.oldOrder
                + "AND FPortCode = " + dbl.sqlString(this.portCode);//shashijie 2011.03.18 TASK #3131::跨入新的一年时，每个组合的指令序号都要重新从1开始
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                throw new YssException("此指令序号已经被占用，请另选择一个新指令序号");
            }
            sqlStr = "update " + pub.yssGetTableName("Tb_Cash_Command") +
                " set FNum=" + dbl.sqlString(this.sNum) + "," + //增加
                "FOrder=" + this.order + "," + //增加 by ly 080526
                "FCommandDate=" + dbl.sqlDate(this.sCommandDate) + "," +
                "FCommandTime=" + dbl.sqlString(this.sCommandTime) + "," +
                "FAccountDate=" + dbl.sqlDate(this.accountDate) + "," +
                "FAccountTime=" + dbl.sqlString(this.accountTime) + "," +
                "FPayerName=" + dbl.sqlString(this.payName) + "," +
                "FPayerBank=" + dbl.sqlString(this.payOperBank) + "," +
                "FPayerAccount=" + dbl.sqlString(this.payAccountNO) + "," +
                "FPayCury=" + dbl.sqlString(this.payCuryCode) + "," +
                "FPayMoney=" + this.payMoney + "," +
                "FRefRate=" + this.dRate + "," +
                "FPortCode=" + dbl.sqlString(this.portCode) + "," +
                "FRelaNum=" + dbl.sqlString(this.sRelaNum) + "," +
                "FNumType=" + dbl.sqlString(this.sRelaType) + "," +
                "FRecerName=" + dbl.sqlString(this.receiverName) + "," +
                "FRecerBank=" + dbl.sqlString(this.reOperBank) + "," +
                "FRecerAccount=" + dbl.sqlString(this.reAccountNO) + "," +
                "FRecCury=" + dbl.sqlString(this.reCuryCode) + "," +
                "FRecMoney=" + this.reMoney + "," +
                "FCashUsage=" + dbl.sqlString(this.cashUsage) + "," +
                "FDesc=" + dbl.sqlString(this.desc) + "," +
                
                //edited by zhouxiang MS01628    关于招商基金需求之电子指令功能  -------
                "Fzltype="+this.orderType+","+
                "Fhktype="+dbl.sqlString(this.transferName)+","+
                "FHkRemarkN="+dbl.sqlString(this.eleDesc)+","+
                //-------------end---------20100917----------------------------------
                /**shashijie 2011.05.27,BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称*/
                " FHKcode = "+dbl.sqlString(this.transferType)+","+
                /**end*/
                 //story 1645 by zhouwei 20111129 QDII工银2011年9月13日10_A
                "FOTHERACCOUNTDATE="+dbl.sqlDate(this.otherAccountDate)+","+
                "FOTHERACCOUNTTIME="+dbl.sqlString(this.otherAccountTime)+","+
                "FOTHERPAYERNAME="+dbl.sqlString(this.otherpayName)+","+
                "FOTHERPAYERBANK="+dbl.sqlString(this.otherpayOperBank)+","+
                "FOTHERPAYERACCOUNT="+dbl.sqlString(this.otherpayAccountNO)+","+
                "FOTHERRECERNAME="+dbl.sqlString(this.otherReceiverName)+","+
                "FOTHERRECERBANK="+dbl.sqlString(this.otherreOperBank)+","+
                "FOTHERRECERACCOUNT="+dbl.sqlString(this.otherreAccountNO)+","+
                "FFEXCHANGESTAT="+dbl.sqlString(this.feState)+","+
                //----end---
                "FCheckState=" + (pub.getSysCheckState() ? "0" : "1") + "," +
                "FCreator=" + dbl.sqlString(this.creatorCode) + "," +
                "FCreateTime=" + dbl.sqlString(this.creatorTime) + "," +
                "FCheckUser=" +
                (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
                " where FNum =" + dbl.sqlString(this.oldNum);
            dbl.executeSql(sqlStr);
            //修改资金调拨
            deleCashTransfer(oldNum);
            if (bTransfer) {
                this.addTransfer(this.oldNum);
            }

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            rs.close();
        } catch (Exception e) {
            try {
            	/**shashijie 2012-7-2 STORY 2475 */
				if (conn != null) {
					conn.rollback();
				}
				/**end*/
            } catch (Exception sqle) {
            }
            throw new YssException("修改划款指令出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
        return "";

    }

    
    public void delSetting() throws YssException {
        ResultSet rs = null;
        boolean bTrans = false;
        String reStr = "";
    	String strSql = ""; //定义一个字符串来放SQL语句
        //获取一个连接
        Connection conn = dbl.loadConnection();
        try {
        	bTrans = true;
        	strSql = "delete from " +
        			pub.yssGetTableName("Tb_Cash_Command_Type") +
        			" where FTranType = " +
        			dbl.sqlString(this.FTranType) +
        			" and FPortCode = " + dbl.sqlString(this.FPortCode) +
        			" and FSellWay = " + dbl.sqlString(this.FSellWay);

        	//执行sql语句
            dbl.executeSql(strSql);
            conn.commit(); //提交事物
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("删除自定义划款方式出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans); //释放资源
        }
    }

    /**
     * checkSetting
     * 功能：可以处理划款业务设置审核、未审核、和回收站的还原功能、还可以同时处理多条信息
     * @throws YssException
     * 修改时间：2008年6月4号
     * 修改人：蒋春
     * story 1842 by zhouwei 20111115 批量处理数据
     */
    public void checkSetting() throws YssException {
//      Connection conn = null;
//      boolean bTrans = false;
//      ResultSet rs = null;
//      String sqlStr = "";
//      try {
//         conn = dbl.loadConnection();
//         conn.setAutoCommit(false);
//         bTrans = true;
//         sqlStr = "update " + pub.yssGetTableName("Tb_Cash_Command") +
//               " set FCheckState=" + this.checkStateId + "," +
//               " FCheckUser=" +
//               (pub.getSysCheckState() ? "' '" : dbl.sqlString(this.creatorCode)) +
//               " ,FCheckTime = '" + YssFun.formatDatetime(new java.util.Date()) +
//               "' where FNum =" + dbl.sqlString(this.sNum);
//         dbl.executeSql(sqlStr);
//         //-------------审核资金调拨
//         sqlStr = "select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
//               " where FNumType='Common' and FRelaNum=" +
//               dbl.sqlString(this.sNum);
//         rs = dbl.openResultSet(sqlStr);
//         while (rs.next()) {
//            sqlStr = " update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
//                  " set FCheckState=" + this.checkStateId +
//                  " where FNum =" + dbl.sqlString(rs.getString("FNum"));
//            dbl.executeSql(sqlStr);
//         }
//         sqlStr = " update " + pub.yssGetTableName("Tb_Cash_Transfer") +
//               " set FCheckState=" + this.checkStateId +
//               " where FNumType='Common' and FRelaNum=" +
//               dbl.sqlString(this.sNum);
//         dbl.executeSql(sqlStr);
//         //----------------
//         conn.commit();
//         bTrans = false;
//         conn.setAutoCommit(true);
//      }
//      catch (Exception e) {
//         throw new YssException("审核收款人设置出错", e);
//      }
//      finally {
//         dbl.endTransFinal(conn, bTrans);
//      }

        String strSql = "";
        String[] arrData = null;
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务
        //获取一个连接
        Connection conn = dbl.loadConnection();
        String returnFnum="";
        try {
        	boolean yssException=false;//判断是否有指令已发送       	
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (batchStr != "" && batchStr != null) {   //story 1842 by zhouwei 2011115
                //根据规定的符号，把多个sql语句分别放入数组
                //arrData = sRecycled.split("\r\n");
            	arrData = batchStr.split("\f\f\f\f");
                conn.setAutoCommit(false);
                bTrans = true;
                //循环执行这些语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    //begin-------- MS01628 2010.09.27 关于招商基金需求之电子指令功能   ---------------------
                    String table=" A"+getBookSetByPortcode(this.portCode)+"JjHkZl ";
                    if(this.checkStateId==0){
                        addTable(table);  //add by zhaoxianlin 20130109 BUG6859建行026组合群新建划款指令，反审核的时候报错 
                    	String checkSend="select nvl(result,'null') as result from "+table+" where fnum="+dbl.sqlString(this.sNum);
                    	rs=dbl.openResultSet(checkSend);
                    	if(rs.next()&&!(rs.getString("result").indexOf("作废") > -1)){//如果返回的结果没有‘作废’标记则不能进行反审核操作
                    		//throw new YssException("指令已经发送，不能进行反审核操作");
                    		if(i%3==0){
                    			if(returnFnum.equals("")){
                    				returnFnum="<"+this.sNum;
                    			}else{
                    				returnFnum=returnFnum+"\r\n<"+this.sNum;
                    			}
                    		}else{
                    			if(returnFnum.equals("")){
                    				returnFnum="<"+this.sNum;
                    			}else{
                    				returnFnum=returnFnum+",<"+this.sNum;
                    			}
                    		}
                    		returnFnum+=">";
                    		yssException=true;
                    	}
                    	
                    	//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                    	dbl.closeResultSetFinal(rs);
                    	
                    	 strSql=" delete from  "+table+" where FNum="+dbl.sqlString(this.sNum);
                    	 dbl.executeSql(strSql);
                    }
                    //end-----------对于已经发送的数据不允许反审核-----------------------------------------
                   //story 1911 by zhouwei 20111229 QDV4招商基金2011年11月22日01_A  
                    //在［审核］划款指令时，须检查“收款人”、“付款人”是否均有值
                    if(this.checkStateId==1){
                	   String sql="select * from "+pub.yssGetTableName("tb_cash_command")+" where (fpayername=' ' or frecername=' ' or fcashusage=' ')"
                		   +" and fnum="+dbl.sqlString(this.sNum);
                	   rs=dbl.openResultSet(sql);
                	   if(rs.next()){
                		   if(returnFnum.equals("")){
                			   returnFnum="指令日期\t指令时间 \t指令序号\r\n";
                		   }
                		   returnFnum=returnFnum+YssFun.formatDate(rs.getDate("FCOMMANDDATE"), "yyyy-MM-dd")+"\t"+rs.getString("FCOMMANDTIME")
                		              +"\t"+rs.getString("FORDER")+"\r\n";
                		   yssException=true;
                	   }
                   }
                    //-----------------------end------------------
                    //by zhouwei 20111129 story 1842
                    if(this.checkStateId==1){//审核时判断数据是否被删除
                    	this.checkInput(YssCons.OP_AUDIT);
                    }
                    //----end----
                    //先审核资金调拨
                    strSql = "select FNum from " +
                        pub.yssGetTableName("Tb_Cash_Transfer") +
                        " where FNumType='Common' and FRelaNum=" +
                        dbl.sqlString(this.sNum);
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        strSql = " update " +
                            pub.yssGetTableName("Tb_Cash_SubTransfer") +
                            " set FCheckState=" + this.checkStateId +
                            " where FNum =" + dbl.sqlString(rs.getString("FNum"));

                        //执行sql语句
                        dbl.executeSql(strSql);
                    }
                    
                	//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                	dbl.closeResultSetFinal(rs);
                    
                    strSql = " update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                        " set FCheckState=" + this.checkStateId +
                        " where FNumType='Common' and FRelaNum=" +
                        dbl.sqlString(this.sNum);

                    //执行sql语句
                    dbl.executeSql(strSql);

                    strSql = "update " + pub.yssGetTableName("Tb_Cash_Command") +
                        " set FCheckState=" + this.checkStateId + "," +
                        " FCheckUser=" +
                        //edited by zhouxiang MS01628 电子划拨指令审核人没有显示
                        //edit by songjie 2011.07.18 BUG 2267 QDV4光大2011年07月13日01_B
                        (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode):"' '" )
                          +
                        " ,FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FNum =" + dbl.sqlString(this.sNum);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而sNum不为空，则按照sNum来执行sql语句
            else {
                if (this.sNum != "" && this.sNum != null) {
                    strSql = "update " + pub.yssGetTableName("Tb_Cash_Command") +
                        " set FCheckState=" + this.checkStateId + "," +
                        " FCheckUser=" +
                        (pub.getSysCheckState() ? "' '" :
                         dbl.sqlString(this.creatorCode)) +
                        " ,FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "' where FNum =" + dbl.sqlString(this.sNum);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            if(yssException==true){
            	throw new YssException(returnFnum);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
        	if(this.checkStateId==1){
        		 //story 1911 by zhouwei 20111229 QDV4招商基金2011年11月22日01_A
        		if(!returnFnum.equals("")){
        			throw new YssException(""+e.getMessage(),e);
        		}else{
        			 throw new YssException("审核划款业务信息出错\r\n" + e.getMessage(), e);
        		}        	   
        	}else{
        		throw new YssException(""+e.getMessage(),e);
        	}        
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }
/**
	 * 增加划款指令表add by zhaoxianlin 20130109 BUG6859建行026组合群新建划款指令，反审核的时候报错 
	 */
	public void addTable(String tableName) throws YssException{
		String strSql = "";
		boolean bTrans = false; //代表是否开始了事务
		Connection conn = dbl.loadConnection();
		try{
			if(dbl.yssTableExist(tableName.trim())){
				return;
			}
	            conn.setAutoCommit(bTrans);
	            bTrans = true;
    			strSql = " create table "+tableName+" (FZLDATE  DATE not null,FHKDATE  DATE not null," + 
   			   		 " FNUM           VARCHAR2(20) not null, FDZDATE        DATE not null, FHKREN         NVARCHAR2(100) not null," +
   			   		 " FHKBANK        NVARCHAR2(100) not null, FHKACCT        NVARCHAR2(100) not null, FHKJE          NUMBER(18,4) not null,"+
   			   	     " FHKREMARK      NVARCHAR2(200) not null, FSKREN         NVARCHAR2(100) not null,  FSKBANK        NVARCHAR2(100) not null," + 
   			   		 " FSKACCT        NVARCHAR2(200) not null, FSKYT          NVARCHAR2(200) not null," +
   			   		 " FDELBZ         CHAR(1),FZLTYPE        NUMBER(1) not null," +
   			   		 " FHKTYPE        VARCHAR2(10) default ' ',FHKTYPE2       VARCHAR2(50) default ' ' not null," +
   			   		 " FSN            NVARCHAR2(30),SEQ_NO         NVARCHAR2(50) not null," +
   			   		 " RESULT         NVARCHAR2(30),REMARK         NVARCHAR2(200)," +
   			   		 " CHECKER_CODE   NVARCHAR2(30),FPK_BOOKMARK   NVARCHAR2(100)," +
   			   		 " TIMESTMP       NVARCHAR2(50) not null,OPERATION_TYPE NVARCHAR2(50) not null," +
   			   		 " FYHSN          NVARCHAR2(50),FSH            NVARCHAR2(30) not null," +
   			   		 " FZZR           VARCHAR2(30) not null,FCHK           VARCHAR2(30) not null," +
   			   		 " FHKREMARKN     NVARCHAR2(200))";
               dbl.executeSql(strSql);
               strSql = "alter table "+tableName+ " add constraint PK_"+tableName.trim()+" primary key (FNUM, FSH, SEQ_NO) ";
               dbl.executeSql(strSql);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
		}catch(Exception e){
			throw new YssException("创建划款指令表出错！", e);
		}finally {
			dbl.endTransFinal(conn, bTrans);
		}
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

    public String getTreeViewData1() throws YssException {
        return "";
    }

    public String getTreeViewData2() throws YssException {
        return "";
    }

    public String getTreeViewData3() throws YssException {
        return "";
    }
    
    

    public String getListViewData1() throws YssException {
    	String sShowDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try{
        	strSql =
        		"select * from "+ pub.yssGetTableName("Tb_Cash_Command_Type")+
        		" where FTranType = "+dbl.sqlString(this.FTranType);
        		;
        	rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs,
                    this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                setAttr(rs); 
                bufAll.append(this.buildRowStr()).append(YssCons.
                    YSS_LINESPLITMARK);
            }
        if (bufShow.toString().length() > 2) {
            sShowDataStr = bufShow.toString().substring(0,
                bufShow.toString().length() - 2);
        }
        String []arrStr = sShowDataStr.split("\t");
    	return  arrStr[0];
        }catch (Exception e) {
            throw new YssException("获取自定义划款指令方式相关的信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    
    public String getListViewData3() throws YssException {
        String strSql = "";
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try{
        	strSql =
        		"select * from "+ pub.yssGetTableName("Tb_Cash_Command_Type")+
        		" where FTranType = "+dbl.sqlString(this.FTranType) + 
        		" and FPortCode = "+dbl.sqlString(this.FPortCode) +
        		" and FSellWay = "+dbl.sqlString(this.FSellWay);
        		
        	rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	 setAttr(rs); 
                 bufAll.append(this.buildRowStr());
            }

    	return bufAll.toString() ;
        }catch (Exception e) {
            throw new YssException("获取自定义划款指令方式相关的信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }
    
    public String getBeforeEditData() throws YssException {
        return "";
    }
    
    public String getListViewData4() throws YssException {
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        try{
        	strSql =
        		"select FTranType from "+ pub.yssGetTableName("Tb_Cash_Command_Type")+
        		" where FSellWay = "+dbl.sqlString(this.FSellWay) +" and FPortCode = "+dbl.sqlString(this.FPortCode);
        	rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	bufShow.append(rs.getString("FTranType")+"\t");
            }
        
        String reStr = bufShow.toString();
    	return  reStr;
        }catch (Exception e) {
            throw new YssException("获取划款方式出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
    }

    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null;
        try {
            if (sRowStr.equals("")) {
                return;
            }

            reqAry = sRowStr.split("\t");
            this.FTranType = reqAry[0];
            this.FSellWay = reqAry[1];
            this.FPortCode = reqAry[2];
            this.FDataSource = reqAry[4];
            this.FAlgFormula = reqAry[5];
            this.FAlgDesc = reqAry[6];
//            super.parseRecLog();
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new CommandTypeBean();
                    this.filterType.setYssPub(pub);
                }
                this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
            }
        } catch (Exception e) {
            throw new YssException("解析划款指令出错", e);
        }
    }

    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.FTranType).append("\t");
        buf.append(this.FSellWay).append("\t");
        buf.append(this.FPortCode).append("\t");
        buf.append(this.FStatus).append("\t");
        buf.append(this.FDataSource).append("\t");
        buf.append(this.FAlgFormula).append("\t");
        buf.append(this.FAlgDesc).append("\t");

//        buf.append(super.buildRecLog());
        return buf.toString();
    }

    private void setAttr(ResultSet rs) throws SQLException {
        this.FTranType = rs.getString("FTranType");
        this.FSellWay = rs.getString("FSellWay");
        this.FPortCode = rs.getString("FPortCode");
        this.FStatus = rs.getString("FStatus");
        this.FDataSource = rs.getString("FDataSource");
        this.FAlgFormula = rs.getString("FAlgFormula");
        this.FAlgDesc = rs.getString("FAlgDesc");
//        super.setRecLog(rs);
    }

    private String buildFilterSql() throws YssException {
        String sqlStr = "";
        //20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
        //===================================
		if(pub.isBrown()==true)
			return " where 1=1";
        //==============end=====================
        try {
            if (this.filterType != null) {
                sqlStr = " where 1=1";
                //xuqiji 20100318 QDV4赢时胜上海2010年03月17日06_B MS00884
                if (this.filterType.bShow.equalsIgnoreCase("1") && pub.isBrown()==false) {	//20111027 modified by liubo.STORY #1285.
                	sqlStr = sqlStr + " and 1=2 ";
                    return sqlStr;
                }
                //--------------------------end 20100318----------------//
                if (this.filterType.sNum != null &&
                    this.filterType.sNum.trim().length() != 0) {
                    sqlStr += " and a.FNum like '" +
                        filterType.sNum.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.sCommandDate != null &&
                    !this.filterType.sCommandDate.equals("9998-12-31")) {
                    sqlStr += " and a.FCommandDate =" +
                        dbl.sqlDate(filterType.sCommandDate);
                }
                //add by guojianhua   20100902   MS01676    划款指令界面只能按照日期进行筛选    QDV4赢时胜(32上线测试)2010年8月30日03_B    
                if(this.filterType.portCode != null &&
                	this.filterType.portCode.trim().length()!=0){
                	sqlStr +=" and a.FPortCode ="+
                	dbl.sqlString(filterType.portCode);
                }//组合
                if(this.filterType.payCuryCode!= null &&
                		this.filterType.payCuryCode.trim().length()!=0){
                	sqlStr +=" and a.FPayCury ="+
                	dbl.sqlString(filterType.payCuryCode);
                }//付款币种代码
               if(this.filterType.payOperBank !=null &&
            		   this.filterType.payOperBank.trim().length()!=0){
            	   sqlStr +=" and a.FPayerBank ="+
            	   dbl.sqlString(filterType.payOperBank);
               }//付款人开户银行
               if(this.filterType.payAccountNO !=null &&
            		   this.filterType.payAccountNO.trim().length()!=0){
            	   sqlStr +=" and a.FPayerAccount ="+
            	   dbl.sqlString(filterType.payAccountNO);
               }//付款人账号
               
               if(this.filterType.reOperBank !=null &&
            		   this.filterType.reOperBank.trim().length() !=0){
            	   sqlStr +=" and a.FRecerBank ="+
            	   dbl.sqlString(filterType.reOperBank);
               }//收款人开户银行
               if(this.filterType.reAccountNO !=null &&
            		   this.filterType.reAccountNO.trim().length() !=0){
            	   sqlStr +=" and a.FRecerAccount ="+
            	   dbl.sqlString(filterType.reAccountNO);
               }//收款人账号
          //------------------end-------------------------------
                	
                
                if (this.filterType.accountDate != null &&
                    !filterType.accountDate.equals("9998-12-31")) {
                    sqlStr += " and a.FAccountDate =" +
                        dbl.sqlDate(filterType.accountDate);
                }
                if (this.filterType.receiverName != null &&
                    this.filterType.receiverName.length() > 0) {
                    sqlStr += " and a.FRecerName like '" +
                        filterType.receiverName.replaceAll("'", "''") + "%'";
                }
                if (this.filterType.payName != null &&
                    this.filterType.payName.length() > 0) {
                    sqlStr += " and a.FPayerName like '" +
                        filterType.payName.replaceAll("'", "''") + "%'"; ;
                }
                if (filterType.reCuryCode != null &&
                    filterType.reCuryCode.length() > 0) {
                    sqlStr += "and a.FRecCury = " + dbl.sqlString(filterType.reCuryCode); //--- modify by wangzuochun  2010.05.28 MS01198 划款指令页面下新建一条信息没有成功显示列表框中  QDV4赢时胜(测试)2010年5月14日4_B
                }
                //begin zhouxiang MS01628    关于招商基金需求之电子指令功能    --------
				if (filterType.transferType != null
						&& filterType.transferType.length() > 0) {
					sqlStr += " and a.FHKcode = " //shashijie 2011.05.27,BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称
							+ dbl.sqlString(filterType.transferType);
				}
				if (filterType.eleDesc != null
						&& filterType.eleDesc.length() > 0) {
					sqlStr += " and a.FHkRemarkN="
							+ dbl.sqlString(filterType.eleDesc);
				}
				//add by huangqirong 2012-04-13 story #2326
				if (this.filterType.gCSState != null
						&& this.filterType.gCSState.trim().length() > 0 && !this.filterType.gCSState.trim().equalsIgnoreCase("000")) {
					sqlStr += " and a.FGCSState="
							+ dbl.sqlString(this.filterType.gCSState);
				}
				//---end---
				//modify by fangjiang 2010.11.06 BUG #303 无法查到划款指令历史数据
//				if (isShowCashType() == true) {
//					sqlStr+=" and ( a.Fzltype=" + dbl.sqlString(filterType.orderType + "") + " or a.fzltype is null ) ";
//				}
				//------------------------------
                //end--------------------------------------------------------------
            }
            return sqlStr;
        } catch (Exception e) {
            throw new YssException("筛选划款指令出错", e);
        }
    }
    /***
     * add by wuweiqi 20101208 QDV4工银2010年11月1日01_A  
     * 添加轧差划款指令
     * @return
     * @throws YssException
     */
    //edit by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
    //添加String portcodes参数
    public String addDictate(String portcodes) throws YssException {
        Connection conn = null;
        String sqlStr = "";
        String Nums = "";
        boolean bTrans = false;
        this.creatorTime=YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");//获取当前系统时间
        this.dRate=1 ;//默认参考汇率
        this.accountTime="00:00:00";
        this.sCommandTime="00:00:00";
        this.checkTime=YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");//审核时间
        this.creatorCode=pub.getUserCode();
        this.cashUsage=(this.portName+"  "+"应付轧差款金额："+this.payMoney+"元").toString();//划款用途  BUG #1041 要求更改为可以同时选择两个接口读取数据也能够生成划款指令 
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans = true;
            
            Nums = YssFun.formatDate(this.sCommandDate, "yyyyMMdd") + this.order;
            
            //edit by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
            //edit by songjie 2011.07.14 BUG 2263 QDV4国泰2011年7月12日01_B 
            //在跨入新的一年的时候，每个组合的指令序号都要重新从1开始
            this.order = dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Command"), "FOrder", "1", " where FNum like '" + YssFun.left(Nums, 4) + "%'");

            sqlStr = "insert into " + pub.yssGetTableName("Tb_Cash_Command") +
                " (FNum,FCommandDate,FCommandTime,FAccountDate,FAccountTime,FPayerName,FOrder," +
                " FPayerBank,FPayerAccount,FPayCury,FPayMoney,FRefRate,FRecerName,FRecerBank," +
                " FRecerAccount,FRecCury,FRecMoney,FCashUsage,FDesc,FPortCode,FRelaNum,FNumType,FCheckState," +
                " FCreator,FCreateTime,FCheckUser,fzltype,fhktype, fhkremarkn,fds,fchecktime " +
                " , FHKcode "+//shashijie,2011.05.27,BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称
                ") values(" +
                //--------------------start -----------------------------------------------
                dbl.sqlString(Nums) + "," + //不处理
                dbl.sqlDate(this.sCommandDate) + "," +//指令时间
                dbl.sqlString(this.sCommandTime) + "," +//不处理
                dbl.sqlDate(this.accountDate) + "," +//到帐日期
                dbl.sqlString(this.accountTime) + "," +//不处理
                dbl.sqlString(this.payName) + "," +//付款人名称
                this.order + "," +//不处理
                dbl.sqlString(this.payOperBank) + "," +//银行
                dbl.sqlString(this.payAccountNO) + "," +//帐户
                dbl.sqlString(this.payCuryCode) + "," +//币种
                this.payMoney + "," +//  付款金额     总赎回金额减去总申购金额的值
                this.dRate + "," +//不处理
                dbl.sqlString(this.receiverName) + "," +//收款人名称
                dbl.sqlString(this.reOperBank) + ","+ //银行
                dbl.sqlString(this.reAccountNO) + "," +//帐户
                dbl.sqlString(this.reCuryCode) + "," +//币种
                this.reMoney + "," + //YssD.mul(this.dRate,this.payMoney)+","+//收款金额等于付款金额
                dbl.sqlString(this.cashUsage) + "," +//不处理
                dbl.sqlString(this.desc) + "," +//不处理
                //edit by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B 将 this.portcode 改为 portcodes
                dbl.sqlString(portcodes) + "," +//不处理
               
                dbl.sqlString(this.sRelaNum) + "," + //可为空 
                dbl.sqlString(this.sRelaType) + "," + //可为空
                (pub.getSysCheckState() ? "1" : "0") + "," +// modify by guyichuan 20110401  #756::用户要求系统自动生成的划款指令默认状态改成“已审核”
                dbl.sqlString(this.creatorCode.length() == 0 ? " " : 
                              this.creatorCode) + "," +
                dbl.sqlString(this.creatorTime.length() == 0 ? " " :
                              this.creatorTime) + "," +   
                (pub.getSysCheckState() ? dbl.sqlString(this.creatorCode) : " " ) +        
                 " , "+
                 this.orderType+" , "+
                 dbl.sqlString(this.transferName)+","+
                 dbl.sqlString(this.eleDesc)+","+
                 dbl.sqlString(this.fDS)+","+ //标示通过接口自动生成划款指令
//                 dbl.sqlString(pub.getAssetGroupCode())+","+
                 dbl.sqlString(this.checkTime)+
                 //-----------------end--------------------------------------------
                 /**shashijie,2011.05.27,BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称*/
                 " , "+dbl.sqlString(this.transferType)+
                 /**end*/
                 ")";
            dbl.executeSql(sqlStr);
            conn.commit();
            //添加资金调拨
            if (bTransfer) {
                this.addTransfer(Nums);
            }
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
			 //modify by wangzuochun 2010.11.18 BUG #446 新增划款指令，提示信息不友好，且新增失败时，不产生资金调拨和不保存划款指令。 
        	if (e.getMessage().indexOf("生成资金调拨不成功") > 0){
        		throw new YssException(e.getMessage());
        	}
        	else{
        		throw new YssException("新增划款指令出错", e);
        	}
			//--------------------BUG #446-------------------//
        }
        finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }
    /***
     * add by wuweiqi 20101208 QDV4工银2010年11月1日01_A  
     * 判断是否需要自动生成划款指令并获取划款指令信息
     * @param portCode
     * @return
     */
    public String getDictate ()throws YssException  {
		String bookset = "001";
		String sql1="";
		String sql2="";
		String sql="";
		ResultSet rs = null;
        java.util.Date tradeDate=null;//日期格式的指令日期
		double tradeMoney=0;//申购总金额
		double tradeMoney2=0;//赎回总金额
		String settleDate="";//结算数据日期
		this.fDS="1";//通过接口生成划款指令有FDS标示为1
		String holiday="";//节假日
		//add by songjie 2011.02.23 BUG:1106 QDV4工银2011年02月15日02_B
		HashMap hmPortType = null;
		try {
			//1.查询是否满足条件 申购总金额 < 赎回总金额
			//2.满足后查询并删除划款指令  按照  fds="1" and 当天的指令日期
			//3.删除后生成轧差指令 可以调用addSetting1()方法
             //从TA现金结算连接设置界面中节假日群中获取节假日群代码
            BaseOperDeal deal = new BaseOperDeal();
            deal.setYssPub(pub);
    		sql1="select fholidayscode from " +
			     pub.yssGetTableName("Tb_TA_CashSettle ")+
			     "where fcheckstate=1 and FStartDate=(select max(FStartDate)from " +
			     pub.yssGetTableName("Tb_TA_CashSettle ")+ " where fcheckstate=1 )";
    		rs = dbl.openResultSet(sql1);
    		if(rs.next())
    		{
    			holiday=rs.getString("fholidayscode");
    		}
    		tradeDate=YssFun.parseDate(this.sCommandDate);//指令日期   从前台获取
    	    this.accountDate=YssFun.formatDate(deal.getWorkDay(holiday,tradeDate, 1), "yyyy-MM-dd");//到账日期
    	    settleDate=YssFun.formatDate(deal.getWorkDay(holiday,tradeDate, 1), "yyyy-MM-dd");//结算数据日期
            //申购总金额和赎回总金额
    	    
    	    //add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
    	    if(this.portCode.indexOf(",") != -1){//若选择多个组合
    	    	String[] portCodes = this.portCode.split(",");
    	    	hmPortType = judgeSumPort(this.portCode);
				for (int i = 0; i < portCodes.length; i++) {
					//若已选的多个组合中有组合类型为汇总组合的数据，则只生成汇总组合对应的划款指令数据，
					//若已选的多个组合中没有组合类型为汇总组合的数据，则生成所有已选组合对应的划款指令数据
					if(hmPortType != null && hmPortType.get("ifHaveSumType") != null 
				       && hmPortType.get("ifHaveSumType").equals("true")){
						//若不是汇总组合，则不生成划款指令数据
						if(hmPortType.get(portCodes[i]) == null){
							continue;
						}
					}
					//add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
					sql2 = "select sum(FBuyMoney) as FBuyMoney, sum(FSellMoney) as FSellMoney  from "
							+ "(select case when FSellType = '01' then case  when FSettleMoney is null or FSettleMoney = 0 "
							+ " then FBeMarkMoney else  FSettleMoney end else 0 end as FBuyMoney, case when FSellType = '02' then "
							+ " case  when FSettleMoney is null or FSettleMoney = 0 then FBeMarkMoney else FSettleMoney end else 0 end as FSellMoney FROM "
							+ pub.yssGetTableName("Tb_TA_trade") + " a where a.FSettleDate = "
							+ dbl.sqlDate(settleDate)+ " and a.FPortCode = "+ dbl.sqlString(portCodes[i])
							+ " and a.FSellType in ('01', '02') and a.fcheckstate = 1)";
					rs = dbl.openResultSet(sql2);
					if (rs.next()) {
						tradeMoney = rs.getDouble("FSellMoney");// 赎回总金额
						tradeMoney2 = rs.getDouble("FBuyMoney");// 申购总金额
					}
					// 获取组合代码对应的组合名称
					sql = "select FPortName from " + pub.yssGetTableName("Tb_Para_Portfolio")
							+ " where FPortCode=" + dbl.sqlString(portCodes[i]);
					rs = dbl.openResultSet(sql);
					if (rs.next()) {
						this.portName = rs.getString("FPortName");
					}
					if (tradeMoney > tradeMoney2)// 赎回金额大于申购金额
					{
						this.payMoney = YssD.round(YssD.sub(tradeMoney,tradeMoney2),2);// 付款金额   bug4543 modify by zhouwei 20120524
						this.reMoney = YssD.round(YssD.sub(tradeMoney, tradeMoney2),2);// 收款金额
						this.orderType = 1;// add by nimengjig 2011.18 BUG #1105
											// 接口读取TA数据自动生成划款指令，指令类型应为
											// 在此设置指令类型为1，表示为“付款”
						getPayeerMessage(portCodes[i]);// 获取收款人和付款人关联数据
						//delete by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B 
						delDictate();//删除通过接口生成的同一日期的划款指令 by guyichuan 20110330 #756
						addDictate(portCodes[i]);// 生成划款指令
					}
					//add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
				}
			} else {
				hmPortType = judgeSumPort(this.portCode);
				//若已选的多个组合中有组合类型为汇总组合的数据，则只生成汇总组合对应的划款指令数据，
				//若已选的多个组合中没有组合类型为汇总组合的数据，则生成所有已选组合对应的划款指令数据
				if(hmPortType != null && hmPortType.get("ifHaveSumType") != null 
			       && hmPortType.get("ifHaveSumType").equals("true")){
					//若不是汇总组合，则不生成划款指令数据
					if(hmPortType.get(this.portCode) == null){
						return bookset;
					}
				}
				
				sql2 = "select sum(FBuyMoney) as FBuyMoney, sum(FSellMoney) as FSellMoney  from "
						+ "(select case when FSellType = '01' then case  when FSettleMoney is null or FSettleMoney = 0 "
						+ " then FBeMarkMoney else  FSettleMoney end else 0 end as FBuyMoney, case when FSellType = '02' then "
						+ " case  when FSettleMoney is null or FSettleMoney = 0 then FBeMarkMoney else FSettleMoney end else 0 end as FSellMoney FROM "
						+ pub.yssGetTableName("Tb_TA_trade") + " a where a.FSettleDate = "
						+ dbl.sqlDate(settleDate) + " and a.FPortCode = " + dbl.sqlString(this.portCode)
						+ " and a.FSellType in ('01', '02') and a.fcheckstate = 1)";
				rs = dbl.openResultSet(sql2);
				if (rs.next()) {
					tradeMoney = rs.getDouble("FSellMoney");// 赎回总金额
					tradeMoney2 = rs.getDouble("FBuyMoney");// 申购总金额
				}
				// 获取组合代码对应的组合名称
				sql = "select FPortName from "
						+ pub.yssGetTableName("Tb_Para_Portfolio")
						+ " where FPortCode=" + dbl.sqlString(this.portCode);
				rs = dbl.openResultSet(sql);
				if (rs.next()) {
					this.portName = rs.getString("FPortName");
				}
				if (tradeMoney > tradeMoney2)// 赎回金额大于申购金额
				{
					this.payMoney = YssD.round(YssD.sub(tradeMoney,tradeMoney2),2);// 付款金额   bug4543 modify by zhouwei 20120524
					this.reMoney = YssD.round(YssD.sub(tradeMoney,tradeMoney2),2);// 收款金额
					this.orderType = 1;// add by nimengjig 2011.18 BUG #1105
					// 接口读取TA数据自动生成划款指令，指令类型应为
					// 在此设置指令类型为1，表示为“付款”
					getPayeerMessage(this.portCode);// 获取收款人和付款人关联数据
					//modified by guyichuan 20110401 #756 添加根据划款人删除条件
					delDictate();//删除通过接口生成的同一日期的划款指令,添加根据划款人删除条件
					addDictate(this.portCode);// 生成划款指令
				}
			}
    	    //add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B
		} catch (Exception ex) {
			
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return bookset;
	}
    
    /**
     * add by songjie 2011.02.23 
     * BUG:1106 QDV4工银2011年02月15日02_B
     * 获取组合对应的组合类型
     * @param portCodes
     * @return
     * @throws YssException
     */
    private HashMap judgeSumPort(String portCodes)throws YssException{
    	HashMap hmPortType = null;
    	String str = "";
    	ResultSet rs = null;
    	try{
    		str = " select FPortcode, FPortType from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
    		      " where FPortCode in(" + operSql.sqlCodes(portCodes) + ")";
    		rs = dbl.openResultSet(str);
    		while(rs.next()){
    			if(hmPortType == null){
    				hmPortType = new HashMap();
    			}
    			if(hmPortType.get(rs.getString("FPortCode")) == null){
    				if(rs.getInt("FPortType") == 1){
    					hmPortType.put("ifHaveSumType","true");
    					hmPortType.put(rs.getString("FPortCode"), String.valueOf(rs.getInt("FPortType")));
    				}
				}
    		}
    		
    		return hmPortType;
    	}catch(Exception e){
    		throw new YssException("获取组合设置的组合类型出错！", e);
    	}
    }
    
    /***
     * add by wuweiqi 20101208 QDV4工银2010年11月1日01_A  
     * 撤销通过接口生成的同一日期的划款指令
     * @throws YssException
     */
    public void delDictate() throws YssException 
    {
    	String sqlStr="";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); //获取一个连接
    	try{
    		conn.setAutoCommit(false);
    	    bTrans = true;
    	    //modified by guyichuan 20110401   #756 增加根据划款人条件
	    	sqlStr="delete  from " 
					+pub.yssGetTableName("Tb_Cash_Command ")
					+" where  fds='1' and FcommandDate= " 
					+dbl.sqlDate(this.sCommandDate)
					+" and FPayerAccount= "
					+dbl.sqlString(this.payAccountNO);
	   
			dbl.executeSql(sqlStr);
		    conn.commit();
	        bTrans = false;
	        conn.setAutoCommit(true);
    	}
    	catch(Exception e)
    	{
    		try{
				conn.rollback();
			}catch(Exception sqe){
			throw new YssException("撤销该划款指令出错", e);}
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    	
    }
    /***
     * add by wuweiqi 20101208 QDV4工银2010年11月1日01_A  
     * 获得收款人付款人设置信息及其银行帐户关联信息
     * @throws YssException
     */
    //edit by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B 添加了portCodes作为方法参数
    public void getPayeerMessage(String portCodes) throws YssException 
    {
    	String sqlStr="";
    	String sql="";
        String payeeName="";//收款人姓名
        String payeerName="";//付款人姓名
    	ResultSet rs = null;
	    String FctlValue[] = new String[4];//轧差收款人付款人信息
        String FctlValue1="";//获取组合代码
        String FctlValue2[]=null;
        int i=0;
    	try{
    		//获取收款人付款人设置页面信息
    		sql= " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
            " where FPUBPARACODE = 'payeeSetter' and Fctlcode='portfolio'";
        	 rs = dbl.openResultSet_antReadonly(sql);  
        	 rs.last();
             FctlValue2 = new String[rs.getRow()];
             rs.beforeFirst();
             while(rs.next()){
             	FctlValue2[i] = rs.getString("FCTLVALUE");
             	i++;
             }
             dbl.closeResultSetFinal(rs);
             for(int j=0;j < i;j++)
             { 
            	 //edit by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B this.portCode 改为 传入的参数 portCodes
            	 if(FctlValue2[j].substring(0,FctlValue2[j].indexOf("|")).equals(portCodes))
            	 {
            		 FctlValue1=FctlValue2[j];
            		 break;
            	 }
             }  
             
             //----add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B----//
             if(FctlValue1.equals("")){
            	 throw new YssException("请在通用业务参数设置中设置【" + portCodes + "】组合对应的划款指令付款人收款人设置！");
             }
             //----add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B----//
             
             sql = " select FCTLVALUE from " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
             " where FPUBPARACODE = 'payeeSetter' and FPARAID=(select min(FPARAID) from "+
             pub.yssGetTableName("TB_PFOper_PUBPARA") +
             " where FPUBPARACODE ='payeeSetter' and FPARAID!=0 and FCTLVALUE=" +
             dbl.sqlString(FctlValue1) +")" ;
             rs = dbl.openResultSet(sql);   
             i = 0;
	          while(rs.next()){
	            	FctlValue[i] = rs.getString("FCTLVALUE");
	            	i++;
	            }
	          dbl.closeResultSetFinal(rs);
	          payeeName=FctlValue[1].substring(0,FctlValue[1].indexOf("|"));//获取收款人姓名
	          payeerName=FctlValue[2].substring(0,FctlValue[2].indexOf("|"));//获取付款人姓名
    		//收款人对应的银行帐户信息
    		sqlStr="select FreceiverName, FOPERBANK,FACCOUNTNUMBER,FCURYCODE from " +
    		        pub.yssGetTableName("tb_para_receiver ")+
    		        " where FRECEIVERCODE=" +
    				dbl.sqlString(payeeName);
    		rs = dbl.openResultSet(sqlStr);
    		if(rs.next())
    		{
    			this.receiverName=rs.getString("FreceiverName");//收款人姓名
				this.reOperBank=rs.getString("FOPERBANK");//银行
				this.reAccountNO=rs.getString("FACCOUNTNUMBER");//帐户
				this.reCuryCode=rs.getString("FCURYCODE");//币种
    		}
    		dbl.closeResultSetFinal(rs);//modified by yeshenghong 20120316 bug3958
    		//付款人对应的银行帐户信息
    		sqlStr="select FreceiverName, FOPERBANK,FACCOUNTNUMBER,FCURYCODE from " +
    		        pub.yssGetTableName("tb_para_receiver ")+
    		        " where FRECEIVERCODE=" +
			        dbl.sqlString(payeerName);
			rs = dbl.openResultSet(sqlStr);
			if(rs.next())
			{		
				this.payName=rs.getString("FreceiverName");//付款人姓名
    			this.payOperBank=rs.getString("FOPERBANK");//银行
    			this.payAccountNO=rs.getString("FACCOUNTNUMBER");//帐户
    			this.payCuryCode=rs.getString("FCURYCODE");//币种
			}
    	}
    	catch(Exception e)
    	{
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭 RS 
        }
    	
    }
    
 // add by 黄啟荣 2011-06-15 STORY #1097
	private String getShowTitleName(String accNum) throws YssException {
		// PrintYnShowTitle Command CtlPriYN_ShowTitle cboShowTitle
		Connection conn = null;
		ResultSet rs = null;
		String strSql = "";
		boolean bTrans = false;
		String title = "";
		try {
			strSql = "select distinct(FTitle) as FTitle from "
					+ pub.yssGetTableName("TB_Para_Receiver")
					+ " where freceivercode=(select distinct(freceivercode) from "
					+ pub.yssGetTableName("TB_Para_Receiver")
					+" where FAccountNumber="+ dbl.sqlString(accNum)+" and fcheckstate=1)";			
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				title = rs.getString("FTitle");
			}
			bTrans = false;
			conn.setAutoCommit(true);

		} catch (Exception e) {
			throw new YssException("查询划款指令抬头名称出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		return title;
	}
    

    public String getOperValue(String sType) throws YssException {
        Connection conn = null;
        String sqlStr = "";
        ResultSet rs = null;
        ResultSet rsSql = null;
        String sRes = "";
        //----------2008-4-30  单亮---------
        String strSql = "";
        //ResultSet rs = null;
        String SellNet = "";
        String[] tempAry = null;
        dMoneyTotal = 0.0;
        double dBuyMoney = 0, dSelltMoney = 0; //add by leeyu
        //--------------------------
        try {
            if (sType.equalsIgnoreCase("num")) { //提编号
                conn = dbl.loadConnection();
                sqlStr = "select max(Forder) as FOrder from " +
                    pub.yssGetTableName("tb_cash_command") +
                    //" where FCommandDate=" + dbl.sqlDate(this.sCommandDate);
                    " where FNum like '" +
                    YssFun.left(this.sCommandDate.toString(), 4) + "%'"; //这里以年为单位取值 by liyu 080530
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    if (rs.getString("FOrder") != null) {
                        sRes = rs.getInt("FOrder") + 1 + "";
                    } else {
                        sRes = "1";
                    }
                }
                rs.close();
                return sRes;
            }
            //add by lidaolong 20110409 #427 划款指令模板需更新
            else if (sType != null && sType.equalsIgnoreCase("commandModeStyle")){
            	CtlPubPara pubPara = new CtlPubPara();
                pubPara.setYssPub(pub);
                //update by guolongchao 20110929 STORY 1483 划款指令需要支持投资组合
                String modeStyle = pubPara.getCommandModeStyle(this.portCode);
                return modeStyle;
            }//end by lidaolong
            /*添加对抬头信息的处理 需求：MS00018 by leeyu 2008-11-21*/
            else if (sType != null && sType.equalsIgnoreCase("getCommandTitle")) {
                String[] arrData = null;
                String sTitle = "";
                CtlPubPara pubPara = new CtlPubPara();
                pubPara.setYssPub(pub);
                arrData = pubPara.getCommandPara().split("\t");
                if (arrData[1].equalsIgnoreCase("true")) {
                    strSql = " select distinct(FTitle) as FTitle from " + pub.yssGetTableName("Tb_Para_Receiver") +
                        " where FReceiverName =" + dbl.sqlString(this.payName); // 这里根据名字来取
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        sTitle = rs.getString("FTitle") + "";
                    }
                }
                return arrData[0] + "\t" + arrData[1] + "\t" + arrData[2] + "\t" + sTitle +"\t"+arrData[3]+ "\tnull";// edit by lidaolong  lidaolong 20110317  #746 托管行要求，当金额为0时，列表视图打印出来的指令单上金额为空不要显示“0”
            }
            /**添加获取报表抬头  需求：STORY #366   shashijie 2011.1.17
                1   获取通用参数配置
                2、	如果选是则显示格式是   "管理人名称_基金名称(组合名)_专用表"
				3、	选否，则取文本框内的内容作为抬头；
				4、	不进行任何设置本参数，则默认为“组合群_专用表”格式。
             */
            if (sType != null && sType.equalsIgnoreCase("setCommandHead")) {
            	String sTitle = "";
            	strSql = " SELECT FctlValue FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
                    " WHERE FPubParaCode = " + dbl.sqlString("setCommandHead") + "AND Fctlcode = " + dbl.sqlString("isFromat");
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                	String rctlValue = rs.getString("FctlValue") + "";
                	String value[] = rctlValue.split(",");
                    if (value[0].equals("1")) {//1表示选"是"即显示抬头为     “管理人名称_基金名称_专用表”
                    	YssLicence licence = new YssLicence();
                    	licence.setYssPub(pub);
                    	//licence.loadLicence("D:/aaa.lic");//用户正式.lic
                    	licence.loadLicence("/");
                    	/*boolean aaa = licence.isBAvailable();
                    	String av = licence.buildRowStr();*/
                    	sTitle = licence.getSClientName() + "_";//管理人名称_
                    	strSql = "SELECT FPortName FROM " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                    			" WHERE FPortCode = " + dbl.sqlString(this.portCode);
                    	ResultSet rst = dbl.openResultSet(strSql);
                    	if (rst.next()) {
                    		sTitle += rst.getString("FPortName") + "";//管理人名称_基金名称
						}
                    	dbl.closeResultSetFinal(rst);
                    	sTitle += "_专用表";
					} else {//设置否时,就以通用参数列表里的文本框内容为抬头
						strSql = " SELECT FctlValue FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") +
	                    	" WHERE FPubParaCode = " + dbl.sqlString("setCommandHead") + 
	                    	" AND Fctlcode = " + dbl.sqlString("TxtCustomizeHead");
						ResultSet rst = dbl.openResultSet(strSql);
						if (rst.next()) {
							sTitle = rst.getString("FctlValue") + "";
						}
					}
                }
                return sTitle + "\tnull";
            }
            
            //add by wuweiqi 20101208 QDV4工银2010年11月1日01_A  TA赎回数据导入成功后对划款指令的处理
            if(sType != null && sType.indexOf("isShowPayeer") != -1) {   	
            	 if(sType.indexOf("/t") != -1 &&  sType.split("/t").length >= 2){
                     if(sType.split("/t")[1].length() >= 0){
                    	 this.sCommandDate = sType.split("/t")[1];//从前台获取指令日期
                         this.portCode = sType.split("/t")[2];
                         //add by songjie 2011.02.21 BUG:1106 QDV4工银2011年02月15日02_B 添加删除方法
                         // delDictate();//删除通过接口生成的同一日期的划款指令 
                      return  getDictate();                        
                     }
                 }	
            }
            /**
             * 单亮
             * 2008-6-1
             * 获取是否产生资金调拨资金调拨
             */
            if (sType.equalsIgnoreCase("getCheckTrans")) {
                conn = dbl.loadConnection();
                sqlStr = "select FRelaNum,FNumType from " +
                    pub.yssGetTableName("Tb_Cash_Transfer") +
                    "  where FRelaNum = " + dbl.sqlString(this.sNum) +
                    " and FNumType = 'Common'";
                rs = dbl.openResultSet(sqlStr);
                while (rs.next()) {
                    sRes = "true";
                }
                if (!sRes.equalsIgnoreCase("true")) {
                    sRes = "false";
                }
                rs.close();
                return sRes;
            }
            //add by yanghaiming 20091111 MS00804  QDV4中金2009年11月10日01_B
            else if(sType != null && sType.equalsIgnoreCase("commandCashStyle")){
            	CtlPubPara ctlpubpara = new CtlPubPara();
    	        ctlpubpara.setYssPub(pub);
    	        String sStyle = ctlpubpara.getCommandCashStyle();
    	        return sStyle;
            }
            /**
             * 单亮
             * 2008-4-30
             * 计算方式：1：两种类型一块计算：按照结算日期，销售类型和组合编号，计算出总金额然后减去，不记入划款金额的费用
             * 计算方式：2：计算钆差金额：计算每种销售类型的总金额，然后计算出其钆差
             * 计算方式：3：净金额的计算：按照销售日期，销售类型和组合编号，计算出总金额然后减去所有的费用
             * 计算方式：4: 汇总模式下的轧差金额的计算
             * 计算方式：5：单类型非净金额的计算：按照销售日期，销售类型和组合编号，计算出总金额然后减去，不记入划款金额的费用
             * 计算TA划款指令的金额
             */
            else if (sType.equalsIgnoreCase("calculateTATotalMoney")) {
                ParaWithSellWay pwSellWay = new ParaWithSellWay();
                pwSellWay.setYssPub(pub);
                pwSellWay.setFCtlValue(this.getPayMoneyQuomodoValue() + "," +
                                       this.getPayMoneyQuomodoIndex());
                SellNet = (String) pwSellWay.getParaResult();
                tempAry = SellNet.split(",");
                //add by guolongchao STORY 1483 根据当前的组合portCode获取TA划款指令的数据源表
                boolean isExistTempTable=false;//此标志用来判断是否存在TA划款指令数据源表所设置的临时表
                CtlPubPara pubPara=new CtlPubPara();
                pubPara.setYssPub(pub);
                String taDataSource=pubPara.getTaTradeDataSourceTable("TaTradeDataSource",portCode);
                if(taDataSource!=null&&taDataSource.trim().length()>0)
                {
                	isExistTempTable=true;
                }
                //add by huangqirong 2013-01-30 story #3510
				boolean isHashCashCodes = false; //是否存在现金账户               
                
                if(this.cashAccountCodes != null && this.cashAccountCodes.trim().length() > 0)
                	isHashCashCodes = true;
                //---end---
                
                //add by guolongchao STORY 1483 -------end
                for (int i = 0; i < tempAry.length; i++) {
                    if (payMoneyQuomodoValue.equals("TA_HZ")) { //根据模式来判断，当前为汇总模式的处理
                        //==============//2008-12-02 MS00059 汇总模式下的轧差款计算
                        if (flag == 4) {
                            strSql =
                                "select sum(FBuyMoney) as FBuyMoney,sum(FSellMoney) as FSellMoney from( " +
                                " select case when FSellType='01' then case when FSettleMoney is null or FSettleMoney=0 then FBeMarkMoney else FSettleMoney end else 0 end as FBuyMoney,case when FSellType='02' then case when FSettleMoney is null or FSettleMoney=0 then FBeMarkMoney else FSettleMoney end else 0 end as FSellMoney " +
                                " FROM " + pub.yssGetTableName("Tb_TA_trade") +
                                " a " +
                                " where a.FSettleDate = " + dbl.sqlDate(sellData) +
                                " and a.FPortCode = " + dbl.sqlString(portCode) +
                                // " and a.FSellNetCode = " + dbl.sqlString(tempAry[i].toString()) +
                                " and a.FSellType in(" +
                                operSql.sqlCodes(sSellType) +
                                ") and a.fcheckstate = 1 " + (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : "") + ") "; //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
                            rs = dbl.openResultSet(strSql);
                            while (rs.next()) {
                                dBuyMoney = rs.getDouble("FBuyMoney");
                                dSelltMoney = rs.getDouble("FSellMoney");
                            }
                            //20110717 added by liubo.Story #1307
                            //划款指令轧差款规则增加转入和转出
                            //*******************************
                            strSql = "select sum(FBuyMoney) as FRollInMoney,sum(FSellMoney) as FRollOutMoney from( " +
	                                " select case when FSellType='04' then case when FSettleMoney is null or FSettleMoney=0 then FBeMarkMoney else FSettleMoney end else 0 end as FBuyMoney,case when FSellType='05' then case when FSettleMoney is null or FSettleMoney=0 then FBeMarkMoney else FSettleMoney end else 0 end as FSellMoney " +
	                                " FROM " + pub.yssGetTableName("Tb_TA_trade") +
	                                " a " +
	                                " where a.FSettleDate = " + dbl.sqlDate(sellData) +
	                                " and a.FPortCode = " + dbl.sqlString(portCode) +
	                                // " and a.FSellNetCode = " + dbl.sqlString(tempAry[i].toString()) +
	                                " and a.FSellType in('04','05') " +
	                                " and a.fcheckstate = 1 " + (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : "") + ")"; //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
                            rsSql = dbl.openResultSet(strSql);
                            while (rsSql.next())
                            {
                            	dBuyMoney = dBuyMoney + rsSql.getDouble("FRollInMoney");
                            	dSelltMoney = dSelltMoney + rsSql.getDouble("FRollOutMoney");
                            }
                            //****************end*********************
                            
                            if (dSelltMoney >= dBuyMoney) { //赎回金额（总的赎回金额-应付赎回费）>申购金额
                            	//edit by songjie 2011.08.16 BUG 2418 QDV4工银2011年08月11日01_B
                                dMoneyTotal = YssD.sub(dSelltMoney, dBuyMoney);
                                cashUsage = "轧差金额=" + dMoneyTotal;
                            } else if (dSelltMoney < dBuyMoney) { //申购金额>赎回金额
                            	//edit by licai 20101206 BUG #517 非中登TA，当该日为净申购时（赎回的流出金额<申购的流入金额），系统未给出提示 
//                              dMoneyTotal = 0;
                            	//edit by songjie 2011.08.16 BUG 2418 QDV4工银2011年08月11日01_B
                                dMoneyTotal = YssD.sub(dSelltMoney, dBuyMoney);
                              //edit by licai 20101206 BUG #517===========================================================end
                                cashUsage = "当日的申购金额>赎回金额 (" + dBuyMoney + ">" +
                                    dSelltMoney + ")";
                            } else if (dBuyMoney == 0) { //当天只有赎回,没有申购
                                dMoneyTotal = dSelltMoney;
                                cashUsage = "轧差金额=" + dMoneyTotal;
                            }
                            if (dBuyMoney == 0 && dSelltMoney == 0 && dbRollIn == 0 && dbRollOut == 0) {
                                dMoneyTotal = 0;
                                cashUsage = "当日无TA申购赎回数据";
                            }
                        } else if (flag == 5) { //是算汇总模式下的赎回款金额
                        	
                        	//20130424 added by liubo.Story #3869
                        	//首先确定选择的组合是否为ETF组合
                        	//判断条件为FSubAssetType字段的值为0106
                        	//==============================
                        	ResultSet rsAssetType = null;
                        	
                        	strSql = "select FSubAssetType from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                        			 " where FportCode = " + dbl.sqlString(this.portCode);
                        	
                        	rsAssetType = dbl.queryByPreparedStatement(strSql);
                        	//==============end================
                        	
                        	if (rsAssetType.next())
                        	{
                        		if (rsAssetType.getString("FSubAssetType").equalsIgnoreCase("0106"))
                        		{

                                	//20130424 deleted by liubo.Story #3869
                                	//当“划款指令”界面选择的组合为ETF组合
                        			//根据“销售日期”查找资金调拨界面的“调拨日期”，获取“调拨子类型”为04TA_IDS和04CR_2的调拨金额之和；
                        			//若该金额大于0，收款人为ETF参数设置界面的“存款账户”，付款人为ETF参数设置界面的“清算备付金账户”；
                        			//若该金额小于0，收款人为ETF参数设置界面的“清算备付金账户”，付款人为ETF参数设置界面的“存款账户”。
                                	//==============================
                        			/**add---shashijie 2013-5-8 STORY 3713 顺便修改之前的BUG把003写死的表名改成公共获取*/
                                	strSql = " select distinct Round(sum(b.FMoney),2) as FMoney, " ;
                                	strSql += getSubTransfer("04TA_IDS,04CR_2");
									/**end---shashijie 2013-5-8 STORY 3713*/
                                	rs = dbl.queryByPreparedStatement(strSql);
                                	
                                	while(rs.next())
                                	{
                                		dMoneyTotal = rs.getDouble("FMoney");
                                		
                                		dMoneyTotal = rs.getDouble("FMoney");
                                		/**Start  20130609 added by liubo.60sp1版本代码走查问题修改.*/
                                		/**将重复调用的赋值语句进行封装*/
                                		setCommAttr(rs);	
                                		/**End  20130609 added by liubo.60sp1版本代码走查问题修改.*/
                                	}
                                	
                                	dbl.closeResultSetFinal(rs);
                                	//==============end================
                        		}
	                        	else
	                        	{
		                            //BugNo:0000371 edit by jc
		                            //直接取实际结算金额，不考虑费用
		                            strSql = "select sum(a.FSettleMoney) as FTotal, sum(a.FSellAmount) as FAmount, (sum(a.FSellMoney)-sum(a.FTradeFee2)-sum(a.FTradeFee1)) as FMoney from " +
		                                pub.yssGetTableName("Tb_Ta_Trade") + " a " + //BugNo:0000389 edit by jc
		                                " where FTradeDate = " +
		                                dbl.sqlDate(this.sellData) +
		                                " and a.FPortCode = " + dbl.sqlString(portCode) +
		                                " and a.FSellType = " + sSellType +
		                                " and a.fcheckstate = 1" + (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : ""); //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
		                            rs = dbl.openResultSet(strSql);
		                            while (rs.next()) {
		                                dMoneyTotal += rs.getDouble(1);
		                                dSellMoney += rs.getDouble(3);
		                            }
		                            //rs.close();
		                        	//============end==================
	                        	}
                        	}
                        	
                        	dbl.closeResultSetFinal(rsAssetType);
                        	
                        }
                        else if (flag == 9)
                        {
                        	//20130424 added by liubo.Story #3869
                        	//首先确定选择的组合是否为ETF组合
                        	//判断条件为FSubAssetType字段的值为0106
                        	//==============================
                        	ResultSet rsAssetType = null;
                        	
                        	strSql = "select FSubAssetType from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
                        			 " where FportCode = " + dbl.sqlString(this.portCode);
                        	
                        	rsAssetType = dbl.queryByPreparedStatement(strSql);
                        	//==============end================
                        	
                        	if (rsAssetType.next())
                        	{
                        		if (rsAssetType.getString("FSubAssetType").equalsIgnoreCase("0106"))
                        		{
                        		//20130424 added by liubo.Story #3869
                        		//当“划款指令”界面选择的组合为ETF组合，
                        		//根据“销售日期”查找资金调拨界面的“调拨日期”，获取“调拨子类型”为04CB（用调拨方向流入的减去流出）和04TA_IDB（用调拨方向流入的减去流出）的调拨金额之和；
                        		//若该金额大于0，收款人为ETF参数设置界面的“存款账户”，付款人为ETF参数设置界面的“清算备付金账户”；
                        		//若该金额小于0，收款人为ETF参数设置界面的“清算备付金账户”，付款人为ETF参数设置界面的“存款账户”。
                        		//=======================================
                        			/**add---shashijie 2013-5-8 STORY 3713 顺便修改之前的BUG把003写死的表名改成公共获取*/
                                	strSql = " select distinct Round(sum(b.FMoney * b.FInOut),2) as FMoney, " ;
                                	strSql += getSubTransfer("04CB,04TA_IDB");
									/**end---shashijie 2013-5-8 STORY 3713*/
			                       	rs = dbl.queryByPreparedStatement(strSql);
			                       	
			                       	while(rs.next())
			                       	{
			                       		dMoneyTotal = rs.getDouble("FMoney");

                                		dMoneyTotal = rs.getDouble("FMoney");
                                		/**Start  20130609 added by liubo.60sp1版本代码走查问题修改.*/
                                		/**将重复调用的赋值语句进行封装*/
                                		setCommAttr(rs);	
                                		/**End  20130609 added by liubo.60sp1版本代码走查问题修改.*/
			                       	}
			                       	
			                       	dbl.closeResultSetFinal(rs);
		                        	
		//                        	return this.buildRowStr();
                        		}
                        	}
                        	
                        	dbl.closeResultSetFinal(rsAssetType);
                    		//=====================end==================
                        }
                        /**add---shashijie 2013-5-8 STORY 3713 新增"申购现金替代退补款"与"申赎现金差额"*/
                        else if (flag == 10) {//申购现金替代退补款
                        	//是否是ETF组合
							if (isETFPortCode(this.portCode)) {
								setCinnabdBean("NO","04TA_IDB");
							}
						} else if (flag == 11) {//申赎现金差额
							if (isETFPortCode(this.portCode)) {
								setCinnabdBean("FInOut","04CB");
							}
						}
						/**end---shashijie 2013-5-8 STORY 3713*/
                        dbl.closeResultSetFinal(rsSql);//add by guolongchao 20111107 BUG3074 QDV4赢时胜上海2011年11月07日01_B.xls
                        dbl.closeResultSetFinal(rs);//add by guolongchao 20111107 BUG3074 QDV4赢时胜上海2011年11月07日01_B.xls
                        return buildRowStr(); //汇总模式下取所有的只和循环一次。
                    } else { //为非汇总模式
                        if (flag == 1) {
                            strSql =
                                "select sum(ftotal + fee1 + fee2 + fee3 + fee4 + fee5 + fee6 + fee7 + fee8) as total" +
                                " from (select sum(a.FSellMoney) as FTotal," +
                                " sum(a.FSellAmount) as FAmount," +
                                " sum(case" +
                                " when b.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee1" +
                                " when b.fissettle = 1 and x.fcashind = -1 then a.FTradeFee1" +
                                " else 0" +
                                " end) as fee1," +
                                " sum(case" +
                                " when c.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee2" +
                                " when c.fissettle = 1 and x.fcashind = -1 then a.FTradeFee2" +
                                " else 0" +
                                " end) as fee2," +
                                " sum(case" +
                                " when d.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee3" +
                                " when d.fissettle = 1 and x.fcashind = -1 then a.FTradeFee3" +
                                " else 0" +
                                " end) as fee3," +
                                " sum(case" +
                                " when e.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee4" +
                                " when e.fissettle = 1 and x.fcashind = -1 then a.FTradeFee4" +
                                " else 0" +
                                " end) as fee4," +
                                " sum(case" +
                                " when f.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee5" +
                                " when f.fissettle = 1 and x.fcashind = -1 then a.FTradeFee5" +
                                " else 0" +
                                " end) as fee5," +
                                " sum(case" +
                                " when g.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee6" +
                                " when g.fissettle = 1 and x.fcashind = -1 then a.FTradeFee6" +
                                " else 0" +
                                " end) as fee6," +
                                " sum(case" +
                                " when h.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee7" +
                                " when h.fissettle = 1 and x.fcashind = -1 then a.FTradeFee7" +
                                " else 0" +
                                " end) as fee7," +
                                " sum(case" +
                                " when i.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee8" +
                                " when i.fissettle = 1 and x.fcashind = -1 then a.FTradeFee8" +
                                " else 0" +
                                " end) as fee8" +
                                " from " + pub.yssGetTableName("Tb_Ta_Trade") +
                                " a" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) b on a.ffeecode1 = b.ffeecode" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) c on a.ffeecode2 = c.ffeecode" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) d on a.ffeecode3 = d.ffeecode" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) e on a.ffeecode4 = e.ffeecode" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) f on a.ffeecode5 = f.ffeecode" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) g on a.ffeecode6 = g.ffeecode" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) h on a.ffeecode7 = h.ffeecode" +
                                " left join (select * from " +
                                pub.yssGetTableName("Tb_Para_Fee") +
                                " where fcheckstate = 1) i on a.ffeecode8 = i.ffeecode" +
                                " left join " +
                                pub.yssGetTableName("Tb_TA_SellType") +
                                " x on a.fselltype = x.fselltypecode" +
                                " where FSettleDate = " +
                                dbl.sqlDate(this.sellData) +
                                " and" +
                                " a.FPortCode = " + dbl.sqlString(portCode) +
                                " and a.FSellNetCode = " +
                                dbl.sqlString(tempAry[i].toString()) +
                                " and  a.FSellType in (" + sSellType +
                                ") and a.fcheckstate = 1" +
                                (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : "") +  //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
                                " group by a.FTradeFee1," +
                                " a.FTradeFee2," +
                                " a.FTradeFee3," +
                                " a.FTradeFee4," +
                                " a.FTradeFee5," +
                                " a.FTradeFee6," +
                                " a.FTradeFee7," +
                                " a.FTradeFee8," +
                                " b.fissettle," +
                                " c.fissettle," +
                                " d.fissettle," +
                                " e.fissettle," +
                                " f.fissettle," +
                                " g.fissettle," +
                                " h.fissettle," +
                                " i.fissettle," +
                                " x.fcashind )";

                            rs = dbl.openResultSet(strSql);
                            while (rs.next()) {
                                dMoneyTotal += rs.getDouble(1);

                            }
                        } else if (flag == 2) {
                        	//------ modify by wangzuochun 2010.04.30  MS01111    自动生成轧差款指令时，生成的应付赎回款金额和应付赎回费金额都不对    QDV4国泰2010年4月21日01_B    
                        	if (this.sellWayValue.equals("08"))
                        	{
                        		strSql  = "select sum(ftotal) as total from (select sum(" + 
                        					"case when x.fcashind = -1 then a.FsettleMoney " +
                        					     "when x.fcashind = 1 then -a.fsettlemoney " +
                        					     "else 0 end) as FTotal,x.fcashind from " + 
                        					(isExistTempTable==true?taDataSource:pub.yssGetTableName("Tb_Ta_Trade"))+//update by guolongchao STORY 1483  如果TA划款指令数据来源表设置的通用参数存在，就从设置的临时表取数，否则从TA交易数据表取数
                        				    " a left join " + pub.yssGetTableName("Tb_TA_SellType") + 
                        				  	" x on a.fselltype = x.fselltypecode " + 
                        				  	" where FSettleDate = " + dbl.sqlDate(this.sellData) +
                                            " and" +
                                            " a.FPortCode = " + dbl.sqlString(portCode) +
                                            " and a.FSellNetCode = " + dbl.sqlString(tempAry[i].toString()) +
                                            " and a.FSellType in (  " + this.sSellType +
                                            " ) and a.fcheckstate = 1 " + (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : "") +  //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
                                            " group by x.fcashind)" ;
                        		
                        		rs = dbl.openResultSet(strSql);
                                while (rs.next()) {
                                    dMoneyTotal += rs.getDouble(1);
                                }
                        		
                        	}
                        	else {

								strSql = "select sum(total) as total from ("
										+ "select -fcashind*(ftotal + fee1 + fee2 + fee3 + fee4 + fee5 + fee6 + fee7 + fee8) as total,"
										+ " FAmount"
										+ " from (select sum(a.FSellMoney) as FTotal,x.fcashind ,"
										+ " sum(a.FSellAmount) as FAmount,"
										+ " sum(case"
										+ " when b.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee1"
										+ " when b.fissettle = 1 and x.fcashind = -1 then a.FTradeFee1"
										+ " else 0"
										+ " end) as fee1,"
										+ " sum(case"
										+ " when c.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee2"
										+ " when c.fissettle = 1 and x.fcashind = -1 then a.FTradeFee2"
										+ " else 0"
										+ " end) as fee2,"
										+ " sum(case"
										+ " when d.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee3"
										+ " when d.fissettle = 1 and x.fcashind = -1 then a.FTradeFee3"
										+ " else 0"
										+ " end) as fee3,"
										+ " sum(case"
										+ " when e.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee4"
										+ " when e.fissettle = 1 and x.fcashind = -1 then a.FTradeFee4"
										+ " else 0"
										+ " end) as fee4,"
										+ " sum(case"
										+ " when f.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee5"
										+ " when f.fissettle = 1 and x.fcashind = -1 then a.FTradeFee5"
										+ " else 0"
										+ " end) as fee5,"
										+ " sum(case"
										+ " when g.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee6"
										+ " when g.fissettle = 1 and x.fcashind = -1 then a.FTradeFee6"
										+ " else 0"
										+ " end) as fee6,"
										+ " sum(case"
										+ " when h.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee7"
										+ " when h.fissettle = 1 and x.fcashind = -1 then a.FTradeFee7"
										+ " else 0"
										+ " end) as fee7,"
										+ " sum(case"
										+ " when i.fissettle = 1 and x.fcashind = 1 then -a.FTradeFee8"
										+ " when i.fissettle = 1 and x.fcashind = -1 then a.FTradeFee8"
										+ " else 0" + " end) as fee8"
										+ " from "
										+ pub.yssGetTableName("Tb_Ta_Trade")
										+ " a"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) b on a.ffeecode1 = b.ffeecode"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) c on a.ffeecode2 = c.ffeecode"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) d on a.ffeecode3 = d.ffeecode"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) e on a.ffeecode4 = e.ffeecode"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) f on a.ffeecode5 = f.ffeecode"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) g on a.ffeecode6 = g.ffeecode"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) h on a.ffeecode7 = h.ffeecode"
										+ " left join (select * from "
										+ pub.yssGetTableName("Tb_Para_Fee")
										+ " where fcheckstate = 1) i on a.ffeecode8 = i.ffeecode"
										+ " left join "
										+ pub.yssGetTableName("Tb_TA_SellType")
										+ " x on a.fselltype = x.fselltypecode"
										+ " where FSettleDate = "
										+ dbl.sqlDate(this.sellData)
										+ " and"
										+ " a.FPortCode = "
										+ dbl.sqlString(portCode)
										+ " and a.FSellNetCode = "
										+ dbl.sqlString(tempAry[i].toString())
										+ " and a.FSellType in (  "
										+ this.sSellType
										+ " ) and a.fcheckstate = 1"
										+ (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : "") //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
										+ " group by a.FTradeFee1,"
										+ " a.FTradeFee2,"
										+ " a.FTradeFee3,"
										+ " a.FTradeFee4,"
										+ " a.FTradeFee5,"
										+ " a.FTradeFee6,"
										+ " a.FTradeFee7,"
										+ " a.FTradeFee8,"
										+ " b.fissettle,"
										+ " c.fissettle,"
										+ " d.fissettle,"
										+ " e.fissettle,"
										+ " f.fissettle,"
										+ " g.fissettle,"
										+ " h.fissettle,"
										+ " i.fissettle," + " x.fcashind ))";

								rs = dbl.openResultSet(strSql);
								while (rs.next()) {
									dMoneyTotal += rs.getDouble(1);
								}
							}
                        	//--------------------MS01111---------------------//

                        } else if (flag == 3) {
                            strSql =
                                "select sum(a.FSellMoney) as FTotal" +
                                " from " + pub.yssGetTableName("Tb_Ta_Trade") +
                                " a" +
                                " where FTradeDate = " +
                                dbl.sqlDate(this.sellData) +
                                " and a.FPortCode = " + dbl.sqlString(portCode) +
                                " and a.FSellNetCode = " +
                                dbl.sqlString(tempAry[i].toString()) +
                                " and a.FSellType = " + sSellType +
                                "and a.fcheckstate = 1" + (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : "");  //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
                            rs = dbl.openResultSet(strSql);
                            while (rs.next()) {
                                dMoneyTotal += rs.getDouble(1);

                            }

                        }
                        
                        else { //是算汇总模式下的赎回款金额

                            //BugNo:0000371 edit by jc
                            //直接取实际结算金额，不考虑费用
                            strSql = "select sum(a.FSettleMoney) as FTotal, sum(a.FSellAmount) as FAmount, (sum(a.FSellMoney)-sum(a.FTradeFee2)-sum(a.FTradeFee1)) as FMoney from " +
                                pub.yssGetTableName("Tb_Ta_Trade") + " a " + //BugNo:0000389 edit by jc
                                " where FTradeDate = " +
                                dbl.sqlDate(this.sellData) +
                                " and a.FPortCode = " + dbl.sqlString(portCode) +
                                " and a.FSellNetCode = " +
                                (dbl.sqlString(tempAry[i].toString()).
                                 equalsIgnoreCase(
                                     "''") ? "a.FSellNetCode" :
                                 dbl.sqlString(tempAry[i].toString())) +
                                " and a.FSellType = " + sSellType +
                                " and a.fcheckstate = 1" + (isHashCashCodes ? " and a.FCashaccCode in (" + operSql.sqlCodes(this.cashAccountCodes) +")" : "") ;  //modify huangqirong 2013-01-30 story #3510 增加现金账户过滤
                            //----------------------jc

                            rs = dbl.openResultSet(strSql);
                            while (rs.next()) {
                                dMoneyTotal += rs.getDouble(1);
                                dSellMoney += rs.getDouble(3); //BugNo:0000389 edit by jc
                            }

                        }
                    } //end
                    dbl.closeResultSetFinal(rs);//add by guolongchao 20111107 BUG3074 QDV4赢时胜上海2011年11月07日01_B.xls
                }
//            }// add by leeyu 这里要加上右括号2008-12-02
                return this.buildRowStr();
            }
            //add by yanghaiming 20100621 MS01302 QDV4长盛2010年06月02日01_A 是否启用新划款指令格式
            else if (sType.equalsIgnoreCase("newstyle")){
            	CtlPubPara ctlPubPara = new CtlPubPara();
            	ctlPubPara.setYssPub(pub);
    	        String sStyle = ctlPubPara.getNewCommandCashStyle();
    	        return sStyle;
            }
            //add by fangjiang 2010.11.05 BUG #302
            else if (sType.equalsIgnoreCase("isShowCashType")){
    	        boolean flag = isShowCashType();
    	        if (flag == true) {
    	        	return "1";
    	        } else {
    	        	return "0";
    	        }
            }
            //------------------
            /**shashijie 2011.03.14 TASK #3131::跨入新的一年时，每个组合的指令序号都要重新从1开始*/
            else if(sType.equalsIgnoreCase("getOrder")){
            	sRes = getOrderValue();
                return sRes;
            }
            /***----------end--------*/
         // add by 黄啟荣 2011-06-15 STORY #1097
			else if (sType.equalsIgnoreCase("getPaytitle")) {
				if (this.payAccountNO != null)
					sRes = getShowTitleName(this.payAccountNO);				
				return sRes;
			}
			else if (sType.equalsIgnoreCase("getRetitle")) {
				if (this.reAccountNO != null)
					sRes = getShowTitleName(this.reAccountNO);				
				return sRes;
			}
			// --end---    
            //by zhouwei 20111129 story 1842
			else if (sType.equalsIgnoreCase("batchAudit")) {
				checkSetting();
				return sRes;
			}
			else if (sType.equalsIgnoreCase("batchUnAudit")) {
				checkSetting();
				return sRes;
			}
			else if (sType.equalsIgnoreCase("deltype")) {
				delSetting();
				return sRes;
			}
            //----end----
            //story 1645 by zhouwei 20111130 QDII工银2011年9月13日10_A 根据通用参数决定是否显示外汇交收
			else if (sType.equalsIgnoreCase("isShowFEType")) {
				sRes=getFEStateByPFOper();// 0 不显示 ；1显示
				return sRes;
			}
            //获取报表模板
			else if (sType.equalsIgnoreCase("getReportModule")) {
				sRes=getCommandModuleRela();// 0 不显示 ；1显示
				return sRes;
			}
            //获取组合信息
			else if (sType.equalsIgnoreCase("getPortRelaInfo")) {
				sRes=getPortRelaInfo();// 0 不显示 ；1显示
				return sRes;
			}
            //add by huangqirong 2012-04-13 story #2326 处理GCS状态
			else if(sType.equalsIgnoreCase("batchgcsstate")){
				if( this.sNum.length() > 0 ){
						conn = dbl.loadConnection();
			            conn.setAutoCommit(false);			            
						String sql ="update "+pub.yssGetTableName("Tb_Cash_Command")+
									" set FGCSState=( case  when FGCSState ='110' then  '111'  else '110'end)" +
									" where FNum in ("+operSql.sqlCodes(this.sNum)+")";
						
						dbl.executeSql(sql);
				        conn.commit();						
			            conn.setAutoCommit(true);
					
				}
				return "";
			}
            //---end---
            //----end----
            return "";
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs); //关闭 RS 李钰添加 2008-11-21
            dbl.closeResultSetFinal(rsSql);//20110721 added by liubo.Story #1307.关闭rsSql 
            dbl.endTransFinal(conn, false);
        }
    }
    

    /**
     * 20130609 added by liubo.Story #3869
     * 代码走查会议问题：需求3869中，此方法中的代码有重复调用的情况
     * 在这个方法中进行封装，方便重复调用
     */
    private void setCommAttr(ResultSet rs) throws YssException
    {
    	try
    	{
	    	if (dMoneyTotal >= 0)
	   		{
	   			this.payName = (rs.getString("FClearAcc") == null ? "" : rs.getString("FClearAcc"));
	   			this.payOperBank = (rs.getString("FClearBank") == null ? "" : rs.getString("FClearBank"));
	   			this.payAccountNO = (rs.getString("FClearAccNum") == null ? "" : rs.getString("FClearAccNum"));
	   			this.payCuryCode = (rs.getString("FClearCury") == null ? "" : rs.getString("FClearCury"));
	   			this.receiverName = (rs.getString("FDepAcc") == null ? "" : rs.getString("FDepAcc"));
	   			this.reOperBank = (rs.getString("FDepBank") == null ? "" : rs.getString("FDepBank"));
	   			this.reAccountNO = (rs.getString("FDepAccNum") == null ? "" : rs.getString("FDepAccNum"));
	   			this.reCuryCode = (rs.getString("FDepCury") == null ? "" : rs.getString("FDepCury"));
	   		}
	   		else
	   		{
	   			this.payName = (rs.getString("FDepAcc") == null ? "" : rs.getString("FDepAcc"));
	   			this.payOperBank = (rs.getString("FDepBank") == null ? "" : rs.getString("FDepBank"));
	   			this.payAccountNO = (rs.getString("FDepAccNum") == null ? "" : rs.getString("FDepAccNum"));
	   			this.payCuryCode = (rs.getString("FDepCury") == null ? "" : rs.getString("FDepCury"));
	   			this.receiverName = (rs.getString("FClearAcc") == null ? "" : rs.getString("FClearAcc"));
	   			this.reOperBank = (rs.getString("FClearBank") == null ? "" : rs.getString("FClearBank"));
	   			this.reAccountNO = (rs.getString("FClearAccNum") == null ? "" : rs.getString("FClearAccNum"));
	   			this.reCuryCode = (rs.getString("FClearCury") == null ? "" : rs.getString("FClearCury"));
	   		}
    	}
    	catch(Exception ye)
    	{
    		throw new YssException(ye);
    	}
    }

    
   /**shashijie 2013-5-8 STORY 3713 设置划款指令*/
	private void setCinnabdBean(String sign, String FSubTsfTypeCode) throws YssException {
		String strSql = "";
		//判断是否需要乘以方向
		if (sign.equals("NO")) {
			strSql = " select distinct Round(sum(b.FMoney),2) as FMoney, " ;
		}else if (sign.equals("FInOut")) {
			strSql = " select distinct Round(sum(b.FMoney * b.FInOut),2) as FMoney, " ;
		}
    	strSql += getSubTransfer(FSubTsfTypeCode);
    	
    	ResultSet rs = null;//定义游标
    	try {
			rs = dbl.openResultSet(strSql);
			while (rs.next()) {
				//设置对象
				setThisCommandBean(rs);
			}
		} catch (Exception e) {
			throw new YssException("获取资金调拨出错!", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}

	/**shashijie 2013-5-8 STORY 3713 设置自身对象*/
	private void setThisCommandBean(ResultSet rs) throws Exception {
		//调拨金额
		dMoneyTotal = rs.getDouble("FMoney");
   		if (dMoneyTotal >= 0)
   		{
   			this.payName = (rs.getString("FClearAcc") == null ? "" : rs.getString("FClearAcc"));
   			this.payOperBank = (rs.getString("FClearBank") == null ? "" : rs.getString("FClearBank"));
   			this.payAccountNO = (rs.getString("FClearAccNum") == null ? "" : rs.getString("FClearAccNum"));
   			this.payCuryCode = (rs.getString("FClearCury") == null ? "" : rs.getString("FClearCury"));
   			this.receiverName = (rs.getString("FDepAcc") == null ? "" : rs.getString("FDepAcc"));
   			this.reOperBank = (rs.getString("FDepBank") == null ? "" : rs.getString("FDepBank"));
   			this.reAccountNO = (rs.getString("FDepAccNum") == null ? "" : rs.getString("FDepAccNum"));
   			this.reCuryCode = (rs.getString("FDepCury") == null ? "" : rs.getString("FDepCury"));
   		}
   		else
   		{
   			this.payName = (rs.getString("FDepAcc") == null ? "" : rs.getString("FDepAcc"));
   			this.payOperBank = (rs.getString("FDepBank") == null ? "" : rs.getString("FDepBank"));
   			this.payAccountNO = (rs.getString("FDepAccNum") == null ? "" : rs.getString("FDepAccNum"));
   			this.payCuryCode = (rs.getString("FDepCury") == null ? "" : rs.getString("FDepCury"));
   			this.receiverName = (rs.getString("FClearAcc") == null ? "" : rs.getString("FClearAcc"));
   			this.reOperBank = (rs.getString("FClearBank") == null ? "" : rs.getString("FClearBank"));
   			this.reAccountNO = (rs.getString("FClearAccNum") == null ? "" : rs.getString("FClearAccNum"));
   			this.reCuryCode = (rs.getString("FClearCury") == null ? "" : rs.getString("FClearCury"));
   		}
	}

	/**shashijie 2013-5-8 STORY 3713 是否是ETF组合*/
	private boolean isETFPortCode(String portCode) {
		boolean flag = false;
		ResultSet rs = null;//定义游标
		try {
			String strSql = "select FSubAssetType from " + pub.yssGetTableName("Tb_Para_Portfolio") + 
			 	" where FportCode = " + dbl.sqlString(this.portCode);
			rs = dbl.queryByPreparedStatement(strSql);
			
			if (rs.next())
			{
				//ETF组合
				if (rs.getString("FSubAssetType").equalsIgnoreCase("0106"))
				{
					flag = true;
				}
			}
		} catch (Exception e) {
			flag = false;
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flag;
	}

	/**shashijie 2013-5-8 STORY 3713 获取资金调拨SQL*/
	private Object getSubTransfer(String Fsubtsftypecode) {
		String sql = " d.FRECEIVERNAME as FDepAcc,d.FOperBank as FDepBank,d.FAccountNumber as FDepAccNum," +
			" d.fcurycode as FDepCury," +
			" e.FRECEIVERNAME as FClearAcc,e.FOperBank as FClearBank,e.FAccountNumber as FClearAccNum," +
			" e.fcurycode as FClearCury " +
			" from " + pub.yssGetTableName("tb_cash_transfer") + " a " +
			" left join " + pub.yssGetTableName("tb_cash_subtransfer") + " b on a.Fnum = b.Fnum " +
			" left join " + pub.yssGetTableName("tb_etf_param") + " c on c.Fportcode = " + dbl.sqlString(this.portCode) +
			" left join (select distinct a.FCashAccCode,b.FRECEIVERNAME,b.FOperBank,b.FAccountNumber,b.FCuryCode from " +
			pub.yssGetTableName("tb_para_cashaccount")+" a " +
			" left join " +
			pub.yssGetTableName("tb_para_receiver")+
			" b on a.FRECPAYCODE = b.FRECEIVERCODE Where a.FCheckState = 1) d on c.FCASHACCCODE = d.fcashacccode " +
			" left join (select distinct a.FCashAccCode,b.FRECEIVERNAME,b.FOperBank,b.FAccountNumber,b.FCuryCode from " +
			pub.yssGetTableName("tb_para_cashaccount")+" a " +
			" left join " +
			pub.yssGetTableName("tb_para_receiver")+
			" b on a.FRECPAYCODE = b.FRECEIVERCODE Where a.FCheckState = 1) e on c.FClearAccCode = e.fcashacccode" +
			" where a.Ftransferdate = " + dbl.sqlDate(this.sellData) +
			" and a.Fsubtsftypecode in ("+operSql.sqlCodes(Fsubtsftypecode)+") " +
			" And a.FCheckState = 1 "+
			" group by d.FRECEIVERNAME,d.FOperBank,d.FAccountNumber,d.fcurycode, e.FRECEIVERNAME," +
			" e.FOperBank,e.FAccountNumber,e.fcurycode";
		return sql;
	}

	// story 1645 by zhouwei 20111130 QDII工银2011年9月13日10_A 根据通用参数决定是否显示外汇交收
	private String getFEStateByPFOper() throws YssException {

		String value = "0";
		String sql = "";
		ResultSet rs = null;
		try {
			sql = " select fctlvalue from "
					+ pub.yssGetTableName("Tb_Pfoper_Pubpara")
					+ " where fpubparacode = 'ForeignExchangeSet' and fparaid <> 0 and FCtlGrpCode = 'ForeignExchangeSet'";
			rs = dbl.openResultSet(sql);
			if (rs.next()) {
				String flag = rs.getString("fctlvalue").split(",")[0];
				if (flag.equals("0")) {
					value = "1";// 显示
				}
			}

		} catch (Exception ex) {
			throw new YssException("获取外汇交收通用参数设置出错" + ex.getMessage(), ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return value;

	}

	// story 1645 by zhouwei 20111215 QDII工银2011年9月13日10_A
	// 根据划款指令链接设置去查找符合条件的模板信息
	private String getCommandModuleRela() throws YssException {

		String reStr = "";
		String sql = "";
		ResultSet rs = null;
		try {
			sql = " select a.*,b.FRECEIVERNAME as payername,c.FRECEIVERNAME as receivername from "
					+ pub.yssGetTableName("TB_CASH_MODULE_RELA")
					+ " a"
					+ " inner join (select * from "
					+ pub.yssGetTableName("TB_data_commondModel")
					+ " where fcheckstate=1) d on a.FREPFORMATCODE=d.FCommmondCODE" // modify
																					// by
																					// zhangjun
																					// 2012-05-22
																					// BUG#4451
					+ " left join (select * from "
					+ pub.yssGetTableName("TB_PARA_RECEIVER")
					+ " where fcheckstate=1) b on a.FPAYERCODE=b.FRECEIVERCODE"
					+ " left join (select * from "
					+ pub.yssGetTableName("TB_PARA_RECEIVER")
					+ " where fcheckstate=1) c on a.FRECEIVERCODE=c.FRECEIVERCODE"
					+ " where a.fcheckstate = 1 "
					+ " and  (a.FFOREXCHANGESTATE="
					+ dbl.sqlString(this.feState)
					+ " or a.FFOREXCHANGESTATE='-1' )"
					+ " and ( a.FPORTCODE="
					+ dbl.sqlString(this.portCode)
					+ " or a.FPORTCODE is null or a.FPORTCODE=' ')"
					+ " and (b.FRECEIVERNAME="
					+ dbl.sqlString(this.payName)
					+ " or b.FRECEIVERNAME is null or b.FRECEIVERNAME=' ')"
					+ " and (c.FRECEIVERNAME="
					+ dbl.sqlString(this.receiverName)
					+ " or c.FRECEIVERNAME is null  or c.FRECEIVERNAME=' ')"
					+ " and (a.FTRANSFERTYPECODE="
					+ dbl.sqlString(this.transferType)
					+ " or a.FTRANSFERTYPECODE is null or  a.FTRANSFERTYPECODE=' ')"
					+ "  order by a.FMODULERELACODE desc";
			// +"  order by a.ftransfertypecode desc,a.fpayercode desc,a.freceivercode desc";
			rs = dbl.openResultSet(sql);
			if (rs.next()) {
				reStr = rs.getString("FREPFORMATCODE");
			}
			return reStr;
		} catch (Exception ex) {
			throw new YssException("获取划款指令模板出错" + ex.getMessage(), ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

	}

	// story 1645 by zhouwei 20111215 QDII工银2011年9月13日10_A 获取组合关联信息(主托管人等)
	private String getPortRelaInfo() throws YssException {
		String sql = "";
		ResultSet rs = null;
		try {
			String fmanagername = "";
			String ftrusteename = "";
			String fassetcode = "";
			String ftrusteecode = "";
			String fmanagercode = "";
			sql = "select b.fportcode,b.fportname,b.fassetcode,c.FTrusteeName,d.fmanagername,c.ftrusteecode,d.fmanagercode from "
					+ pub.yssGetTableName("Tb_Para_Portfolio_RelaShip")
					+ " a"
					+ " left join (select * from "
					+ pub.yssGetTableName("Tb_Para_Portfolio")
					+ " where fcheckstate=1) b on a.fportcode=b.fportcode"
					+ " left join (select * from "
					+ pub.yssGetTableName("Tb_Para_Trustee")
					+ " where fcheckstate=1) c on a.fsubcode=c.ftrusteecode "
					+ " and a.frelatype='Trustee' and a.frelagrade='primary'"
					+ " left join (select * from "
					+ pub.yssGetTableName("Tb_Para_Manager")
					+ " where fcheckstate=1) d on a.fsubcode=d.fmanagercode and a.frelatype='Manager'"
					+ " where a.fcheckstate=1 and a.fportcode="
					+ dbl.sqlString(this.portCode)
					+ " and (c.ftrusteecode is not null or d.fmanagercode is not null )";
			rs = dbl.openResultSet(sql);
			while (rs.next()) {
				if (rs.getString("fmanagercode") != null
						&& !rs.getString("fmanagercode").equals("")
						&& fmanagercode.equals("")) {
					fmanagercode = rs.getString("fmanagercode");
					fmanagername = rs.getString("fmanagername");
				}
				if (rs.getString("ftrusteecode") != null
						&& !rs.getString("ftrusteecode").equals("")
						&& ftrusteecode.equals("")) {
					ftrusteecode = rs.getString("ftrusteecode");
					ftrusteename = rs.getString("ftrusteename");
				}
				fassetcode = rs.getString("fassetcode");
			}
			return fassetcode + "\t" + ftrusteename + "\t" + fmanagername
					+ "\t" + ftrusteecode + "\t" + fmanagercode + "\tnull";
		} catch (Exception ex) {
			throw new YssException("获取组合关联信息出错" + ex.getMessage(), ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	public String getSNum() {
        return sNum;
    }

    public String getSCommandTime() {
        return sCommandTime;
    }

    public String getSCommandDate() {
        return sCommandDate;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getOrder() {
        return order;
    }

    public String getOldNum() {
        return oldNum;
    }

    public double getReMoney() {
        return reMoney;
    }

    public CommandTypeBean getFilterType() {
        return filterType;
    }

    public String getDesc() {
        return desc;
    }

    //---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
    public int getOrderType(){
    	return orderType;
    }
    //---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
    
    public String getCashUsage() {
        return cashUsage;
    }

    public String getAccountTime() {
        return accountTime;
    }

    public void setAccountDate(String accountDate) {
        this.accountDate = accountDate;
    }

    public void setSNum(String sNum) {
        this.sNum = sNum;
    }

    public void setSCommandTime(String sCommandTime) {
        this.sCommandTime = sCommandTime;
    }

    public void setSCommandDate(String sCommandDate) {
        this.sCommandDate = sCommandDate;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setOldNum(String oldNum) {
        this.oldNum = oldNum;
    }

    public void setReMoney(double money) {
        this.reMoney = money;
    }

    public void setFilterType(CommandTypeBean filterType) {
        this.filterType = filterType;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    //---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
    public void setOrderType(int orderType){
    	this.orderType = orderType;
    }
    //---add by songjie 2011.05.13 需求 759 QDV4工银2011年3月7日05_A---//
    
    public void setCashUsage(String cashUsage) {
        this.cashUsage = cashUsage;
    }

    public void setAccountTime(String accountTime) {
        this.accountTime = accountTime;
    }

    public void setReOperBank(String operBank) {
        this.reOperBank = operBank;
    }

    public void setReAccountNO(String accountNO) {
        this.reAccountNO = accountNO;
    }

    public void setBShow(String bShow) {
        this.bShow = bShow;
    }

    public void setOldOrder(String oldOrder) {
        this.oldOrder = oldOrder;
    }

    public void setPayCuryCode(String payCuryCode) {
        this.payCuryCode = payCuryCode;
    }

    public void setPayCuryName(String payCuryName) {
        this.payCuryName = payCuryName;
    }

    public void setPayOperBank(String payOperBank) {
        this.payOperBank = payOperBank;
    }

    public void setPayMoney(double payMoney) {
        this.payMoney = payMoney;
    }

    public void setPayAccountNO(String payAccountNO) {
        this.payAccountNO = payAccountNO;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public void setReCuryName(String reCuryName) {
        this.reCuryName = reCuryName;
    }

    public void setReCuryCode(String reCuryCode) {
        this.reCuryCode = reCuryCode;
    }

    public void setDRate(double dRate) {
        this.dRate = dRate;
    }

    public void setPayChinese(String payChinese) {
        this.payChinese = payChinese;
    }

    public void setReChinese(String reChinese) {
        this.reChinese = reChinese;
    }

    public void setBTransfer(boolean bTrans) {
        this.bTransfer = bTrans;
    }

    public void setSRelaType(String sRelaType) {
        this.sRelaType = sRelaType;
    }

    public void setSRelaNum(String sRelaNum) {
        this.sRelaNum = sRelaNum;
    }

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    //public void setContraSideCode(String contraSideCode) {
    //  this.payCode = contraSideCode;
    //}

    public void setContraSideName(String contraSideName) {
        this.payName = contraSideName;
    }

    public String getAccountDate() {
        return accountDate;
    }

    public String getReOperBank() {
        return reOperBank;
    }

    public String getReAccountNO() {
        return reAccountNO;
    }

    public String getBShow() {
        return bShow;
    }

    public String getOldOrder() {
        return oldOrder;
    }

    public String getPayCuryCode() {
        return payCuryCode;
    }

    public String getPayCuryName() {
        return payCuryName;
    }

    public String getPayOperBank() {
        return payOperBank;
    }

    public double getPayMoney() {
        return payMoney;
    }

    public String getPayAccountNO() {
        return payAccountNO;
    }

    public String getPayName() {
        return payName;
    }

    public String getReCuryName() {
        return reCuryName;
    }

    public String getReCuryCode() {
        return reCuryCode;
    }

    public double getDRate() {
        return dRate;
    }

    public String getPayChinese() {
        return payChinese;
    }

    public String getReChinese() {
        return reChinese;
    }

    public boolean isBTransfer() {
        return bTransfer;
    }

    public String getSRelaType() {
        return sRelaType;
    }

    public String getSRelaNum() {
        return sRelaNum;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getPortName() {
        return portName;
    }

    // public String getContraSideCode() {
    // return payCode;
    // }

    public String getContraSideName() {
        return payName;
    }

    public void setSellData(String sellData) {
        this.sellData = sellData;
    }

    //-----------------------2008-4-30  单亮----------------

    public void setSellWayValue(String sellWayValue) {
        this.sellWayValue = sellWayValue;
    }

    public void setSellWayIndex(String SellWayIndex) {
        this.sellWayIndex = SellWayIndex;
    }

    public void setPayMoneyQuomodoIndex(String payMoneyQuomodoIndex) {
        this.payMoneyQuomodoIndex = payMoneyQuomodoIndex;
    }

    public void setPayMoneyQuomodoValue(String payMoneyQuomodoValue) {
        this.payMoneyQuomodoValue = payMoneyQuomodoValue;
    }

    public void setSSellType(String sSellType) {
        this.sSellType = sSellType;
    }

    public void setDSellMoney(double DSellMoney) {
        this.DSellMoney = DSellMoney;
    }

    public String getSellData() {
        return sellData;
    }

    public String getSellWayValue() {
        return sellWayValue;
    }

    public String getSellWayIndex() {
        return sellWayIndex;
    }

    public String getPayMoneyQuomodoIndex() {
        return payMoneyQuomodoIndex;
    }

    public String getPayMoneyQuomodoValue() {
        return payMoneyQuomodoValue;
    }

    public String getSSellType() {
        return sSellType;
    }

    public double getDSellMoney() {
        return DSellMoney;
    }

    //--------------------------------------------
    // add by wuweiqi 
    public String getfDS() {
		return fDS;
	}

	public void setfDS(String fDS) {
		this.fDS = fDS;
	}

    /***
     * 新增产生资金调拨方法
     * modify by wangzuochun 2010.11.17  BUG #446 新增划款指令，提示信息不友好，且新增失败时，不产生资金调拨和不保存划款指令。
     */
    private void addTransfer(String sNum) throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090421 --
        EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
        rateOper.setYssPub(pub);
        //-----------------------------------------------------------

        try {
            TransferBean tran = new TransferBean();
            TransferSetBean transfersetIn = new TransferSetBean();
            TransferSetBean transfersetOut = new TransferSetBean();
            // TransferSetBean transfersetInFee = new TransferSetBean();
            // TransferSetBean transfersetOutFee = new TransferSetBean();
            ArrayList tranSetList = new ArrayList();
            CashTransAdmin tranAdmin = new CashTransAdmin();
            sqlStr = "select FNum from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FNumType='Common' and FRelaNum=" + dbl.sqlString(sNum);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                sqlStr = " delete from " +
                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum =" + dbl.sqlString(rs.getString("FNum"));
                dbl.executeSql(sqlStr);
            }
            
        	//add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
        	dbl.closeResultSetFinal(rs);
            
            sqlStr = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FNumType='Common' and FRelaNum=" + dbl.sqlString(sNum);
            dbl.executeSql(sqlStr);

            sqlStr = "select a.*,i.fanalysiscode1 as FIAnalysisCode1,i.fanalysiscode2 as FIAnalysisCode2,i.fanalysiscode3 as FIAnalysisCode3," +
                " i.Fportcode as FIPortCode,i.Fcashacccode as FICashAccCode ," +
                " o.fanalysiscode1 as FOAnalysisCode1,o.fanalysiscode2 as FOAnalysisCode2,o.fanalysiscode3 as FOAnalysisCode3," +
                " o.Fportcode as FOPortCode,o.Fcashacccode as FOCashAccCode " +
                " from " + pub.yssGetTableName("Tb_Cash_Command") + " a " +
                "  join (select distinct(FReceiverName),FCashAccCode,FPortCode,FCuryCode,FoperBank,FAccountNumber,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Para_Receiver") + " where FPortCode<>' ' and FCashAccCode <>' ' and FCuryCode <>' ' and FCheckState=1) o on a.FPayerName = o.freceiverName and a.FPayCury=o.FcuryCode and a.FPayerBank =o.FOperBank and a.FPayerAccount=o.FAccountNumber " +
                "  join (select distinct(FReceiverName),FCashAccCode,FPortCode,FCuryCode,FoperBank,FAccountNumber,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
                pub.yssGetTableName("Tb_Para_Receiver") + " where FPortCode<>' ' and FCashAccCode <>' ' and FCuryCode <>' ' and FCheckState=1) i on a.frecername = i.freceiverName and a.FRecCury=i.FcuryCode and a.FRecerBank =i.FOperBank and a.FRecerAccount=i.FAccountNumber " +
                " where  a.FNum=" + dbl.sqlString(sNum); //增加条件,防止取出多条不相关的数据 ,因名称相同,所以这里需多加判断 by liyu 080530
            rs = dbl.openResultSet_antReadonly(sqlStr);
            int iRow = 0;
            while (rs.next()) {
                iRow = rs.getRow();
            }
            rs.beforeFirst();
            if (iRow == 0) {
                throw new SQLException("生成资金调拨不成功,请检查收款人设置中的关联资金调拨项数据是否完整!");
            } while (rs.next()) {

                PortfolioBean bPort = new PortfolioBean();
                bPort.setYssPub(pub);
                bPort.setPortCode(rs.getString("FIPortCode"));
                bPort.getSetting();
                PortfolioBean sPort = new PortfolioBean();
                sPort.setYssPub(pub);
                sPort.setPortCode(rs.getString("FOPortCode"));
                sPort.getSetting();
                tranAdmin.setYssPub(pub);
                tran.setYssPub(pub);

                tran.setDtTransDate(rs.getDate("FCommandDate")); //存入时间
                tran.setDtTransferDate(rs.getDate("FAccountDate"));
                tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
                tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_FACT);
                tran.setStrTransferTime("00:00:00");
//            tran.setDataSource(0);
                tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
                //tran.setRateTradeNum(sRateTradeNum);
                tran.setFRelaNum(sNum);
                tran.setFNumType("Common");
                tran.checkStateId = 0;
                tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                    "yyyyMMdd HH:mm:ss");
//            tran.setDataSource(0); //自动计算标志
                tran.setDataSource(1); //这里应为自动标记 by leeyu BUG:MS00020 2008-11-24
                //资金流入帐户
                transfersetIn.setDMoney(rs.getDouble("FRecMoney"));
                transfersetIn.setSPortCode(rs.getString("FIPortCode"));
                transfersetIn.setSAnalysisCode1(rs.getString("FIAnalysisCode1"));
                transfersetIn.setSAnalysisCode2(rs.getString("FIAnalysisCode2"));

                transfersetIn.setDBaseRate(this.getSettingOper().getCuryRate(rs.
                    getDate("FCommandDate"), rs.getString("FRecCury"),
                    rs.getString("FIPortCode"),
                    YssOperCons.YSS_RATE_BASE));
                //transfersetIn.setDPortRate(YssD.div(rs.getDouble("FRefRate"), 1, 12));
//            transfersetIn.setDPortRate(this.getSettingOper().getCuryRate(rs.
//                  getDate("FCommandDate"), bPort.getCurrencyCode(),
//                  rs.getString("FIPortCode"),
//                  YssOperCons.YSS_RATE_PORT));
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(rs.
                                          getDate("FCommandDate"), rs.getString("FRecCury"), rs.getString("FIPortCode"));
                transfersetIn.setDPortRate(rateOper.getDPortRate());
                //-----------------------------------------------------------------------------------

                transfersetIn.setSCashAccCode(rs.getString("FICashAccCode")); //改为流入帐户
                transfersetIn.setIInOut(1);
                transfersetIn.checkStateId = 0;

                //资金流出帐户
                transfersetOut.setDMoney(rs.getDouble("FPayMoney"));
                transfersetOut.setSPortCode(rs.getString("FOPortCode"));
                transfersetOut.setSAnalysisCode1(rs.getString("FOAnalysisCode1"));
                transfersetOut.setSAnalysisCode2(rs.getString("FOAnalysisCode2"));

                //transfersetOut.setDBaseRate(YssD.div(this.baseMoney, this.sMoney));
                //transfersetOut.setDPortRate(YssD.div(this.baseMoney, this.portMoney));

                //流出汇率按照当日汇率

                //transfersetOut.setDPortRate(YssD.div(rs.getDouble("FRefRate"), 1, 12));
                //transfersetOut.setDBaseRate(1);
//            transfersetOut.setDPortRate(this.getSettingOper().getCuryRate(rs.
//                  getDate("FCommandDate"), sPort.getCurrencyCode(),
//                  rs.getString("FOPortCode"),
//                  YssOperCons.YSS_RATE_PORT));
                //---- MS00011 QDV4赢时胜（上海）2008年11月13日01_A  20090415 --------------------------
                rateOper.getInnerPortRate(rs.
                                          getDate("FCommandDate"), rs.getString("FPayCury"), rs.getString("FOPortCode"));
                transfersetOut.setDPortRate(rateOper.getDPortRate());
                //-----------------------------------------------------------------------------------
                transfersetOut.setDBaseRate(this.getSettingOper().getCuryRate(rs.
                    getDate("FCommandDate"), rs.getString("FPayCury"),
                    rs.getString("FOPortCode"),
                    YssOperCons.YSS_RATE_BASE));
                transfersetOut.setSCashAccCode(rs.getString("FOCashAccCode"));
                transfersetOut.setIInOut( -1);
                transfersetOut.checkStateId = 0;

                tranSetList.add(transfersetOut);
                tranSetList.add(transfersetIn);
                tranAdmin.addList(tran, tranSetList);
                tranAdmin.insert(sNum, true);

            }
        } catch (SQLException ex) {
            throw new YssException(ex.getMessage());
        } catch (Exception e) {
            throw new YssException("生成资金调拨出错", e);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
        finally{
        	dbl.closeResultSetFinal(rs);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
    }

    /**
     * deleteRecycleData
     * 功能：从数据库彻底删除数据
     * @throws YssException
     * 时间：2008年6月5号
     * 修改人：蒋春
     */
    public void deleteRecycleData() throws YssException {
        String strSql = "";
        String[] arrData = null;
        ResultSet rs = null;
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
                        pub.yssGetTableName("Tb_Cash_Command") +
                        " where FNum = " +
                        dbl.sqlString(this.sNum);

                    //执行sql语句
                    dbl.executeSql(strSql);

                    //删除资金调拨
                    strSql = "select FNum from " +
                        pub.yssGetTableName("Tb_Cash_Transfer") +
                        " where FNumType='Common' and FRelaNum=" +
                        dbl.sqlString(this.sNum);
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        strSql = " delete from " +
                            pub.yssGetTableName("Tb_Cash_SubTransfer") +
                            " where FNum =" + dbl.sqlString(rs.getString("FNum"));
                        dbl.executeSql(strSql);
                    }
                    
                    //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                    dbl.closeResultSetFinal(rs);
                    
                    strSql = " delete from " +
                        pub.yssGetTableName("Tb_Cash_Transfer") +
                        " where FNumType='Common' and FRelaNum=" +
                        dbl.sqlString(this.sNum);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            //sRecycled如果sRecycled为空，而sNum不为空，则按照sNum来执行sql语句
            else {
                if (this.sNum != "" && this.sNum != null) {
                    strSql = "delete from " +
                        pub.yssGetTableName("Tb_Cash_Command") +
                        " where FNum = " +
                        dbl.sqlString(this.sNum);

                    //执行sql语句
                    dbl.executeSql(strSql);

                    //删除资金调拨
                    strSql = "select FNum from " +
                        pub.yssGetTableName("Tb_Cash_Transfer") +
                        " where FNumType='Common' and FRelaNum=" +
                        dbl.sqlString(this.sNum);
                    rs = dbl.openResultSet(strSql);
                    while (rs.next()) {
                        strSql = " delete from " +
                            pub.yssGetTableName("Tb_Cash_SubTransfer") +
                            " where FNum =" + dbl.sqlString(rs.getString("FNum"));
                        dbl.executeSql(strSql);
                    }
                    
                    //add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B
                    dbl.closeResultSetFinal(rs);
                    
                    strSql = " delete from " +
                        pub.yssGetTableName("Tb_Cash_Transfer") +
                        " where FNumType='Common' and FRelaNum=" +
                        dbl.sqlString(this.sNum);

                    //执行sql语句
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //删除关联的资金调拨 by leeyu 080811
    private void deleCashTransfer(String sNum) throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            sqlStr = "delete from " + pub.yssGetTableName("tb_cash_subtransfer") +
                " where fnum in(select FNum from " +
                pub.yssGetTableName("tb_cash_transfer") +
                " where FNumType='Common' and FRelaNum=" + dbl.sqlString(sNum) +
                " and FTsfTypeCode=" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_InnerAccount) +
                " and FSubTsfTypeCode=" +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_COST_FACT) + ")";
            dbl.executeSql(sqlStr);
            sqlStr = " delete from " + pub.yssGetTableName("tb_cash_transfer") +
                " where FNumType='Common' and FRelaNum=" + dbl.sqlString(sNum) +
                " and FTsfTypeCode=" +
                dbl.sqlString(YssOperCons.YSS_ZJDBLX_InnerAccount) +
                " and FSubTsfTypeCode=" +
                dbl.sqlString(YssOperCons.YSS_ZJDBZLX_COST_FACT);
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            try {
            	/**shashijie 2012-7-2 STORY 2475 */
				if (conn != null) {
					conn.rollback();
				}
				/**end*/
            } catch (Exception e) {
                throw new YssException("删除关联的资金调拨数据出错", e);
            }
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
    /**
     * @author zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * @throws YssException 
     * @方法名：eleTransferDeal ----------------------------------------------电子划拨指令报文处理控制方法
     * @参数：flag 操作标识  operType 操作类型
     * @返回类型：void
     * @说明：TODO
     */
    public String eleTransferDeal(String flag) throws YssException{
    	String operation="";
        SingleLogOper logOper = null;
        logOper = SingleLogOper.getInstance();
		if (flag.equalsIgnoreCase("send")) {
			operation=sendMessage();
			logOper.setIData(this, YssCons.OP_SEND, pub);//操作日志的类型
		} else if(flag.equalsIgnoreCase("backout")) {
			operation=backOut();
			logOper.setIData(this, YssCons.OP_CANCEL, pub);//操作日志的类型
		}else if(flag.equals("searchstatus")){
			searchStatus();    
		}else if(flag.equals("ModifyStutes")){
			ModifyStutes();
			logOper.setIData(this, YssCons.OP_EDIT, pub);//操作日志的类型
			
		}
		return operation;
    }
   
    /***
     * @author zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * @throws YssException 
     * @方法名：receipSearch--------------------------------------------------------------------修改状态
     * @参数：
     * @返回类型：void
     * @说明：TODO
     */
    private void ModifyStutes() throws YssException {
    	Connection conn = null;
		ResultSet rs = null;
		ResultSet subRs=null;
		boolean bTrans = false;
		String sqlStr = "";
		try {
			String table=" A"+getBookSetByPortcode(this.portCode)+"JjHkZl ";
			String checkstatus="select nvl(result,'null') as result from "+table+"  where fnum="+dbl.sqlString(this.sNum);
			if ((subRs = dbl.openResultSet(checkstatus)).next()) {
				if ("null".equalsIgnoreCase(subRs.getString("result"))){	//返回结果如果不为空 ，可以修改
					conn = dbl.loadConnection();
					conn.setAutoCommit(false);
					bTrans = true;
					sqlStr = "update "+table+" set remark="+dbl.sqlString(this.remark)+",result="+dbl.sqlString(this.result)+" where fnum="
							+ dbl.sqlString(this.sNum);						//”执行情况“按钮的数据设定
					dbl.executeSql(sqlStr);									// FFiletype=“1261” ，Frpttype=”01“， FIssend=1，Fsh=1
					conn.commit();
					bTrans = false;
					conn.setAutoCommit(true);
				}else if(subRs.getString("result").indexOf("手工：") > -1) {//并且返回结果包含‘手工：'标识可以修改
					conn = dbl.loadConnection();
					conn.setAutoCommit(false);
					bTrans = true;
					sqlStr = "update "+table+" set remark="+dbl.sqlString(this.remark)+",result="+dbl.sqlString(this.result)+" where fnum="
							+ dbl.sqlString(this.sNum);						//”执行情况“按钮的数据设定
					dbl.executeSql(sqlStr);									// FFiletype=“1261” ，Frpttype=”01“， FIssend=1，Fsh=1
					conn.commit();
					bTrans = false;
					conn.setAutoCommit(true);
				}else{
					throw new YssException("托管行抢先给了回执，不允许修改");
				}
			} else {
				throw new YssException("该指令还未发送!,无法修改其指令状态!");
			}
			
		}catch (Exception e) {
			try {
				/**shashijie 2012-7-2 STORY 2475 */
				if (conn!=null) {
					conn.rollback();
				}
				/**end*/
			}catch (Exception sqle) {			
			throw new YssException("修改指令状态出错", e);}
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
		
	}

	/***
     * @author  zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * @throws YssException 
     * @方法名：searchStatus-----------------------------------------------------------------执行指令状态
     * @参数：
     * @返回类型：void
     * @说明：TODO
     */
    private void searchStatus() throws YssException {
    	Connection conn = null;
		//ResultSet rs = null;//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		boolean bTrans = false;
		String sqlStr = "";
		ResultSet rs = null;
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			String table=" A"+getBookSetByPortcode(this.portCode)+"JjHkZl ";
			bTrans = true;
			

			//20130509 modified by liubo.Bug #7609.QDV4南方2013年04月24日01_B
			//关联电子对账表和划款指令表，查找某条划款指令在电子对账表中有无1261类型的数据
			//若存在该数据，则直接操作该条1261类型的数据
			//若不存在，则新建一条1261，并设置为查询状态
			//排除1241是因为1241代表发送状态，是独立的
			//===================================
			sqlStr = "select a.FNum,a.Fsn,b.Fsn as target from " + table + " a " +
					 " left join (select Fsn from TDZBBINFO where Ffiletype = '1261' and Ffiletype <> '1241') b " +
					 " on a.Fsn = b.Fsn " +
					 " where fnum = " + dbl.sqlString(this.sNum);
			
			rs = dbl.queryByPreparedStatement(sqlStr);
			
			if(rs.next())
			{
				if (rs.getString("target") != null)
				{
					sqlStr = "update TDzbbinfo set FFiletype = '1261', Frpttype = "+dbl.sqlString("01")+", " +
							 "FIssend = 1, Fsh = 1 where FSN = (select Fsn from "+table+"  where fnum="
							 + dbl.sqlString(this.sNum)+")" +
							 " and FFIleType <> '1241'";			
					dbl.executeSql(sqlStr);	
				}
				else
				{
					sqlStr = "insert into "
							 + " TDZBBINFO ("
							 + "FSN,FDATE,FZZR,FSHR,FSH,FISSEND,FSDR,FFILETYPE,FRPTTYPE) values ("
							 + dbl.sqlString(rs.getString("Fsn")) + ","
							 + dbl.sqlDate(YssFun.formatDate(this.sCommandDate,"yyyy-MM-dd")) + "," 
							 + dbl.sqlString(this.creatorCode)+ "," 
							 + dbl.sqlString(this.checkUserCode) 
							 + ",1" + ",1,"
							 + dbl.sqlString(this.creatorCode) + ","
							 + dbl.sqlString("1261") + ","
							 + dbl.sqlString("01")+")";
					dbl.executeSql(sqlStr);			
				}
			}
			//=================end==================
			
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			//rs.close();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
		}catch (Exception e) {
			try {
				/**shashijie 2012-7-2 STORY 2475 */
				if (conn != null) {
					conn.rollback();
				}
				/**end*/
			}catch (Exception sqle) {			
			throw new YssException("撤销该划款指令出错", e);}
		} finally {
			dbl.endTransFinal(conn, bTrans);
			dbl.closeResultSetFinal(rs);
		}
	}

	/**
     * @throws YssException 
     * @方法名：backOut ----------------------------------------------------------------------撤销指令方法
     * @参数：@author zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * @返回类型：void
     * @说明：TODO
     */
    private String backOut() throws YssException {
		Connection conn = null;
		ResultSet rs = null;
		boolean bTrans = false;
		String sqlStr = "";
		String sqlSub = "";
		try {
			conn = dbl.loadConnection();
			conn.setAutoCommit(false);
			bTrans = true;
			String table=" A"+getBookSetByPortcode(this.portCode)+"JjHkZl ";
			sqlStr = "select fDELBZ from " + table+ " where FNum="
					+ dbl.sqlString(this.sNum);
			rs = dbl.openResultSet(sqlStr);
			if (rs.next()) {
				if (rs.getString("FDELBZ").equals("C")) {
					throw new YssException("此指令序号已经被撤销");
				}
			} else {
				throw new YssException("此指令还尚未发送，无法进行撤销操作");
			}
			sqlStr = "update " + table
					+ // 撤销需要对电子划拨指令表的该数据的删除标志标识为：C
					" set FDelBz='C' " + " where FNum ="
					+ dbl.sqlString(this.sNum);
			dbl.executeSql(sqlStr); // 修改电子报文表中的FFiletype=“1261” ，Frpttype=”01“，
									// FIssend=1，Fsh=1
			
			//20130509 modified by liubo.Bug #7609.QDV4南方2013年04月24日01_B
			//对电子对账表的数据做撤销操作，不要操作到1241（发送状态）的数据。
			//==============================
			
			/**Start 20130626 modified by liubo.Bug #8448.QDV4赢时胜(上海开发)2013年6月26日06_B
			 * 这个update语句有个语法错误*/
			sqlSub = "update TDzbbinfo  set Frpttype="+dbl.sqlString("01")+",FIssend=1,Fsh=1 "
					+ "where fsn =(SELECT Fsn FROM "+table+" where fnum="
					+ dbl.sqlString(this.sNum) + ") and FFiletype<>'1241'";
			/**End 20130626 modified by liubo.Bug #8448.QDV4赢时胜(上海开发)2013年6月26日06_B*/
			//=============end=================
			dbl.executeSql(sqlSub);
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			rs.close();
			return "撤销成功";
		}catch (Exception e) {
			try{
				/**shashijie 2012-7-2 STORY 2475 */
				if (conn != null) {
					conn.rollback();
				}
				/**end*/
			}catch(Exception sqe){}
			throw new YssException("撤销该划款指令出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
			
		}
		
    }    

	/**
     * 
     * @方法名：sendMessage---------------------------------------------------------电子划拨发送报文的方法
     * @参数： zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * @返回类型：void
     * @说明：TODO
     */
    private String sendMessage() throws YssException {
		//begin------------------------------------电子划拨表中的数据初始化----------------------------------------------------
		String FZlDate=YssFun.formatDate(this.sCommandDate, "yyyy-MM-dd");  	//指令日期
		String FHKDate=YssFun.formatDate(this.accountDate, "yyyy-MM-dd");  	//划款日期
		String FNum=this.sNum; 			//编号
		String FDzDate=YssFun.formatDate(this.accountDate, "yyyy-MM-dd"); 	//到帐日期，报文系统以此为真实划款日期
		String FHkRen=this.payName;			//划款人
		String FHKBank=this.payOperBank;	//划款银行
		String FHKAcct=this.payAccountNO;	//划款账户
		double FHkJe=this.payMoney;			//划款金额
		String FHkRemark=this.cashUsage;	//划款备注
		String FSkRen=this.receiverName;	//收款人
		String FSkBank=this.reOperBank;		//收款人银行
		String FSkAcct=this.reAccountNO;	//收款人帐户
		String FSkYt=this.cashUsage;		//收款用途
		String FDelBz="N";					//删除标志 Y—删除，N—正常,C--撤销
		int Fzltype=this.orderType;			//指令类型
		String Fhktype=this.transferName;	//划款类型
		/**shashijie,2011.05.27,BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称*/
		String Fhktype2=this.transferName;  //划款类型2
		/**end*/
		String Fsn=getFsn(this.sCommandDate);				//报文发送序号
		String seq_no=getSEQ_NO();			//序列号，提交给托管行的业务流水号
		String RESULT="";					//托管行处理的结果
		String remark="";					//托管行返回的备注
		String CHECKER_CODE="";				//经办人
		String FPK_bookmark="";				//识别主键值：所有主键的字符串
		String TIMESTMP=this.sCommandDate;					//发送时间
		String operation_type=this.transferType;//电子对账类型
		String FYHSN="";					//银行间市场交易序号
		int FSH=this.checkStateId;			//审核标识
		String FZZR=this.creatorCode;		//制作人
		String FCHK=this.checkUserCode;		//审核人
		String FHkRemarkN=this.eleDesc;		//电子划款备注
		//end--------------------------------------初始化结束--------------------------------------------------------------------
		Connection conn = null;
        String sqlStr = "";
        String sqlBw="";
        String sqlbaowen="";
        String Nums = "";
        ResultSet rs = null;
        ResultSet subRs=null;
        boolean bTrans=false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(false);
            bTrans=true;
            String table=" A"+getBookSetByPortcode(this.portCode)+"JjHkZl ";
            sqlStr = "select * from " + table +//在电子划拨表中查找是否已经发送
              			" where fnum="+dbl.sqlString(this.sNum);
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                throw new YssException("该划拨指令已经发送，请重新选择!");
            }
            sqlStr = "insert into "
					+ table
					+ "(FZLDATE,"
					+ " FHKDATE,FNUM,FDZDATE,FHKREN,FHKBANK,FHKACCT,FHKJE,FHKREMARK,FSKREN,FSKBANK,FSKACCT,"
					+ " FSKYT,FDELBZ,FZLTYPE,FHKTYPE,FHKTYPE2,FSN,SEQ_NO,RESULT,REMARK,CHECKER_CODE,FPK_BOOKMARK,"
					+ " TIMESTMP,OPERATION_TYPE,FYHSN,FSH	,FZZR,FCHK,FHKREMARKN) values("
					+ dbl.sqlDate(FZlDate) + "," + dbl.sqlDate(FHKDate)
					+ "," + dbl.sqlString(FNum) + ","
					+ dbl.sqlDate(FDzDate) + "," + dbl.sqlString(FHkRen)
					+ "," + dbl.sqlString(FHKBank) + ","
					+ dbl.sqlString(FHKAcct) + "," + FHkJe + ","
					+ dbl.sqlString(FHkRemark) + ","
					+ dbl.sqlString(FSkRen) + "," + dbl.sqlString(FSkBank)
					+ "," + dbl.sqlString(FSkAcct) + ","
					+ dbl.sqlString(FSkYt) + "," + dbl.sqlString(FDelBz)
					+ "," + Fzltype + "," + dbl.sqlString(Fhktype) + ","
					+ dbl.sqlString(Fhktype2) + "," + dbl.sqlString(Fsn)
					+ "," + dbl.sqlString(seq_no) + ","
					+ dbl.sqlString(RESULT) + "," + dbl.sqlString(remark)
					+ "," + dbl.sqlString(CHECKER_CODE) + ","
					+ dbl.sqlString(FPK_bookmark) + ","
					+ dbl.sqlString(TIMESTMP) + ","
					+ dbl.sqlString(operation_type) + ","
					+ dbl.sqlString(FYHSN) + "," + FSH + ","
					+ dbl.sqlString(FZZR) + "," + dbl.sqlString(FCHK) + ","
					+ dbl.sqlString(FHkRemarkN) + ")";
			dbl.executeSql(sqlStr);
			/**shashijie 2011.05.27 BUG1915在新建划款指令界面上，在选择了划款指令类型后，后面的文本框中显示的不是划款指令名称*/
			
			//20130509 modified by liubo.Bug #7609.QDV4南方2013年04月24日01_B
			//发送状态的数据，状态由1261改变为1241
			//关联需求为MS01628《QDV4招商基金2010年08月23日01_A 》
			//===========================
			sqlBw = "select '1241' as FBWType from " + pub.yssGetTableName("tb_cash_command") + " a left join (select FHKType,FHKcode,FBWType from TDzTypeCodePP "
					+ "where fcheckstate=1) b on " +
					" b.FHKcode = a.FHKcode " +
			/**end*/
					" where a.FNum = " // 获取报文类型
					+ dbl.sqlString(this.sNum);
			//===========end================
			subRs = dbl.openResultSet(sqlBw);
			while (subRs.next()) {
				sqlbaowen = "insert into "
						+ " TDZBBINFO ("
						+ "FSN,FDATE,FZZR,FSHR,FSH,FISSEND,FSDR,FFILETYPE,FRPTTYPE) values ("
						+ dbl.sqlString(Fsn)
						+ ","
						+ dbl.sqlDate(YssFun.formatDate(this.sCommandDate,
								"yyyy-MM-dd")) + "," + dbl.sqlString(FZZR)
						+ "," + dbl.sqlString(FCHK) + ",1" + ",1,"
						+ dbl.sqlString(FZZR) + ","
						+ dbl.sqlString(subRs.getString("FBWType")) + ","
						+ dbl.sqlString("01")+")";
				dbl.executeSql(sqlbaowen);
			}
			conn.commit();
			bTrans = false;
			conn.setAutoCommit(true);
			return "发送成功";
        } catch (Exception e) {
            throw new YssException("向电子划拨指令表中插入数据出现异常", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(subRs);
        }
        
    }

	/**@throws YssException 
     * @ 获取电子调拨中的SEQ_NO编号
     * add by zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * SEQ_NO格式：“ZL+资产代码＋日期＋流水号”， 22位，例如ZL 000001 20100830 000001
     */
    public String getSEQ_NO() throws YssException{
    	String SEQ_NO="";
    	String Fsn="";
    	String strSql="";
    	ResultSet rs=null;
    	String fassetcode="";
    	try{
    		strSql="select distinct fassetcode from "
    			+pub.yssGetTableName("tb_para_portfolio")
    			+" where  fcheckstate=1 and fportcode="
    			+dbl.sqlString(this.portCode);
    		rs=dbl.openResultSet(strSql);
    	while(rs.next()){
    		fassetcode=rs.getString("fassetcode");
    	}
    	}catch(Exception ex){}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    	String table=" A"+getBookSetByPortcode(this.portCode)+"JjHkZl ";
    	SEQ_NO = "ZL"
				+ fassetcode
				+ YssFun.formatDatetime(YssFun.parseDate(this.sCommandDate)).substring(0, 8)
				+ dbFun.getNextInnerCode(table, dbl
						.sqlRight("SEQ_NO", 6), "000001",
						" where FZlDate = "
								+ dbl.sqlDate(YssFun.formatDate(this.sCommandDate, "yyyy-MM-dd")));
    	return SEQ_NO;
    }
    /**
     * 获取电子对账对账报文处理信息表中的Fsn编号
     * @author zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * Fsn：往该表插数据时生成，内部使用的规则，”DZ”+”YYYYMMDD”+顺序号（5个字符，前面补0）如DZ 20090513 00002）
     * @throws YssException  
     * @param 参数报文表中的查询日期this.sCommandDate
     */
    public String getFsn(String Date) throws YssException{
    	String Fsn="";
    	//---add by songjie 2011.05.17 BUG 1910 QDV4深圳赢时胜2011年5月9日01_B---//
    	String jjHkZlCode = dbFun.getNextInnerCode("A"+getBookSetByPortcode(this.portCode)+"JjHkZl",
    			 dbl.sqlRight("Fsn", 5), "00001",
				" where FZLDate = " + dbl.sqlDate(YssFun.formatDate(Date, "yyyy-MM-dd")));
    	String zbbinfoCode = dbFun.getNextInnerCode("TDzbbinfo", dbl.sqlRight("Fsn", 5), "00001",
				" where FDate = " + dbl.sqlDate(YssFun.formatDate(Date, "yyyy-MM-dd")));
    	//若A001JjHkZl中的FSN的最大编号 > TDzbbinfo中FSN的最大编号，则根据A001JjHkZl的FSN的最大编号生成最新的编号
    	if(YssFun.toInt(jjHkZlCode) > (YssFun.toInt(zbbinfoCode))){
    		Fsn = "DZ" + YssFun.formatDatetime(YssFun.parseDate(Date)).substring(0, 8) + jjHkZlCode;
    	}else{
    		Fsn = "DZ" + YssFun.formatDatetime(YssFun.parseDate(Date)).substring(0, 8) + zbbinfoCode;
    	}
    	//---add by songjie 2011.05.17 BUG 1910 QDV4深圳赢时胜2011年5月9日01_B---//
    	//---delete by songjie 2011.05.17 BUG 1910 QDV4深圳赢时胜2011年5月9日01_B---//
//    	Fsn = "DZ"
//				+ YssFun.formatDatetime(YssFun.parseDate(Date)).substring(0, 8)
//				+ dbFun.getNextInnerCode("TDzbbinfo", dbl
//						.sqlRight("Fsn", 5), "00001",
//						" where FDate = "
//								+ dbl.sqlDate(YssFun.formatDate(Date, "yyyy-MM-dd")));
    	//---delete by songjie 2011.05.17 BUG 1910 QDV4深圳赢时胜2011年5月9日01_B---//	
    	
    	return Fsn;
    }
   
    /**add by liuxiaojun 20130726 story 4094 删除自定义划款方式之前判断是否是固有划款方式           ---start*/
    public String getListViewData2() throws YssException {
    	String strSql = "";
        String reStr = "";
        ResultSet rs = null;
        try{
        	strSql =
        		"select FStatus from "+ pub.yssGetTableName("Tb_Cash_Command_Type")+
        		" where FTranType = " + dbl.sqlString(this.FTranType) + 
        		" and FPortCode =  " + dbl.sqlString(this.FPortCode) + 
        		" and FSellWay = " + dbl.sqlString(this.FSellWay);
        	rs = dbl.openResultSet(strSql);
            while (rs.next()) {
            	reStr = rs.getString("FStatus");
            }
        
    	return  reStr;
        }catch (Exception e) {
            throw new YssException("获取值出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
        
    }
    /**add by liuxiaojun 20130726 story 4094 删除自定义划款方式之前判断是否是固有划款方式           ---end*/
    
    /** zhouxiang MS01628 2010.09.19 关于招商基金需求之电子指令功能   
     * 使用组合代码获取套帐代码
     * @author zhouxiang 2010.09.26
     * modify by huangqirong 2013-04-24 bug #7486 调整组合套帐链接相关代码
     */
	public String getBookSetByPortcode(String portCode) {
		String bookset = "001";
		//String sql = "";
		//ResultSet rs = null;
		YssFinance finace = new YssFinance(); 
		
		try {
			finace.setYssPub(this.pub);
			String tmpSetId = finace.getBookSetId(pub.getAssetGroupCode() , portCode);
			
			/**Start 20130626 modified by liubo.Bug #8448.QDV4赢时胜(上海开发)2013年6月26日06_B
			 * 获取财务系统的表名的套账号时，需要将获取到的套账号格式化成类似“001”这样的格式。
			 * 使之与财务系统的数据表命名方式相同，避免造成查无此表的异常*/
			if(tmpSetId != null && tmpSetId.trim().length() > 0 )
			{
				if(YssFun.isNumeric(tmpSetId))
				{
					bookset = YssFun.formatNumber(YssFun.toInt(tmpSetId), "000");
				}
			}
			/**End 20130626 modified by liubo.Bug #8448.QDV4赢时胜(上海开发)2013年6月26日06_B*/
		} catch (Exception ex) {
		} finally {
			//dbl.closeResultSetFinal(rs);
		}
		return bookset;
	}

	/***
	 * @方法名：getFAssetCodeByPortCode
	 * @参数：portCode 组合代码  使用组合代码获取资产代码
	 * @返回类型：String
	 * @说明： MS01628 2010.09.19 关于招商基金需求之电子指令功能   
	 * @author zhouxiang 2010.09.26
	 */
	public String getFAssetCodeByPortCode(String portCode) {
		String FAssetCode = "";
		String sql = "";
		ResultSet rs = null;
		try {
			sql = "select FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") + " where fcheckstate=1 and fportcode= "
					+ dbl.sqlString(portCode);
			rs = dbl.openResultSet(sql);
			if (rs.next()) {
				FAssetCode = rs.getString("fbooksetcode");
			} else {
				throw new YssException("没有找到该组合对应的资产代码!");
			}
		} catch (Exception ex) {
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return FAssetCode;
	}
    
	/***
	 * @方法名：isShowCashType
	 * @返回类型：boolean
	 * @说明： BUG #302 划款指令界面增加“划款类型”为必输项，客户较少用此类型  是否显示划款类型
	 * @author fangjiang 2010.11.05
	 */
	public boolean isShowCashType() {
		String value = "";
		String sql = "";
		ResultSet rs = null;
		boolean flag = false;
		try {
			sql = " select fctlvalue from " + pub.yssGetTableName("Tb_Pfoper_Pubpara") + " where fpubparacode = 'CashType' and fparaid <> 0 ";
			rs = dbl.openResultSet(sql);
			if (rs.next()) {
				value = rs.getString("fctlvalue");
				value = value.split(",")[0];
			}
			if (value.equals("0")) {
				flag = true;
			}
		} catch (Exception ex) {
	
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return flag;
	}
	
	/**获取最大指令序号,原先不针对组合,现在得针对组合获取	
	 * shashijie 2011.03.14 TASK #3131::跨入新的一年时，每个组合的指令序号都要重新从1开始*/
    private String getOrderValue() throws YssException{
    	String sRes = "";
    	ResultSet rs = null;
    	try {
    		String sqlStr = "SELECT MAX(Forder) AS FOrder FROM " +
	            pub.yssGetTableName("tb_cash_command") +
	            " WHERE to_char(FCommandDate,'yyyy') = " + dbl.sqlString(YssFun.left(this.sCommandDate, 4)) +
	            //这里以年为单位取值,并且要针对组合
	        	" AND FPortCode = " + dbl.sqlString(this.portCode.trim().equals("")? "" : this.portCode) ;
	        rs = dbl.openResultSet(sqlStr);
	        if (rs.next()) {
	            if (rs.getString("FOrder") != null) {
	                sRes = rs.getInt("FOrder") + 1 + "";
	            } else {
	                sRes = "1";
	            }
	        } else {
	        	sRes = "1";
			}
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("获取最大指令序号出错!" + "\r\n" +e.getMessage());
		} finally{
			dbl.closeResultSetFinal(rs);
		}
        return sRes;
	}
    
    /**根据指令日期获取同一天当中的最大指令序号,拼接日期得出主见FNum的值*/
    private String getMaxNextAddOneToFNum() throws YssException{
    	String sRes = "";
    	ResultSet rs = null;
    	String date = YssFun.formatDate(this.sCommandDate, "yyyyMMdd");
    	try {
    		//edit by songjie 2011.06.08 BUG 2039 QDV4赢时胜上海2011年06月03日01_B
    		String sqlStr = "SELECT " + dbl.sqlString(date) + " || MAX(to_number(substr(FNum,9,length(FNum) - 8))) || '' AS FNum FROM " +
	            pub.yssGetTableName("tb_cash_command") +
	            " WHERE FNum LIKE " + dbl.sqlString(date+"%");
	        rs = dbl.openResultSet(sqlStr);
	        if (rs.next()) {
	            if (rs.getString("FNum") != null && rs.getString("FNum").length() > 8) {
	            	//同一天中编号最大值加1
	            	//---edit by songjie 2012.04.28 BUG 4337 QDV4赢时胜(测试)2012年04月20日01_B start---//
	            	long num = Long.valueOf(rs.getString("FNum").substring(8,rs.getString("FNum").length())) + 1;
	            	sRes = rs.getString("FNum").substring(0,8) + (num + "");
	            	//---edit by songjie 2012.04.28 BUG 4337 QDV4赢时胜(测试)2012年04月20日01_B end---//
	            } else {
	                sRes = date + "1" ;
	            }
	        } else {
	        	sRes = "1";
			}
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("获取最大指令序号出错!" + "\r\n" +e.getMessage());
		} finally{
			dbl.closeResultSetFinal(rs);
		}
        return sRes;
	}
	
    /**
     * 设置划款指令参数
     * add by songjie
     * 2011.05.14
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * @param hmCashFee
     * @return
     * @throws YssException
     */
    public CommandTypeBean setCommandInfo(HashMap hmCashFee,Date tradeDate)throws YssException{
    	this.portCode = (String)hmCashFee.get("PortCode");
		this.sCommandDate = YssFun.formatDate((Date)hmCashFee.get("ZLDate"));
		this.sCommandTime = "00:00:00";
        this.accountDate = YssFun.formatDate((Date)hmCashFee.get("DKDate"));
		this.accountTime = "00:00:00";
		this.payName = (String)hmCashFee.get("FKName");
		this.payOperBank = (String)hmCashFee.get("FKBank");
		this.payAccountNO = (String)hmCashFee.get("FKBankAccount");
		this.payCuryCode = (String)hmCashFee.get("FKCuryCode");
		this.dRate = 1;
		this.receiverName = (String)hmCashFee.get("SKName");
		this.reOperBank = (String)hmCashFee.get("SKBank");
		this.reAccountNO = (String)hmCashFee.get("SKBankAccount");
		this.reCuryCode = (String)hmCashFee.get("SKCuryCode");
		this.orderType = 1;//划款类型：付款
		this.order = "0";
		this.creatorCode=pub.getUserCode();
		this.sRelaType = "ValGnrt" + YssFun.formatDate(tradeDate, "yyyyMMdd");//表示为资产估值时生成的数据
		this.creatorTime=YssFun.formatDate(new java.util.Date(), "yyyyMMdd HH:mm:ss");//获取当前系统时间
    	return this;
    }
    
    /**
     * 删除资产估值生成的划款指令数据
     * add by songjie
     * 2011.05.16
     * 需求 759
     * QDV4工银2011年3月7日05_A
     * @throws YssException
     */
    public void deleteValGenerateData(String feeType)throws YssException{
    	String strSql = "";
    	try{
    		strSql = " delete from " + pub.yssGetTableName("Tb_Cash_Command") +
    		" where FNumType = " + dbl.sqlString(this.sRelaType) + " and FCashUsage like '%应付" + feeType + "%'";
    		dbl.executeSql(strSql);
    	}catch(Exception e){
    		throw new YssException("删除资产估值生成的划款指令数据出错");
    	}
    }
}
