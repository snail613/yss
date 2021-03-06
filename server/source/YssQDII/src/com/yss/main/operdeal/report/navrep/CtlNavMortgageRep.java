package com.yss.main.operdeal.report.navrep;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import com.yss.base.*;
import com.yss.log.SingleLogOper;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdeal.platform.pfoper.pubpara.*;
import com.yss.main.operdeal.report.*;
import com.yss.main.operdeal.report.navrep.pojo.*;
import com.yss.pojo.cache.*;
import com.yss.util.*;
import com.yss.main.etfoperation.etfshareconvert.ETFShareConvertAdmin;

public class CtlNavMortgageRep
    extends BaseAPOperValue implements IClientOperRequest {
    private Object obj = null;
    private java.util.Date dDate = null;//日期
    private String portCode = "";//组合
    private String sborker="";//券商
    public String getSborker() {
		return sborker;
	}


	public void setSborker(String sborker) {
		this.sborker = sborker;
	}


	public String getsMortgagecode() {
		return sMortgagecode;
	}


	public void setsMortgagecode(String sMortgagecode) {
		this.sMortgagecode = sMortgagecode;
	}
	private String sMortgagecode="";//抵押物代码
    private NavRepBean navRep = null;
	private int operType;
	private SingleLogOper logOper;
    public String getPortCode() {
        return portCode;
    }

  
    public Object getObj() {
        return obj;
    }

    public Date getDDate() {
        return dDate;
    }

  

    public void setPortCode(String portCode) {
        this.portCode = portCode;
    }

  

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public void setDDate(Date dDate) {
        this.dDate = dDate;
    }
 //-----------------------------------------------------------

    public CtlNavMortgageRep() {
    }

    public void init(Object bean) throws YssException {
        String reqAry[] = null;
        String reqAry1[] = null;
        String sRowStr = (String) bean;
        if (sRowStr.trim().length() == 0) {
            return;
        }
        reqAry = sRowStr.split("\n");
        reqAry1 = reqAry[0].split("\r");
        this.dDate = YssFun.toDate(reqAry1[1]);
        reqAry1 = reqAry[1].split("\r");
        this.portCode = reqAry1[1];
        reqAry1 = reqAry[2].split("\r");
        this.sborker=reqAry1[1];
        reqAry1 = reqAry[3].split("\r");
        this.sMortgagecode=reqAry1[1];
       
        //--------------------------end--------------------------//
    }

	public Object invokeOperMothed() throws YssException {
		// ---2009.04.18 蒋锦 添加 流程控制中适用组合的处理---//
		// 参数布局散乱不便操作 MS00003
		// 判断是否在组合中执行
		if (pub.getFlow() != null
				&& pub.getFlow().keySet().contains(pub.getUserCode())) {
			// 插入已执行组合
			((FlowBean) pub.getFlow().get(pub.getUserCode()))
					.setFPortCodes(portCode);
		}
		// -----------------------------------------------//
		// -------此处增加通用参数控制室为了在插入资产净值表数据时，若为中保则安之不同的投资经理插入不同的值。sj edit 20081120
		String netValueType = "";
		CtlPubPara pubpara = new CtlPubPara();     
		pubpara.setYssPub(pub);
		netValueType = pubpara.getNavType();
		
		doNAVDeal("NavMortgageSec", "Security");
		doNAVDeal("NavMortgageCash", "Cash");
		doTotalNavDeal();
		return null;
	}
	//计算抵押物资产净值 
   private void doTotalNavDeal() throws YssException {
	   String strSql="";
	   ResultSet rs=null;
	   NavRepBean navRep=null;
	   ArrayList reArr = new ArrayList();
	   BaseNavRep baseNav=new BaseNavRep();
	   baseNav.setYssPub(pub);
	   baseNav.initReport(this.dDate, this.portCode, this.sborker, this.sMortgagecode);
	   baseNav.deleteMortGageData("Total");
	   try{
		   strSql="select sum(a.fmarketvalue*a.finout) as fmarketvalue,sum(a.fportmarketvalue*a.finout) as fportmarketvalue from " 
		   		  + pub.yssGetTableName("tb_data_navmortgdate")
		   		  +" a where a.fdetail=0 and a.fnavdate="+dbl.sqlDate(this.dDate);
		   rs=dbl.openResultSet(strSql);
		   if(rs.next()){
			   navRep = new NavRepBean();
		       navRep.setNavDate(this.dDate); //净值日期
		       navRep.setPortCode(this.portCode);
		       navRep.setOrderKeyCode("Total7");
		       navRep.setKeyCode("抵押物净值:");
		       navRep.setDetail(0); //汇总
		       navRep.setReTypeCode("Total");
		       navRep.setMarketValue(rs.getDouble("fmarketvalue"));
		       navRep.setPortMarketValue(rs.getDouble("fportmarketvalue"));
		       navRep.setCuryCode(" ");
		   }
		   
	   }catch(Exception e){
		   throw new YssException("统计抵押物净值数据报错!");
	   }
	   reArr.add(navRep);
	   baseNav.insertMortGageTable(reArr);
	}


	private void doNAVDeal(String sBeanId, String Type) throws YssException {
	        ArrayList valData = null;
	        BaseNavRep val = (BaseNavRep) pub.getOperDealCtx().getBean(sBeanId); //获取子类
	        val.setYssPub(pub);
	        valData = val.buildRepData(dDate, portCode,sborker,sMortgagecode); //执行子类方法*/
	        val.deleteMortGageData(Type);
	        val.insertMortGageTable(valData);
	    }

    /**
     * 20091013 MS00002 QDV4.1赢时胜（上海）2009年9月28日01_A
     */
	private void doETFNAVDeal(String sBeanId, String Type) throws YssException {
        ArrayList valData = null;
        BaseNavRep val = (BaseNavRep) pub.getOperDealCtx()
            .getBean(sBeanId); //获取子类
        val.setYssPub(pub);
        val.bETFVal = true;
        /*valData = val.buildRepData(dDate, portCode, sborker,sMortgagecode); //执行子类方法
       val.deleteETFData(Type);*/
        val.insertETFTable(valData);
    }

    

  /*  *//**
     * 从净值数据表中获取相关汇总数据。sj edit 20080627
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @return Hashtable
     * @throws YssException
     *//*
    private Hashtable getReport(Date dDate, String portCode, String invMgrCode) throws
        YssException {
        Hashtable hmNetValue = null;
        String sqlStr = "";
        ResultSet rs = null;
        YssNetValue netValue = null;
        String key = "";
        try {
            hmNetValue = new Hashtable();
            sqlStr = "select FKeyCode,FPrice,FPortMarketValue from " +
                pub.yssGetTableName("Tb_Data_NavData") +
                " where FNavDate = " + dbl.sqlDate(dDate) + " and FPortCode = " +
                dbl.sqlString(portCode) +
                " and FInvMgrCode = " + dbl.sqlString(invMgrCode) +
                " and FReTypeCode in ('Total')";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                netValue = new YssNetValue();
                netValue.setInvMgrCode(this.invMgrCode);
                key = rs.getString("FKeyCode");
                if (key.equalsIgnoreCase("TotalValue")) { //资产净值
                    netValue.setPortNetValue(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("Unit")) { //单位净值
                    if (rs.getDouble("FPortMarketValue") == 0.0) {
                        netValue.setUnitPortNetValue(rs.getDouble("FPrice"));
                    } else {
                        netValue.setUnitPortNetValue(rs.getDouble("FPortMarketValue"));
                    }
                } else if (key.equalsIgnoreCase("MV")) { //估值增值
                    netValue.setIncPortMV(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("FX")) { //汇兑损益
                    netValue.setExPortFX(rs.getDouble("FPortMarketValue"));

                } else if (key.equalsIgnoreCase("TotalAmount")) { //实收资本
                    netValue.setCapitalPortCuryCost(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("UnPL")) { //损益平准金（未实现）
                    netValue.setPortUnPl(rs.getDouble("FPortMarketValue"));
                } else if (key.equalsIgnoreCase("PL")) { //损益平准金（已实现）
                    netValue.setPortPl(rs.getDouble("FPortMarketValue"));
                }
                hmNetValue.put(key, netValue);
            }
        } catch (Exception e) {
            throw new YssException("");
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hmNetValue;
    }
*/
    /**
     * 录入资产净值表。sj edit 20080627
     * @param hmNetValue Hashtable
     * @return String
     * @throws YssException
     */
    public String saveReport(Hashtable hmNetValue, String InvMgrCode) throws
        YssException {
        String strSql = "";
        PreparedStatement pst = null;
        YssNetValue netValue = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        Iterator iter = null;
        String key = "";
        try {
            conn.setAutoCommit(false);
            bTrans = true;
         	dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NetValue"));//添加行级锁 合并太平版本代码
            //删除原有记录
            if (InvMgrCode == null || InvMgrCode.trim().length() == 0) { //如果投资经理为空格.
                strSql = "delete from " + pub.yssGetTableName("Tb_Data_NetValue") +
//                  " where FInvMgrCode = " + dbl.sqlString(InvMgrCode) + //按投资经理为空格的纪录删除。
//                  " and FPortCode = " + dbl.sqlString(this.portCode) +
//                  " and FNAVDate = " + dbl.sqlDate(dDate);              //投资经理为空的话全删除
                    " where FPortCode = " + dbl.sqlString(this.portCode) +
                  //edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A
                    " and FInvMgrCode = ' ' and FNAVDate = " + dbl.sqlDate(dDate);
            } else {
                strSql = "delete from " + pub.yssGetTableName("Tb_Data_NetValue") +
                //edit by yanghaiming 20100624 MS01228 QDV4赢时胜(上海)2010年06月02日01_A
                    //" where FInvMgrCode <> " + dbl.sqlString(" ") + //将投资经理不是空格的纪录都删除。
                	" where FInvMgrCode = " + dbl.sqlString(InvMgrCode) +
                    " and FPortCode = " + dbl.sqlString(this.portCode) +
                    " and FNAVDate = " + dbl.sqlDate(dDate);
            }
            dbl.executeSql(strSql);

            //向资产净值表中插入基础货币资产净值
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_NetValue") +
                " (FNAVDate,FPortCode,FInvMgrCode,FBaseNetValue,FPortNetValue,FAmount,FType,FCheckState,FCreator,FCreateTime)" +
                " values(?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);

            Iterator it = hmNetValue.keySet().iterator();
            while (it.hasNext()) {
                key = (String) it.next();
                netValue = (YssNetValue) hmNetValue.get(key);
                if (InvMgrCode != null && InvMgrCode.trim().length() > 0) { //若参数InvMgrCode有值，则设置此投资经理的值。sj edit 20080714
                    netValue.setInvMgrCode(InvMgrCode);
                }
                if (key.equalsIgnoreCase("TotalValue")) { //插入资产净值
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) { //若为total,则设置为空格。sj edit 20080707.
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getBaseNetValue());
                    pst.setDouble(5, netValue.getPortNetValue());
                    pst.setDouble(6, 0);
                    pst.setString(7, "01"); //标识－资产净值
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();

                } else if (key.equalsIgnoreCase("Unit")) { //插入单位净值
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4,
                                  netValue.getUnitBaseNetValue());
                    pst.setDouble(5,
                                  netValue.getUnitPortNetValue());
                    pst.setDouble(6, 0);
                    pst.setString(7, "02"); //标识－单位净值
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("MV")) { //插入估值增值
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }

                    pst.setDouble(4, netValue.getIncBaseMV());
                    pst.setDouble(5, netValue.getIncPortMV());
                    pst.setDouble(6, 0);
                    pst.setString(7, "03"); //标识－估值增值
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("FX")) { //插入汇兑损益
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getExBaseFX());
                    pst.setDouble(5, netValue.getExPortFX());
                    pst.setDouble(6, 0);
                    pst.setString(7, "04"); //标识－汇兑损益
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("TotalAmount")) { //插入实收资本
//               netValue = (YssNetValue) hmNetValue.get(key);
                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getCapitalBaseCuryCost());
                    pst.setDouble(5, netValue.getCapital());
                    pst.setDouble(6, netValue.getCapitalPortCuryCost());
                    pst.setString(7, "05"); //标识－实收资本
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();
                } else if (key.equalsIgnoreCase("UnPL")) { //插入损益平准金（未实现）
//               netValue = (YssNetValue) hmNetValue.get(key);

                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getBaseUnPl());
                    pst.setDouble(5, netValue.getPortUnPl());
                    pst.setDouble(6, 0); //实收资本插入数量
                    pst.setString(7, "06"); //标识－损益平准金（未实现）
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();

                } else if (key.equalsIgnoreCase("PL")) { //插入损益平准金（已实现）
//               netValue = (YssNetValue) hmNetValue.get(key);

                    pst.setDate(1, YssFun.toSqlDate(dDate));
                    pst.setString(2, this.portCode);
                    if (netValue.getInvMgrCode().equalsIgnoreCase("total")) {
                        pst.setString(3, " ");
                    } else {
                        pst.setString(3, netValue.getInvMgrCode());
                    }
                    pst.setDouble(4, netValue.getBasePl());
                    pst.setDouble(5, netValue.getPortPl());
                    pst.setDouble(6, 0);
                    pst.setString(7, "07"); //标识－损益平准金（已实现）
                    pst.setInt(8, 1);
                    pst.setString(9, pub.getUserCode());
                    pst.setString(10, YssFun.formatDatetime(new java.util.Date()));
                    pst.executeUpdate();

                } 
//                else if (key.equalsIgnoreCase("Cash")) { //插入现金头寸
//                }
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("更新资产净值表信息出错" + "\n" + e.getMessage());
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeStatementFinal(pst);
        }
        return "";
    }

    /**
     * 更新净值表的市值比例
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @throws YssException
     */
    private void updateNavRatio(Date dDate, String portCode,
                                String invMgrCode) throws YssException {
        String sqlStr = "";
        String tempViewName = "";
	    Connection conn =dbl.loadConnection();
	    boolean bTrans =false;
        try {
            tempViewName = "V_Temp_NavRatio_" + pub.getUserCode();
            createNavRatioView(dDate, portCode, invMgrCode, tempViewName);
        } catch (Exception e) {
            throw new YssException(e);
        }
     	dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NavData")); //合并太平版本代码 加锁
        sqlStr = "update " + pub.yssGetTableName("tb_Data_NavData") + " nav set nav.FPortMarketValueRatio = (select FPortMarketRatio from " +
            tempViewName + "  where  FKeyCode = nav.Fkeycode" +
            " and FNavDate = nav.fnavdate " +
            " and FReTypeCode = nav.FReTypeCode" +
            " and FDetail = nav.FDetail" +
            " and FOrderCode = nav.FOrderCode" +
            ") where exists (select 'X' from " + tempViewName + " where nav.Fkeycode = " + tempViewName + ".FKeyCode" +
            " and nav.fnavdate = " + tempViewName + ".FNavDate and nav.FReTypeCode = " + tempViewName + ".FReTypeCode " +
            " and nav.FDetail = " + tempViewName + ".FDetail" +
            " and nav.FOrderCode = " + tempViewName + ".FOrderCode" +
            ")"+//fanghaoln 20100429 MS01103 QDV4华夏2010年4月20日01_B
            " and nav.FportCode = "  +dbl.sqlString(portCode);//QDII分盘在生成净值表要区分组合
            //------------------end ---MS01103----------------------
        try {
    	  	conn.setAutoCommit(bTrans);
          	bTrans =true;
            dbl.executeSql(sqlStr);
         	conn.commit();
         	conn.setAutoCommit(bTrans);
         	bTrans =false;
        } catch (Exception e) {
            throw new YssException("更新净值比例出错！", e);
	    }finally{
	      dbl.endTransFinal(conn, bTrans);
	    }
    }

    /**
     * 建立生成比例的视图
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @param tempViewName String
     * @throws YssException
     */
    private void createNavRatioView(Date dDate, String portCode,
                                    String invMgrCode, String tempViewName) throws YssException {
        String sqlStr = "";
        StringBuffer buf = null;
        try {
            if (tempViewName.trim().length() > 0 && dbl.yssViewExist(tempViewName)) {
                dbl.executeSql("drop view " + tempViewName);
            }
            //添加删除表的语句，系统优化，合并太平版本调整 by leeyu 20100824
            if (tempViewName.trim().length() > 0 && dbl.yssTableExist(tempViewName)) {
            	/**shashijie ,2011-10-12 , STORY 1698*/
                dbl.executeSql(dbl.doOperSqlDrop("drop table " + tempViewName));
                /**end*/
            }
          //添加删除表的语句，系统优化，合并太平版本调整 by leeyu 20100824
        } catch (Exception e) {
            throw new YssException("建立比例试图出错！", e);
        }
//      try {
        buf = new StringBuffer(4096);
        //buf.append("create  view ").append(tempViewName);
        buf.append("create  table ").append(tempViewName);//将视图改为临时表，系统优化，合并太平版本调整 by leeyu 20100824
        buf.append(" as (");
        buf.append(" select data.FKeyCode,");
        buf.append(" data.FDetail,");
        buf.append(" data.FCuryCode,");
        buf.append(" data.FNavDate,");
        buf.append(" data.FCost,");
        buf.append(" data.FPortCost,");
        buf.append(" data.FMarketValue,");
        buf.append(" data.FPortMarketValue,");
        buf.append(" data.FMVValue,");
        buf.append(" data.FFXValue,");
        buf.append(" data.FPortMVValue,");
        buf.append(" data.FSParAmt,");
        buf.append(" round((data.FPortMarketValue / totalvalue.FPortMarketValue) * 100, 4) as FPortMarketRatio,");
        buf.append(" data.FOrderCode,");
        buf.append(" data.FReTypeCode");
        buf.append(" from (select FKeyCode,");
        buf.append(" FCuryCode,");
        buf.append(" FNavDate,");
        buf.append(" FDetail,");
        buf.append(" FCost,");
        buf.append(" FPortCost,");
        buf.append(" FMarketValue,");
        buf.append(" FPortMarketValue,");
        buf.append(" FFXValue,");
        buf.append(" FMVValue,");
        buf.append(" FPortMVValue,");
        buf.append(" round(FSParAmt, 2) as FSParAmt,");
        buf.append(" 'link' as FLink,");
        buf.append(" FGradeType2,");
        buf.append(" FGradeType6,");
        buf.append(" FOrderCode,");
        buf.append(" FReTypeCode");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("tb_Data_NavData"));
        buf.append(" where  ");
//         buf.append(" and FDetail = 0");
        buf.append("  FInvMgrCode = ");
        buf.append(dbl.sqlString(invMgrCode));
//         buf.append(" and FGradeType2 = 'EQ01'");
        buf.append(" and ((FReTypeCode = 'Security' and (FGradeType6 is null or FGradeType6 = '')) or (FReTypeCode = 'Cash' and (FGradeType5 is null or FGradeType5 = '')) or (FReTypeCode = 'Invest')) ");
        buf.append(" and FPortCode = ");
        buf.append(dbl.sqlString(portCode));
        buf.append(" and FNAVDate = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(" ) data");
        buf.append(" left join (select FPortMarketValue, 'link' as FLink");
        buf.append(" from ");
        buf.append(pub.yssGetTableName("tb_Data_NavData"));
        buf.append(" where FNAVDate = ");
        buf.append(dbl.sqlDate(dDate));
        buf.append(" and FPortCode =");
        buf.append(dbl.sqlString(portCode));
        buf.append(" and FInvMgrCode =");
        buf.append(dbl.sqlString(invMgrCode));
        buf.append(" and FReTypeCode = 'Total'");
        buf.append(" and FKeyCode = 'TotalValue') totalvalue on data.FLink = ");
        buf.append(" totalvalue.FLink)");
        sqlStr = buf.toString();
        buf.delete(0, buf.length());
        try {
            dbl.executeSql(sqlStr);
            //添加主键 ，系统优化，合并太平版本调整 by leeyu 20100824
            sqlStr="alter table "+tempViewName+" add constraint PK_"+tempViewName
            +" primary key(FKeyCode,FDetail,FCuryCode,FNavDate,FOrderCode,FRetypeCode)";
            dbl.executeSql(sqlStr);
            //合并太平版本调整 by leeyu 20100824
        } catch (Exception e) {
            throw new YssException("生成净值比例视图出错！", e);
        }
    }

//-------为了使净值表可以确认，增加的接口。BugId:MS00184 QDV4交银施罗德2009年01月09日01_B sj modified 20090120
    public String checkRequest(String sType) throws YssException {
        return "";
    }

    public String doOperation(String sType) throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
    }

    public String buildRowStr() throws YssException {
        return "";
    }

    public String getOperValue(String sType) throws YssException {
        String[] confirms = null;
        String resultStr = "";
        if (sType.indexOf("@confirm@") > 0) { //若有，则为对当日的净值确认或反确认。
            confirms = sType.split("@confirm@");
            resultStr = doConfirmed(confirms[0], confirms[1]); //0位操作类型,1为参数
        } else if (sType.indexOf("@navInfo@") > 0) { //查询当日的净值为确认或反确认
            confirms = sType.split("@navInfo@");
            resultStr = getConfirmInfo(confirms[1]); //1为参数
        } else if (sType.equalsIgnoreCase("checkPerInterface")){
        	resultStr = isPerInterface();
        }
        return resultStr;
    }

    /**
     * 执行确认及反确认
     * BugId:MS00184 QDV4交银施罗德2009年01月09日01_B
     * @param doType String
     * @param params String
     * @return String
     * @throws YssException
     */
    private String doConfirmed(String doType, String params) throws YssException {
        String resultStr = "";
        //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能 QDV4华夏2009年12月29日01_A ===========  
        String[] reqDate = null;
        reqDate = params.split("\b\f\b")[0].split("~n~");//日期段的两个日期区间是用~n~分隔的
        String dNavDate = "";//净值日期
        dNavDate = reqDate[0];
        String dEndDate = "";//结束日期，如果是日期区间，此处才有值      
        boolean hasNavValue = true;
        if(reqDate!=null&&reqDate.length>1){
        	dEndDate = reqDate[1];
        }
        //edit by yanghaiming 20100408 MS01068 QDV4赢时胜上海2010年4月06日01_A 
        else{
        	dEndDate = dNavDate;
        }
        //===========end==================================================
//        String dNavDate = params.split("\b\f\b")[0]; //净值日期
        String sPortCode = params.split("\b\f\b")[1]; //组合
        String sInvMgrCode = params.split("\b\f\b")[2]; //投资经理
        BaseNavRep navConfirm = null; //对净值表操作的类
        ArrayList valData = null; //植入BaseNavRep
        NavRepBean nav = null; //净值数据的VO
        if (doType.equalsIgnoreCase("confirm")) { //确认净值
        	hasNavValue = hasNavTotalValue(dNavDate, dEndDate, sPortCode, sInvMgrCode);
//            if (hasNavValue != null && hasNavValue.length() > 1) { //判断是否已生成资产净值数据，若尚未生成，则抛出提示信息。
//            	//edit by yanghaiming 20100408 MS01068 QDV4赢时胜上海2010年4月06日01_A
//                throw new YssException("对不起！以下日期" + hasNavValue +"尚未生成资产净值数据，请生成资产净值数据再进行确认操作！");
//            }
        	//edit by yanghaiming 20100513 MS01068 QDV4赢时胜上海2010年4月06日01_A
        	if(!hasNavValue){
        		throw new YssException("对不起！" + dNavDate + "至" + dEndDate +"无资产净值数据，请生成资产净值数据再进行确认操作！");
        	}
            navConfirm = new BaseNavRep();
            navConfirm.portCode = sPortCode;
            if (!YssFun.isDate(dNavDate)) {
                throw new YssException("系统在确认净值时获取净值日期出现异常!");
            }
            navConfirm.dDate = YssFun.toDate(dNavDate);
          //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
            if(dEndDate!=null&&dEndDate.trim().length()>0&&YssFun.isDate(dEndDate)){
            	navConfirm.dEndDate =YssFun.toDate(dEndDate);
            }
            //==================end==============================
            navConfirm.invMgrCode = sInvMgrCode;
            navConfirm.setYssPub(pub);
            try {
                navConfirm.deleteData("confirm"); //删除确认信息
            } catch (YssException ex) {
                throw new YssException("系统在确认净值前的删除操作出现异常", ex);
            }
            valData = new ArrayList();
          //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能 QDV4华夏2009年12月29日01_A ===========  
            //循环日期区间，对其中的每个日期进行确认操作=============
            if (null == navConfirm.dEndDate){
            	navConfirm.dEndDate = navConfirm.dDate;
            }//若没有结束日期，不是区间段确认，就需要设置一个默认值。
            while(navConfirm.dDate.compareTo(navConfirm.dEndDate)<=0){//当dDate比dEndDate小的时候循环 
            	
            	nav = new NavRepBean(); //实例化净值数据的VO                
                nav.setReTypeCode("confirm"); //类型为净值确认数据
                nav.setInvMgrCode(sInvMgrCode);
                nav.setPortCode(sPortCode);
                nav.setNavDate(navConfirm.dDate);
                nav.setKeyCode("confirm");
                nav.setKeyName("确认净值表的锁定记录");
                nav.setOrderKeyCode("confirm" + YssFun.formatDate(navConfirm.dDate));
                nav.setCuryCode("confirmCury");
                valData.add(nav); //将净值数据放入ArrayList
                navConfirm.dDate=YssFun.addDay(navConfirm.dDate, 1);//加上一天，这样才能循环日期区间
            }
            //===========end====================================
            /*
            nav = new NavRepBean(); //实例化净值数据的VO
            //-----------------------------
            nav.setReTypeCode("confirm"); //类型为净值确认数据
            nav.setInvMgrCode(sInvMgrCode);
            nav.setPortCode(sPortCode);
            nav.setNavDate(YssFun.toDate(dNavDate));
            nav.setKeyCode("confirm");
            nav.setKeyName("确认净值表的锁定记录");
            nav.setOrderKeyCode("confirm" + dNavDate);
            nav.setCuryCode("confirmCury");
            valData.add(nav); //将净值数据放入ArrayList         */
            
            navConfirm.insertTable(valData); //将确认信息插入净值表
            
            //add by guojianhua  2010 09 13-------------------
            operType=17;
            logOper = SingleLogOper.getInstance();
            this.setFunName("navdata");
            this.setModuleName("dayfinish");
            this.setRefName("000499");
            logOper.setIData(this, operType, pub);
            //--------------end----------------------------------
            
            resultStr = "confirm"; //向前台返回当日以确认净值
        } else if (doType.equalsIgnoreCase("unconfirm")) { //反确认净值
            navConfirm = new BaseNavRep();
            navConfirm.portCode = sPortCode;
            if (!YssFun.isDate(dNavDate)) {
                throw new YssException("系统在反确认净值时获取净值日期出现异常!");
            }
            navConfirm.dDate = YssFun.toDate(dNavDate);
          //====add by xxm,2010.01.29.MS00900 建议增加跨区间确认与反确认净值表的功能
            if(dEndDate!=null&&dEndDate.trim().length()>0&&YssFun.isDate(dEndDate)){
            	navConfirm.dEndDate =YssFun.toDate(dEndDate);
            }
            //==================end==============================
            navConfirm.invMgrCode = sInvMgrCode;
            navConfirm.setYssPub(pub);
            //add by guojianhua  2010 09 13-------------------
            operType=18;
            logOper = SingleLogOper.getInstance();
            this.setFunName("navdata");
            this.setModuleName("dayfinish");
            this.setRefName("000499");
            logOper.setIData(this, operType, pub);
            //--------------end----------------------------------
            try {
                navConfirm.deleteData("confirm"); //删除确认信息
            } catch (YssException e) {
                throw new YssException("系统在反确认净值出现异常!", e);
            }
            resultStr = "unconfirm"; //向前台返回当日反确认净值
        }
        return resultStr;
    }

    /**
     * 获取当日的净值表确认情况
     * BugId:MS00184 QDV4交银施罗德2009年01月09日01_B
     * @param navInfo String
     * @return String
     * @throws YssException
     */
    private String getConfirmInfo(String navInfo) throws YssException {
        String sqlStr = "";
        String resultStr = "";
        ResultSet rs = null;
        String dNavDate = navInfo.split("\b\f\b")[0]; //净值日期
        String sPortCode = navInfo.split("\b\f\b")[1]; //组合
        String sInvMgrCode = navInfo.split("\b\f\b")[2]; //投资经理
        if (!hasNavTotalValue(dNavDate, sPortCode, sInvMgrCode)) { //若尚未生成净值数据，则向前台传递尚未生成的信息。
            resultStr = "ungenerate"; //未生成
            return resultStr; //返回此信息。
        }
        sqlStr = "select * from " + pub.yssGetTableName("tb_Data_NavData") +
            " where FInvMgrCode = " + dbl.sqlString(sInvMgrCode) + " and FReTypeCode = 'confirm' and FPortCode = " +
            dbl.sqlString(sPortCode) + " and FNavDate = " + dbl.sqlDate(dNavDate); //获取当日的净值确认情况
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                resultStr = "unconfirm"; //若为已确认，则向前台返回unconfirm。在按钮上显示为"反确认"的样式 。
            } else {
                resultStr = "confirm"; //若为未确认，则向前台返回confirm。在按钮上显示为"确认"的样式 。
            }
        } catch (Exception ex) {
            throw new YssException("获取净值确认信息出现异常!", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return resultStr;
    }

    /**
     * 判断当日的净值是否已生成
     * BugId:MS00184 QDV4交银施罗德2009年01月09日01_B 20090122
     * @param params String
     * @return boolean
     * @throws YssException
     */
    private boolean hasNavTotalValue(String dNavDate, String sPortCode, String sInvMgrCode) throws YssException {
        boolean hasNavUnit = false;
        String sqlStr = "";
        ResultSet rs = null;
        sqlStr = "select * from " + pub.yssGetTableName("tb_Data_NavData") +
            " where FInvMgrCode = " + dbl.sqlString(sInvMgrCode) + " and FReTypeCode = 'Total' and FKeyCode = 'TotalValue' and FPortCode = " +
            dbl.sqlString(sPortCode) + " and FNavDate = " + dbl.sqlDate(dNavDate); //获取当日的资产净值情况;
        try {
            rs = dbl.openResultSet(sqlStr);
            if (rs.next()) {
                hasNavUnit = true;
            }
        } catch (Exception ex) {
            throw new YssException("系统在判断当日是否已生成净值时出现异常！", ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return hasNavUnit;
    }
//-------------------------------------------------------------------------------
    /**
     * 增加单位成本和涨跌
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @throws YssException
     * MS00570 QDV4华安2009年07月16日01_AB
     */
    private void updateCostAndChangeWithCost(Date dDate, String portCode,
                                             String invMgrCode) throws YssException {
        String sqlStr = null;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NavData"));//添加行级独占锁 合并太平版本调整添加 by leeyu 20100825
        sqlStr = "update " + pub.yssGetTableName("tb_Data_NavData") +
            " set FPortUnitCost = case FSPARAMT when 0 then  0 else FPortCost/FSPARAMT end," + //成本/股数
            " FPortChangeWithCost = case FPortCost when 0 then 0 else FPortMVValue/FPortCost end, " + //浮动盈亏/成本
            //=========================by xuxuming,20090818.MS00637 QDV4华安2009年08月14日01_AB=======================
            " FUnitCost = case FSPARAMT when 0 then  0 else FCost/FSPARAMT end," + //原币成本/股数
            " FChangeWithCost = case FCost when 0 then 0 else FMVValue/FCost end " + //原币浮动盈亏/原币成本
            //=======================================================================================================
            " where " +
            "(FReTypeCode = 'Security' and FDetail = 0 and (FGradeType6 is null or FGradeType6 = ''))" +//不获取应收应付的数据来进行计算。
            " and FinvMgrCode = " + dbl.sqlString(invMgrCode) +
            " and FPortCode = " + dbl.sqlString(portCode) +
            " and FNavDate = " + dbl.sqlDate(dDate);
        try {
            con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        }
        catch (Exception e) {
            throw new YssException("更新单位成本和涨跌出错！", e);
        }
        finally{
            dbl.endTransFinal(con, bTrans);
        }

    }
    /**
     * 更新新股行情价格
     * @param dDate Date
     * @param portCode String
     * @param invMgrCode String
     * @throws YssException
     * add by yanghaiming 20091124 MS00824 QDV4赢时胜海富通2009年11月20日01_B
     */
    private void updateNewSharePrice(Date dDate, String portCode,
    									String invMgrdode) throws YssException {
    	String sqlStr = null;
        boolean bTrans = false;
        Connection con = dbl.loadConnection();
        dbl.lockTableInRowExclusiveMode(pub.yssGetTableName("tb_Data_NavData"));//添加行级独占锁 合并太平版本调整添加 by leeyu 20100825
        sqlStr = "update " + pub.yssGetTableName("tb_Data_NavData") +
        	" set FPRICE = case FSPARAMT when 0 then 0 else ROUND(FCOST/FSPARAMT,2) end" +
        	" where " +
        	"FRETYPECODE = 'Security' and  FGradeType5 is not null  and FPRICE = 0" +//xuqiji 2010-02-09 MS00927 : QDV4赢时胜上海2010年01月12日01_AB 
        	" and FinvMgrCode = " + dbl.sqlString(invMgrdode) +
            " and FPortCode = " + dbl.sqlString(portCode) +
            " and FNavDate = " + dbl.sqlDate(dDate);
        try{
        	con.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(sqlStr);
            con.commit();
            bTrans = false;
            con.setAutoCommit(true);
        }catch (Exception e) {
        	throw new YssException("更新新股行情价格出错！", e);
        }
        finally{
            dbl.endTransFinal(con, bTrans);
        }
    }
    
    /***
     * 获取是否显示B股个性化界面通用参数
     * @return String
     * @throws YssException 
     */
    private String isPerInterface() throws YssException{
    	CtlPubPara ctlPubPara = new CtlPubPara();
    	ctlPubPara.setYssPub(pub);
    	String result = ctlPubPara.getPerInterface();
    	return result;
    }
	/**
     * 判断日期段内净值是否已生成
     * BugId:MS01068 QDV4赢时胜上海2010年4月06日01_A  20100408 
     * @author yanghaiming
     * @param params String
     * @return String
     * @throws YssException
     */
    private boolean hasNavTotalValue(String dNavDate, String dEndDate, String sPortCode, String sInvMgrCode) throws YssException {
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date dDate = YssFun.toDate(dNavDate);
        boolean flag = false;
        while(dDate.compareTo(YssFun.toDate(dEndDate))<=0){
        	if(hasNavTotalValue(format.format(dDate), sPortCode, sInvMgrCode)){
        		flag = true;
        	}
        	dDate = YssFun.addDay(dDate, 1);
        }
		return flag;
    }
}
