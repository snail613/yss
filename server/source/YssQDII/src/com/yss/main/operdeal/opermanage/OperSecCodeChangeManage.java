/**
 * 
 */
package com.yss.main.operdeal.opermanage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import com.yss.commeach.EachRateOper;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.SecPecPayBean;
import com.yss.main.operdata.futures.OptionsIntegratedAdmin;
import com.yss.main.parasetting.SecurityCodeChangeBean;
import com.yss.manager.SecRecPayAdmin;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 * @包名：com.yss.main.operdeal.opermanage
 * @文件名：OperSecCodeChange.java
 * @创建人：zhangfa
 * @创建时间：2010-8-9
 * @版本号：0.1
 * @说明：TODO <P>
 * @修改记录 日期 | 修改人 | 版本 | 说明<br>
 *       ----------------------------------------------------------------<br>
 *       2010-8-9 | zhangfa | 0.1 |
 */
public class OperSecCodeChangeManage extends BaseOperManage {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yss.main.operdeal.opermanage.BaseOperManage#doOpertion()
	 */
	public void doOpertion() throws YssException {
		String sql = "";
		String strSql="";
		
		
		// 1.获取变更前后的证券代码
		ArrayList scclist = getSecuritys();
		// 2.获取证券代码的前一日库存信息,并且保持到综合业务表中
		
		//add by zhouwei 20120428 bug4367 产生转入的债券利息数据
		ArrayList secRecPayList =new ArrayList();
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		SecRecPayAdmin recPayAdmin = new SecRecPayAdmin();
		
		
		if(scclist.size()==0){
			this.sMsg="        当日无业务";
		}
		//-----------end---
		try {
			//modify by zhangfa 20100903 MS01674    关于证券代码变更业务的问题    QDV4赢时胜(32上线测试)2010年8月30日01_B   
			//3.保存数据之前,先删除旧数据,后删库存数据
			sql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated")
					+ " where FOperDate=" + dbl.sqlDate(this.dDate)
					+ " and FTradeTypeCode='87'" + " and FPortCode in("
					+ dbl.sqlString(this.sPortCode)+") and FDataOrigin <>'1' ";//modify by zhangjun 2012.07.03 BUG4862业务处理后，综合业务下手工录入一笔交易类型为87的证券更名业务数据被清除 
		    dbl.executeSql(sql);
           //----------------------------------------------------------------------------------------------------
			for (int i = 0; i < scclist.size(); i++) {				
				
				SecurityCodeChangeBean scc = (SecurityCodeChangeBean) scclist.get(i);
				
				//modify by zhangfa 20100903 MS01674    关于证券代码变更业务的问题    QDV4赢时胜(32上线测试)2010年8月30日01_B   
				/*String num=getFum(this.dDate,"87",scc.getSecurityCodeBefore(),this.sPortCode,-1," ");
				if (num != null && num.length() != 0) {
					String[] realnum = getRelaNum(num).split(",");
					for (int j = 0; j < realnum.length; j++) {
						
						strSql = "delete from "
								+ pub.yssGetTableName("Tb_Data_SecRecPay")
								+ " where FNum=" + dbl.sqlString(realnum[j]);
						dbl.executeSql(strSql);
					}
				}
				if(scc==null){
					continue;
				}
				**/
				//----------------------------------------------------------------------------
				
				//保存数据
				saveSecurityStock(scc,i);
				//saveSecRecPays( scc,sNewNum, i);
				

				setSecRecPayData(secRecPayList, scc);//add by zhouwei 20120428 bug4367
			}
			//modify huangqirong 2012-07-07 bug #4940
			if(secRecPayList.size() > 0){
				//add by zhouwei bug4367 保存证券应收应付数据
				conn.setAutoCommit(false);
	            bTrans = true;
	            recPayAdmin.addList(secRecPayList);
	            recPayAdmin.setYssPub(pub);
	            recPayAdmin.insert("",
	                               dDate,
	                               dDate,
	                               "",
	                               "",
	                               sPortCode,
	                               "", "", "", "",
	                               0,
	                               true,
	                               0,
	                               false,
	                               "", "",
	                              "SecCodeChange");
	            conn.commit();
	            bTrans = false;
	            conn.setAutoCommit(true);
			}
			//---end---
		} catch (Exception e) {
			throw new YssException("处理证券代码变更业务时出错！", e);
		} finally{
            dbl.endTransFinal(conn, bTrans);
        }

	}
	private void setSecRecPayData(ArrayList list,SecurityCodeChangeBean scc) throws YssException{
		ResultSet rs=null;
		String sql="";
		try{
			//查询变更前的证券应收应付库存
			 sql = "select a.*,b.fexchangecode from "
					+ pub.yssGetTableName("Tb_stock_secrecpay")+" a"
					+" left join "+pub.yssGetTableName("tb_para_security")+" b"
					+" on a.FSecurityCode=b.FSecurityCode"
					+ "  where a.FSecurityCode="
					+ dbl.sqlString(scc.getSecurityCodeBefore())
					+ " and a.FCheckState=1   and a.FStorageDate=" + dbl.sqlDate(YssFun.addDay(this.dDate, -1)) 					
					+ " and a.FPortCode = " + dbl.sqlString(this.sPortCode)
					+ " and a.FSUBTSFTYPECODE='06FI'";//债券利息
			 rs=dbl.openResultSet(sql);
			 while(rs.next()){
				 getRecPayData(list,rs,scc);
			 }
		}catch (Exception e) {
			throw new YssException("证券代码变更产生证券应收应付数据出错！"+e, e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/** 
	 * add by zhouwei 证券应收应付对象赋值
	* @Title: getRecPayData 
	* @Description: TODO
	* @param @param alRecPay
	* @param @param rs
	* @param @throws YssException    设定文件 
	* @return void    返回类型 
	* @throws 
	*/
	private void getRecPayData( ArrayList alRecPay,ResultSet rs,SecurityCodeChangeBean scc) throws YssException{

        SecPecPayBean recPay = null;
        double baseCuryRate = 0;
        double portCuryRate = 0;
        try {
            EachRateOper rateOper = new EachRateOper(); //新建获取利率的通用类
            rateOper.setYssPub(pub);
            baseCuryRate = this.getSettingOper().getCuryRate(this.dDate,
                rs.getString("FCURYCODE"), rs.getString("FPortCode"),
                YssOperCons.YSS_RATE_BASE);

            rateOper.getInnerPortRate(this.dDate, rs.getString("FCURYCODE"),
                                      rs.getString("FPortCode"));
            portCuryRate = rateOper.getDPortRate();

            //-----------------------转入债券利息--------------------------//
            recPay = new SecPecPayBean();
            recPay.setInvestType(rs.getString("FINVESTTYPE"));
            recPay.setTransDate(this.dDate);
            recPay.setCheckState(1);
            recPay.setAttrClsCode(("CY".equalsIgnoreCase(rs.getString("fexchangecode")))?" ":rs.getString("FATTRCLSCODE"));//转入所属分类
            recPay.setRelaNum(" ");
            recPay.setRelaNumType("SecCodeChange");//证券代码变更
            recPay.setStrPortCode(rs.getString("FPortCode"));
            recPay.setInvMgrCode(rs.getString("FANALYSISCODE1"));
            recPay.setBrokerCode(rs.getString("FANALYSISCODE2")); //modify huangqirong 2012-07-25 bug #4940

            recPay.setStrSecurityCode(scc.getSecurityCodeAfter());
            recPay.setStrCuryCode(rs.getString("FCURYCODE"));
            recPay.setStrTsfTypeCode(rs.getString("FTsftypecode"));//modify huangqirong 2012-07-25 bug #4940
            recPay.setStrSubTsfTypeCode("06FI_B");

            recPay.setInOutType(1);
            recPay.setMoney(rs.getDouble("FBAL"));

            recPay.setBaseCuryRate(baseCuryRate);
            recPay.setPortCuryRate(portCuryRate);

            recPay.setMMoney(recPay.getMoney());
            recPay.setVMoney(recPay.getMoney());

            recPay.setBaseCuryMoney(this.getSettingOper().calBaseMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), 2));
            recPay.setMBaseCuryMoney(recPay.getBaseCuryMoney());
            recPay.setVBaseCuryMoney(recPay.getBaseCuryMoney());

            recPay.setPortCuryMoney(this.getSettingOper().calPortMoney(recPay.getMoney(),
                recPay.getBaseCuryRate(), recPay.getPortCuryRate(),
                rs.getString("FCURYCODE"), this.dDate, recPay.getStrPortCode(), 2));

            recPay.setMPortCuryMoney(recPay.getPortCuryMoney());
            recPay.setVPortCuryMoney(recPay.getPortCuryMoney());

            recPay.setPortCuryMoneyF(recPay.getPortCuryMoney());
            recPay.setBaseCuryMoneyF(recPay.getBaseCuryMoney());
            recPay.setMoneyF(recPay.getMoney());
            alRecPay.add(recPay);
        } catch (Exception ex) {
            throw new YssException("处理应收应付数据出错！", ex);
        }
    
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yss.main.operdeal.opermanage.BaseOperManage#initOperManageInfo(java
	 * .util.Date, java.lang.String)
	 */
	public void initOperManageInfo(Date dDate, String portCode)
			throws YssException {
		this.dDate = dDate; // 调拨日期
		this.sPortCode = portCode; // 组合

	}

	public ArrayList getSecuritys() throws YssException {
		ArrayList scclist = new ArrayList();
		String sql = "";
		
		ResultSet rs = null;
		SecurityCodeChangeBean scc = null;
		try {
			//modify by zhangfa 20100903 MS01674    关于证券代码变更业务的问题    QDV4赢时胜(32上线测试)2010年8月30日01_B    
			sql = "select * from "
					+ pub.yssGetTableName("Tb_Para_SecCodeChange")
					+ " where FBUSINESSDATE="+dbl.sqlDate(this.dDate) 
					+ " and FCheckState=1";
			//------------------------------------------------------------------------------------------
			rs = dbl.queryByPreparedStatement(sql);
			while (rs.next()) {
				scc = new SecurityCodeChangeBean();
				scc.setSecurityCodeBefore(rs.getString("FSecurityCodeBefore"));
				scc.setSecurityCodeAfter(rs.getString("FSecurityCodeAfter"));
				scclist.add(scc);
			}
		} catch (Exception e) {
			throw new YssException("获取证券代码变更时出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs); // 释放资源
		}

		return scclist;

	}
	//获取变更后的证券应收应付数据
	public SecPecPayBean saveSecRePayBefore(String FTsfTypeCode,String FSubTsfTypeCode,SecurityCodeChangeBean scc) throws YssException{
		SecPecPayBean secPay = null;
		String sql = "";
		ResultSet rs=null;
		try{
			sql="select p.*,t.* from  (select * from "
				+ pub.yssGetTableName("Tb_Stock_SecRecPay")
				+ "   where FSecurityCode="
				+ dbl.sqlString(scc.getSecurityCodeBefore())
				+ "  and FStorageDate=" + dbl.sqlDate(YssFun.addDay(this.dDate, -1))
				+" and FTsfTypeCode="+dbl.sqlString(FTsfTypeCode)+"  and FSubTsfTypeCode="+dbl.sqlString(FSubTsfTypeCode)+") p"
			    +"  join (select * from "+pub.yssGetTableName("Tb_Stock_Security") +" )t  on  p.FSecurityCode=t.FSecurityCode" +
			    "  and p.FStorageDate=t.FStorageDate ";
			rs=dbl.queryByPreparedStatement(sql);
			while(rs.next()){
				    secPay = new SecPecPayBean();
			        secPay.setStrPortCode(this.sPortCode);       //组合代码
			        secPay.setStrSecurityCode(scc.getSecurityCodeAfter());

			        secPay.setStrCuryCode(rs.getString("FCuryCode"));       //货币代码
			        secPay.setInOutType(1);         //流入流出方向,默认为正方向
			        secPay.setTransDate(this.dDate) ;           //业务日期
			        secPay.setStrTsfTypeCode(FTsfTypeCode);
			        secPay.setStrSubTsfTypeCode(FSubTsfTypeCode);
			        secPay.setBaseCuryRate(rs.getDouble("FBaseCuryRate"));
			        secPay.setPortCuryRate(rs.getDouble("FPortCuryRate"));
			        
			        secPay.setMoney(rs.getDouble("FBal"));
			        secPay.setMMoney(rs.getDouble("FMBal"));
			        secPay.setVMoney(rs.getDouble("FVBal"));
			       
			        secPay.setBaseCuryMoney(rs.getDouble("FBaseCuryBal"));   //基础货币金额
			        secPay.setMBaseCuryMoney(rs.getDouble("FMBaseCuryBal"));
			        secPay.setVBaseCuryMoney(rs.getDouble("FVBaseCuryBal"));
			        
			        secPay.setPortCuryMoney(rs.getDouble("FPortCuryBal"));
			        secPay.setMPortCuryMoney(rs.getDouble("FMPortCuryBal"));
			        secPay.setVPortCuryMoney(rs.getDouble("FVPortCuryBal"));
			        
			        secPay.setInvMgrCode(rs.getString("FAnalysisCode1"));
			        secPay.setBrokerCode(rs.getString("FAnalysisCode2"));
			        secPay.setExchangeCode(rs.getString("FAnalysisCode3"));
			        
			        secPay.setCatTypeCode(rs.getString("FCatType"));
			        secPay.setAttrClsCode(rs.getString("FAttrClsCode"));
			        secPay.setInvestType(rs.getString("FInvestType"));
			        
			        secPay.setRelaNumType("SecNameChange");
			        
			        pub.setbSysCheckState(true);
			        secPay.setYssPub(pub);
			        secPay.setInvestType(" ");
			        secPay.creatorCode=pub.getUserCode();
			        secPay.creatorTime=YssFun.formatDatetime(new Date());
			        secPay.addSetting();
			}
			
			return secPay;
			
		}catch (Exception e) {
			throw new YssException("获取证券应收应付数据时出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);


		}
	
		
	}
	//保存单条应收应付数据到综合业务表中
	public void saveSecRecPay(SecPecPayBean secPay,String sNewNum,int i) throws YssException{
		String strSql="";
		String sSubNum = "";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;	
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement ps=null;
		YssPreparedStatement ps=null;
        //=============end====================
		//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		try{
			if(secPay==null){
				return;
			}
		   conn.setAutoCommit(false);
  		   bTrans = true;
			
			strSql = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc," +
            " FCheckState,FCreator,FCreateTime,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FInvestType) " + 
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//			 ps = dbl.getPreparedStatement(strSql);
			ps = dbl.getYssPreparedStatement(strSql);
			//==============end================
			 
			//add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
			   sSubNum = integrateAdmin.getKeyNum();
				ps.setString(1, sNewNum);
				ps.setString(2, sSubNum);
				ps.setInt(3, 1);
				ps.setString(4, secPay.getStrSecurityCode());
				ps.setDate(5, YssFun.toSqlDate(this.dDate)); // 兑换日期是this中的
				ps.setDate(6, YssFun.toSqlDate(this.dDate));
				ps.setString(7, "87");
				ps.setString(8, secPay.getStrNum()); // 这里的 sRelaNum,sNumType都为' '
				ps.setString(9, "SecRecPay");
				ps.setString(10, this.sPortCode);
				ps.setString(11, " ");
				ps.setString(12, " ");
				ps.setString(13, " ");
				ps.setDouble(14, 0.0);
				ps.setDouble(15, secPay.getMoney());
				ps.setDouble(16, secPay.getMMoney());
				ps.setDouble(17, secPay.getVMoney());
				ps.setDouble(18, secPay.getBaseCuryMoney());
				ps.setDouble(19, secPay.getMBaseCuryMoney());
				ps.setDouble(20, secPay.getVBaseCuryMoney());
				ps.setDouble(21, secPay.getPortCuryMoney());
				ps.setDouble(22, secPay.getMPortCuryMoney());
				ps.setDouble(23, secPay.getVPortCuryMoney());
				ps.setDouble(24, secPay.getBaseCuryRate());
				ps.setDouble(25, secPay.getPortCuryRate());
				ps.setString(26, " ");
				ps.setString(27, " ");
				ps.setInt(28,  1);
				ps.setString(29, pub.getUserCode());
				ps.setString(30, YssFun.formatDatetime(new Date()));
				ps.setString(31, secPay.getStrTsfTypeCode());
				ps.setString(32, secPay.getStrSubTsfTypeCode());
				ps.setString(33, " ");
				ps.setString(34, " ");
			 
				   
			   ps.executeUpdate();
               conn.commit();
			   conn.setAutoCommit(true);
               bTrans = false;
		}catch (Exception e) {
			throw new YssException("保存单条应收应付数据到综合业务表时出错！", e);
		} finally {
			dbl.closeStatementFinal(ps);
			dbl.endTransFinal(conn, bTrans);
		}
		
	}
	//保存综合业务中的证券应收应付分页的信息
	public void saveSecRecPays(SecurityCodeChangeBean scc,String sNewNum,int i) throws YssException{

		try {
			
			 //成本的汇兑损益
			 SecPecPayBean cbhdsy= saveSecRePayBefore("99","9905EQ", scc);
			//估值增值汇兑损益
			 SecPecPayBean gzhdsy= saveSecRePayBefore("99","9909EQ", scc);
			 //股票估值增值
			 SecPecPayBean gpgz=saveSecRePayBefore("09","09EQ", scc);
			 if(gpgz!=null){
			    saveSecRecPay(gpgz,sNewNum,i+3) ;
			 }
			 if(cbhdsy!=null){
			    saveSecRecPay(cbhdsy,sNewNum,i+4) ;
			 }
			 if(gzhdsy!=null){
			    saveSecRecPay(gzhdsy,sNewNum,i+5) ;
			 }
		
		}catch (Exception e) {
			throw new YssException("处理证券代码变更业务时出错！", e);
		} finally {

		}
	}
	//保存综合业务中的证券成本分页信息
	/*modify huangqirong 2012-07-11 bug #4940*/
	public void saveSecurityStock(SecurityCodeChangeBean scc,int i) throws YssException{
		String strSqlB="";
		String strSqlA="";
		Connection conn = dbl.loadConnection();
		boolean bTrans = false;
		String sql = "";
		String sSubNum = "";
		String sNewNum = "";
		//modified by liubo.Story #2145
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//		PreparedStatement pstB = null;
//		PreparedStatement pstA = null;
		YssPreparedStatement pstB = null;
		YssPreparedStatement pstA = null;
        //=============end====================
		ResultSet rs = null;
		//---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
        OptionsIntegratedAdmin integrateAdmin = new OptionsIntegratedAdmin();
        integrateAdmin.setYssPub(pub);
        //---add by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
		try {
			 conn.setAutoCommit(false);
	  		   bTrans = true;
				
			strSqlB = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc," +
            " FCheckState,FCreator,FCreateTime,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FInvestType,FDataOrigin) " + 
            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//modified by liubo.Story #2145
			//==============================
//			 pstB = dbl.getPreparedStatement(strSqlB);
			pstB = dbl.getYssPreparedStatement(strSqlB);
				//==============end================
			 
			 strSqlA = "insert into " + pub.yssGetTableName("Tb_Data_Integrated") +
	            " (FNum,FSubNum,FInOutType,FSecurityCode,FExchangeDate,FOperDate,FTradeTypeCode,FRelaNum,FNumType," +
	            " FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FAmount,FExchangeCost,FMExCost," +
	            " FVExCost,FBaseExCost,FMBaseExCost,FVBaseExCost,FPortExCost," +
	            " FMPortExCost,FVPortExCost,FBaseCuryRate,FPortCuryRate,FSecExDesc,FDesc," +
	            " FCheckState,FCreator,FCreateTime,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FInvestType,FDataOrigin) " + 
	            " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				//modified by liubo.Story #2145
				//==============================
//			 pstA = dbl.getPreparedStatement(strSqlA);
			 pstA = dbl.getYssPreparedStatement(strSqlA);
				//==============end================
			 
			 sql = "select * from "
					+ pub.yssGetTableName("Tb_Stock_Security")
					+ "  where FSecurityCode="
					+ dbl.sqlString(scc.getSecurityCodeBefore())
					+ " and FCheckState=1   and FStorageDate=" + dbl.sqlDate(YssFun.addDay(this.dDate, -1)) 
					//add by songjie 2012.01.03 BUG 3503 QDV4赢时胜（深圳_Roy）2011年12月23日01_B 查询库存时，需根据当前组合查询
					+ " and FPortCode = " + dbl.sqlString(this.sPortCode);
			rs = dbl.queryByPreparedStatement(sql);
			while (rs.next()) {
				//add by zhangfa 20100908 MS01706    证券代码相同所属分类不同，会引起业务处理报错    QDV4赢时胜(测试)2010年09月07日01_B    
				 sNewNum = "E" + YssFun.formatDate(this.dDate, "yyyyMMdd") +
	             dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Data_Integrated"),
	                                    dbl.sqlRight("FNUM", 6),
	                                    "000001",
	                                    " where FExchangeDate=" + dbl.sqlDate(this.dDate) +
	                                    " or FExchangeDate=" + dbl.sqlDate("9998-12-31") +
	                                    " or FNum like 'E" + YssFun.formatDate(this.dDate, "yyyyMMdd") + "%'");
				 //------------------------------------------------------------------------------------------------------------
				//edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
				sSubNum = integrateAdmin.getKeyNum();
				pstB.setString(1, sNewNum);
				pstB.setString(2, sSubNum);
				pstB.setInt(3, -1);
				pstB.setString(4, scc.getSecurityCodeBefore());
				pstB.setDate(5, YssFun.toSqlDate(this.dDate)); // 兑换日期是this中的
				pstB.setDate(6, YssFun.toSqlDate(this.dDate));
				pstB.setString(7, "87");
				pstB.setString(8, " "); // 这里的 sRelaNum,sNumType都为' '
				pstB.setString(9, " ");
				pstB.setString(10, this.sPortCode);
				pstB.setString(11, rs.getString("FAnalysisCode1"));
				pstB.setString(12, rs.getString("FAnalysisCode2"));
				pstB.setString(13, rs.getString("FAnalysisCode3"));
				pstB.setDouble(14, -rs.getDouble("FStorageAmount"));
				pstB.setDouble(15, -rs.getDouble("FStorageCost"));
				pstB.setDouble(16, -rs.getDouble("FMStorageCost"));
				pstB.setDouble(17, -rs.getDouble("FVStorageCost"));
				pstB.setDouble(18, -rs.getDouble("FBaseCuryCost"));
				pstB.setDouble(19, -rs.getDouble("FMBaseCuryCost"));
				pstB.setDouble(20, -rs.getDouble("FVBaseCuryCost"));
				pstB.setDouble(21, -rs.getDouble("FPortCuryCost"));
				pstB.setDouble(22, -rs.getDouble("FMPortCuryCost"));
				pstB.setDouble(23, -rs.getDouble("FVPortCuryCost"));
				pstB.setDouble(24, rs.getDouble("FBaseCuryRate"));
				pstB.setDouble(25, rs.getDouble("FPortCuryRate"));
				pstB.setString(26, " ");
				pstB.setString(27, " ");
				pstB.setInt(28, 1);
				pstB.setString(29, pub.getUserCode());
				pstB.setString(30, YssFun.formatDatetime(new Date()));
				pstB.setString(31, "05");
				pstB.setString(32, "05"+getCatCode(scc.getSecurityCodeBefore()));
				pstB.setString(33, rs.getString("FAttrClsCode"));
				pstB.setString(34, rs.getString("FInvestType"));
				pstB.setString(35, "0");
				
				//edit by songjie 2012.12.20 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
				sSubNum = integrateAdmin.getKeyNum();
				pstA.setString(1, sNewNum);
				pstA.setString(2, sSubNum);
				pstA.setInt(3, 1);
				pstA.setString(4, scc.getSecurityCodeAfter());
				pstA.setDate(5, YssFun.toSqlDate(this.dDate)); // 兑换日期是this中的
				pstA.setDate(6, YssFun.toSqlDate(this.dDate));
				pstA.setString(7, "87");
				pstA.setString(8, " "); // 这里的 sRelaNum,sNumType都为' '
				pstA.setString(9, " ");
				pstA.setString(10, this.sPortCode);
				pstA.setString(11, rs.getString("FAnalysisCode1"));
				pstA.setString(12, rs.getString("FAnalysisCode2"));
				pstA.setString(13, rs.getString("FAnalysisCode3"));
				pstA.setDouble(14, rs.getDouble("FStorageAmount"));
				pstA.setDouble(15, rs.getDouble("FStorageCost"));
				pstA.setDouble(16, rs.getDouble("FMStorageCost"));
				pstA.setDouble(17, rs.getDouble("FVStorageCost"));
				pstA.setDouble(18, rs.getDouble("FBaseCuryCost"));
				pstA.setDouble(19, rs.getDouble("FMBaseCuryCost"));
				pstA.setDouble(20, rs.getDouble("FVBaseCuryCost"));
				pstA.setDouble(21, rs.getDouble("FPortCuryCost"));
				pstA.setDouble(22, rs.getDouble("FMPortCuryCost"));
				pstA.setDouble(23, rs.getDouble("FVPortCuryCost"));
				pstA.setDouble(24, rs.getDouble("FBaseCuryRate"));
				pstA.setDouble(25, rs.getDouble("FPortCuryRate"));
				pstA.setString(26, " ");
				pstA.setString(27, " ");
				pstA.setInt(28, 1);
				pstA.setString(29, pub.getUserCode());
				pstA.setString(30, YssFun.formatDatetime(new Date()));
				pstA.setString(31, "05");
				pstA.setString(32, "05"+getCatCode(scc.getSecurityCodeAfter()));
				pstA.setString(33, rs.getString("FAttrClsCode"));
				pstA.setString(34, rs.getString("FInvestType"));
				pstA.setString(35, "0");
				
				pstB.executeUpdate();
				pstA.executeUpdate();
				  conn.commit();
				   conn.setAutoCommit(true);
	               bTrans = false;
			} 
		}catch (Exception e) {
			throw new YssException("处理证券代码变更业务时出错！", e);
		} finally {
			dbl.closeStatementFinal(pstB);
			dbl.closeStatementFinal(pstA);
			dbl.closeResultSetFinal(rs);
			dbl.endTransFinal(conn, bTrans);
		}
		
	}
	//获取综合业务编号
	public String getFum(Date date,String sTradeTypeCode,String securitycode,String portcode,int iInOutType,String sNumType) throws YssException{
		String sNum="";
		ResultSet rs = null;
        String strSql = "";
        try{
        	strSql="select FNum from  "+ pub.yssGetTableName("Tb_Data_Integrated")+
        	       "  where FOperDate="+dbl.sqlDate(date)+" and FTradeTypeCode="+dbl.sqlString(sTradeTypeCode)+
        	       "  and FSecurityCode="+dbl.sqlString(securitycode)+" and FPortCode="+dbl.sqlString(portcode)+
        	       "  and FInOutType="+iInOutType+" and FNumType="+dbl.sqlString(sNumType);
        	rs=dbl.queryByPreparedStatement(strSql);
        	while(rs.next()){
        		sNum=rs.getString("FNum");
        	}
        } catch (Exception e) {
            throw new YssException("获取综合业务交易主编号出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return sNum;
		
	}
	//获取关联编号
	public String getRelaNum(String num) throws YssException{
		String relanums="";
		ResultSet rs = null;
        String strSql = "";
        try{
        	strSql="select FRelaNum from "+ pub.yssGetTableName("Tb_Data_Integrated")+
        	       " where FNum="+dbl.sqlString(num);
        	rs=dbl.queryByPreparedStatement(strSql);
        	while(rs.next()){
        		relanums=rs.getString("FRelaNum")+",";
        	}
        } catch (Exception e) {
            throw new YssException("获取综合业务关联编号出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return relanums;
		
	}

	/**
	 * add by guolongchao 20120224 获取品种类型
	 * @return
	 * @throws YssException
	 */
	public String getCatCode(String securitycode) throws YssException{
		String relCatCode="";
		ResultSet rs = null;
        String strSql = "";
        try{
        	strSql = " select FCatCode from "+ pub.yssGetTableName("Tb_Para_Security") +
    			     " where FSecurityCode ="+dbl.sqlString(securitycode);
        	rs=dbl.queryByPreparedStatement(strSql);
        	if(rs.next()){
        		relCatCode=rs.getString("FCatCode");
        	}
        } catch (Exception e) {
            throw new YssException("获取获取品种类型出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
		return relCatCode;		
	}
}
