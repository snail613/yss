/**@author shashijie ,2011-12-26 上午10:39:08 STORY 1713 */ 
package com.yss.main.parasetting.FixInterest; 

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.util.Iterator;

import com.sun.xml.rpc.processor.modeler.j2ee.xml.exceptionMappingType;
import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.pojo.param.bond.YssBondIns;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/** 
 * @author 作者 : shashijie
 * @version 创建时间：2011-12-26 上午10:39:08 
 * STORY 1713
 * 类说明 
 */
/**@author shashijie ,2011-12-26 上午10:39:08 STORY 债券付息期间设置 */
public class InterestTime extends BaseDataSettingBean implements IDataSetting {
	
	private String FSecurityCode = "";//证券代码
	private String FSecurityName = "";//证券名称
	private Date FInsStartDate = null;//起始日期
	private Date FInsEndDate = null;//截至日期
	private Date FIssueDate = null;//派发日期(派息到帐日)
	private Date FRecordDate = null;//派息登记日
	private Date FExRightDate = null;//派息除权日
	private double FFaceRate = 0.0;//税后票面利率
	private double FMoneyAutomatic = 0.0;//每百元实际利息(自动)
	private double FMoneyControl = 0.0;//每百元实际利息(手动)
	
	private String OldFSecurityCode = "";//旧证券代码
	private String fixInterest = "";//债券基本信息
	private InterestTime filterType;//自身对象

	private Date dSettleDate = null;		//20130325 added by liubo.Story #3528.到帐日
	private double dPayMoney = 0.0;			//20130325 added by liubo.Story #3528.偿还本金
	private double dRemainMoney = 0.0;		//20130325 added by liubo.Story #3528.剩余本金
	private double dBeforeFaceRate = 0.0;	//20130325 added by liubo.Story #3528.税前票面利率
	private double dID = 0;				//20130325 added by liubo.Story #3528.付息序号
	private double dOldID = 0;

    private String sRecycled = "";

	public double getOldID() {
		return dOldID;
	}

	public void setOldID(double dOldID) {
		this.dOldID = dOldID;
	}

	public double getID() {
		return dID;
	}

	public void setID(double sID) {
		this.dID = sID;
	}

	public Date getSettleDate() {
		return dSettleDate;
	}

	public void setSettleDate(Date dSettleDate) {
		this.dSettleDate = dSettleDate;
	}

	public double getPayMoney() {
		return dPayMoney;
	}

	public void setPayMoney(double dPayMoney) {
		this.dPayMoney = dPayMoney;
	}

	public double getRemainMoney() {
		return dRemainMoney;
	}

	public void setRemainMoney(double dRemainMoney) {
		this.dRemainMoney = dRemainMoney;
	}

	public double getBeforeFaceRate() {
		return dBeforeFaceRate;
	}

	public void setBeforeFaceRate(double dBeforeFaceRate) {
		this.dBeforeFaceRate = dBeforeFaceRate;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fSecurityCode的值
	 */
	public String getFSecurityCode() {
		return FSecurityCode;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fSecurityCode为fSecurityCode的值
	 */
	public void setFSecurityCode(String fSecurityCode) {
		FSecurityCode = fSecurityCode;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fSecurityName的值
	 */
	public String getFSecurityName() {
		return FSecurityName;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fSecurityName为fSecurityName的值
	 */
	public void setFSecurityName(String fSecurityName) {
		FSecurityName = fSecurityName;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fInsStartDate的值
	 */
	public Date getFInsStartDate() {
		return FInsStartDate;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fInsStartDate为fInsStartDate的值
	 */
	public void setFInsStartDate(Date fInsStartDate) {
		FInsStartDate = fInsStartDate;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fInsEndDate的值
	 */
	public Date getFInsEndDate() {
		return FInsEndDate;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fInsEndDate为fInsEndDate的值
	 */
	public void setFInsEndDate(Date fInsEndDate) {
		FInsEndDate = fInsEndDate;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fIssueDate的值
	 */
	public Date getFIssueDate() {
		return FIssueDate;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fIssueDate为fIssueDate的值
	 */
	public void setFIssueDate(Date fIssueDate) {
		FIssueDate = fIssueDate;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fFaceRate的值
	 */
	public double getFFaceRate() {
		return FFaceRate;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fFaceRate为fFaceRate的值
	 */
	public void setFFaceRate(double fFaceRate) {
		FFaceRate = fFaceRate;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fMoneyAutomatic的值
	 */
	public double getFMoneyAutomatic() {
		return FMoneyAutomatic;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fMoneyAutomatic为fMoneyAutomatic的值
	 */
	public void setFMoneyAutomatic(double fMoneyAutomatic) {
		FMoneyAutomatic = fMoneyAutomatic;
	}

	/**shashijie 2011-12-26 
	 * @return 获取fMoneyControl的值
	 */
	public double getFMoneyControl() {
		return FMoneyControl;
	}

	/**shashijie 2011-12-26 
	 * @param 设置fMoneyControl为fMoneyControl的值
	 */
	public void setFMoneyControl(double fMoneyControl) {
		FMoneyControl = fMoneyControl;
	}

	/**shashijie 2011-12-26 
	 * @return 获取oldFSecurityCode的值
	 */
	public String getOldFSecurityCode() {
		return OldFSecurityCode;
	}

	/**shashijie 2011-12-26 
	 * @param 设置oldFSecurityCode为oldFSecurityCode的值
	 */
	public void setOldFSecurityCode(String oldFSecurityCode) {
		OldFSecurityCode = oldFSecurityCode;
	}

	/**shashijie 2011-12-26 
	 * @return 获取filterType的值
	 */
	public InterestTime getFilterType() {
		return filterType;
	}

	/**shashijie 2011-12-26 
	 * @param 设置filterType为filterType的值
	 */
	public void setFilterType(InterestTime filterType) {
		this.filterType = filterType;
	}

	/** shashijie 2011-12-26 STORY  */
	public String addSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	//先检查是否已有存在记录
        	if (isDriftRateInsert()) {
        		throw new YssException("需要增加债券信息浮动利率的数据已存在!");
			}
        	
        	this.dID = getCurId();
        	
            strSql = "insert into " +
                pub.yssGetTableName("Tb_Para_InterestTime") +
                "( FSecurityCode,FInsStartDate,FInsEndDate,FIssueDate,FRecordDate,FExRightDate," +
                " FFaceRate,FMoneyAutomatic,FMoneyControl" +
                " ,FID,Fsettledate,FBeforeFaceRate,Fpaymoney,Fremainmoney,FCHECKSTATE,FCREATOR,FCREATETIME" + 
                " ) values ( " + 
                dbl.sqlString(this.FSecurityCode) + "," +
                dbl.sqlDate(this.FInsStartDate) + "," +
                dbl.sqlDate(FInsEndDate) + "," +
        		dbl.sqlDate(FIssueDate) + "," +
        		dbl.sqlDate(FRecordDate)+ ","+
        		dbl.sqlDate(FExRightDate)+ ","+
                BigDecimal.valueOf(FFaceRate) + "," +
                BigDecimal.valueOf(FMoneyAutomatic) + "," +
        		BigDecimal.valueOf(FMoneyControl) + "," +
        		this.dID + "," +
        		dbl.sqlDate(this.dSettleDate) + "," +
        		this.dBeforeFaceRate + "," +
        		this.dPayMoney + "," +
//        		this.dRemainMoney +
        		calcRemainMoney(this.dID) + "," +
                (pub.getSysCheckState() ? "0" : "1") + "," +
                dbl.sqlString(pub.getUserCode()) + "," +
                dbl.sqlString(YssFun.formatDate(new java.util.Date())) +
                " ) ";
            
            conn.setAutoCommit(false);
            bTrans = true;
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        }catch (Exception e) {
            throw new YssException("增加债券期间浮动利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

        return null;
	}
	
	private double getCurId() throws YssException
	{
		double dReturn = 0;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = " select max(FID) as cnt from " + pub.yssGetTableName("tb_para_interesttime") +
					 " where FSecurityCode = " + dbl.sqlString(this.FSecurityCode);
			rs = dbl.queryByPreparedStatement(strSql);
			
			if(rs.next())
			{
				if (rs.getString("cnt") == null)
				{
					dReturn = 0;
				}
				else
				{
					dReturn = YssD.add(rs.getDouble("cnt"), 1);
				}
			}
			else
			{
				dReturn = 0;
			}
		}
		catch(Exception ye)
		{
			throw new YssException("生成债券计息期间ID出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return dReturn;
	}
	
	private double calcRemainMoney(double dCurID) throws YssException
	{
		double dReturn = 0.0;
		String strSql = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from " +
					 pub.yssGetTableName("tb_para_fixinterest") + " a " +
					 " left join (select FID,Fremainmoney,fsecuritycode as FSec from " + pub.yssGetTableName("tb_para_interesttime") + " where  " +
					 " fsecuritycode = " + dbl.sqlString(this.FSecurityCode) + 
					 " and FID in (select max(FID) from " + pub.yssGetTableName("tb_para_interesttime") + 
					 " where fsecuritycode = " + dbl.sqlString(this.FSecurityCode) + " and FID < " + this.dID + ")) b " +
					 " on a.fsecuritycode = b.FSec " +
					 " where a.fsecuritycode = " + dbl.sqlString(this.FSecurityCode);
			rs = dbl.queryByPreparedStatement(strSql);
			
			while(rs.next())
			{
				if (rs.getString("FID") == null)
				{
					dReturn = YssD.sub(rs.getDouble("FFaceValue"), this.dPayMoney);
				}
				else
				{
					dReturn = YssD.sub(rs.getDouble("FRemainMoney"), this.dPayMoney);
				}
			}
			
		}
		catch(Exception ye)
		{
			throw new YssException("计算剩余本金出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
		
		return dReturn;
	}

	/** shashijie 2011-12-26 STORY  */
	public void checkInput(byte btOper) throws YssException {
		/*dbFun.checkInputCommon(btOper,
                pub.yssGetTableName("Tb_Para_InterestTime"),
                "FSecurityCode,FInsStartDate",
                this.FSecurityCode + "," + YssFun.formatDate(this.OldFSecurityCode),
                this.OldFSecurityCode + "," + YssFun.formatDate(this.OldFStartDate));*/
	}

	
	/** shashijie 2011-12-26 STORY //审核不需要  */
	public void checkSetting() throws YssException 
	{

        String strSql = "";
        String[] arrData = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            if (sRecycled != null && !sRecycled.equalsIgnoreCase("")) {
                arrData = sRecycled.split("\r\n");
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    strSql = "update " + pub.yssGetTableName("tb_para_InterestTime") +
                        " set FCheckState = " +
                        this.checkStateId +
                        ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                        ", FCheckTime = '" +
                        YssFun.formatDatetime(new java.util.Date()) +
                        "'" +
                        " where FSECURITYCODE = " +dbl.sqlString(this.FSecurityCode) + 
                        " and FID = " + this.dID;
                    dbl.executeSql(strSql);
                }
            }
            //如果sRecycled为空，而feelinkCode不为空，则按照feelinkCode来执行sql语句
            //edit by songjie 2012.07.18 STORY #2475 QDV4赢时胜(上海开发部)2012年4月6日03_A
            else if (FSecurityCode != null && !FSecurityCode.equalsIgnoreCase("")) {
                strSql = "update " + pub.yssGetTableName("tb_para_InterestTime") +
                " set FCheckState = " +
                this.checkStateId +
                ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
                ", FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) +
                "'" +
                " where FSECURITYCODE = " +dbl.sqlString(this.FSecurityCode) + 
                " and FID = " + this.dID;
                dbl.executeSql(strSql);
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核/反审核债券计息期间设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
	}

	/** shashijie 2011-12-26 STORY 删除数据，即放入回收站 */
	public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
        	strSql = "update " + pub.yssGetTableName("tb_para_InterestTime") +
            " set FCheckState = " +
            this.checkStateId +
            ", FCheckUser = " + dbl.sqlString(pub.getUserCode()) +
            ", FCheckTime = '" +
            YssFun.formatDatetime(new java.util.Date()) +
            "'" +
            " where FSECURITYCODE = " +dbl.sqlString(this.FSecurityCode) + 
            " and FID = " + this.dID;

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("刪除债券计息期间设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

	}
	
	/** shashijie 2011-12-26 STORY  */
	public void deleteRecycleData() throws YssException {
		boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
		ResultSet rs = null;
        String[] arrData = null;
        String strSql = "";
		
		try {
	        conn.setAutoCommit(false);
            bTrans = true;
            
            if (sRecycled != null && !sRecycled.equals("")) {
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
                strSql = "delete from " + pub.yssGetTableName("Tb_Para_InterestTime") + " where FSecurityCode = "+
                	dbl.sqlString(this.FSecurityCode) + 
                	" and FID = " + this.dID + " and FCHECKSTATE = 2";
                
                //this.FInsStartDate==null ? " And FInsStartDate = " + dbl.sqlDate(this.FInsStartDate) : "";
                
                dbl.executeSql(strSql);
                }
            }
            
//            if (!this.FSecurityCode.trim().equals("null") && this.FSecurityCode.trim().length() > 0) {
//                strSql = "delete from " + pub.yssGetTableName("Tb_Para_InterestTime") + " where FSecurityCode = "+
//                	dbl.sqlString(this.FSecurityCode) + 
//                	" and FID = " + this.dID + " and FCHECKSTATE = 2";
//                
//                //this.FInsStartDate==null ? " And FInsStartDate = " + dbl.sqlDate(this.FInsStartDate) : "";
//                
//                dbl.executeSql(strSql);
	        
	        conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
            
		} catch (Exception e) {
			dbl.closeResultSetFinal(rs);
			throw new YssException("删除债券期间浮动利率出错", e);
		} finally {
			dbl.endTransFinal(conn, bTrans);
		}
	}

	/** shashijie 2011-12-26 STORY 1713 */
	public String editSetting() throws YssException {
		String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            strSql = "update " + pub.yssGetTableName("Tb_Para_InterestTime") +
                " set " +
                " FInsStartDate = " + dbl.sqlDate(this.FInsStartDate) + " ," +
                " FInsEndDate = " + dbl.sqlDate(this.FInsEndDate) + " ," +
                " FIssueDate = "+dbl.sqlDate(this.FIssueDate)+","+
                " FRecordDate = "+dbl.sqlDate(FRecordDate)+","+
                " FExRightDate = "+dbl.sqlDate(FExRightDate)+","+
                " FFaceRate = "+this.FFaceRate+","+
                " FMoneyAutomatic = "+this.FMoneyAutomatic+","+
                " FMoneyControl = "+this.FMoneyControl+", "+
                " Fsettledate = "+ dbl.sqlDate(this.dSettleDate) +", "+
                " FBeforeFaceRate = "+this.dBeforeFaceRate+", "+
                " Fpaymoney = "+this.dPayMoney+", "+
                " Fremainmoney = "+ calcRemainMoney(this.dID) +
                
                " where FSecurityCode = " + dbl.sqlString(this.OldFSecurityCode) + " and FID = " + this.dID + " and FCHECKSTATE = 0"; 
                //" And FStartDate = " + dbl.sqlDate(this.OldFInsStartDate);
            conn.setAutoCommit(false);
            bTrans = true;
            
            dbl.executeSql(strSql);
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("修改债券期间浮动利率出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
	}
	
	/** shashijie 2011-12-26 STORY  */
	public String getAllSetting() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public IDataSetting getSetting() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String saveMutliSetting(String sMutilRowStr) throws YssException {
		String[] strAry = null;
        try {
            strAry = sMutilRowStr.split("\f\f");
            for (int i = 0; i < strAry.length; i++) {
                this.parseRowStr(strAry[i]);
                
                if (i==0) {//第一次执行
                	//先删除已有记录
                    this.deleteRecycleData();
				}
                
                //添加记录
                this.addSetting();
            }
        } catch (Exception e) {
            throw new YssException("批量保存债券期间浮动利率出错", e);
        }
        return "";
	}
	
	/**
	 * add by songjie 2013.03.27
	 * STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
	 * 批量保存债券期间浮动利率出错
	 * @param alIt
	 * @throws YssException
	 */
	public void saveMutliInfo(ArrayList alIt) throws YssException {
		InterestTime it = null;
		String securityCode ="";
		double id = 0;
		String delSql = "";
		String strSql = "";
		Iterator iter = null;
		PreparedStatement pst = null;
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();
        try {
            conn.setAutoCommit(false);
            bTrans = true;
        	
        	delSql = "delete from " + pub.yssGetTableName("Tb_Para_Interesttime")+
    		" where ";
        	iter = alIt.iterator();
        	while(iter.hasNext()){
        		it = (InterestTime)iter.next();
        		securityCode = it.getFSecurityCode();
        		id = it.getID();
        		delSql += " (FSecurityCode = " + dbl.sqlString(securityCode) + 
        		" and FId = " + id + ") or";
        	}
        	if(delSql.endsWith(" or")){
        		delSql = delSql.substring(0, delSql.length() - 3);
        	}
        	dbl.executeSql(delSql);
        	
            strSql = "insert into " +
            pub.yssGetTableName("Tb_Para_InterestTime") +
            "( FSecurityCode,FInsStartDate,FInsEndDate,FIssueDate,FRecordDate,FExRightDate," +
            " FFaceRate,FMoneyAutomatic,FMoneyControl" +
            " ,FID,Fsettledate,FBeforeFaceRate,Fpaymoney,Fremainmoney " + 
            " ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
            pst = dbl.getPreparedStatement(strSql);
            
        	iter = alIt.iterator();
        	while(iter.hasNext()){
        		it = (InterestTime)iter.next();
        		
        		pst.setString(1, it.getFSecurityCode());
        		pst.setDate(2, YssFun.toSqlDate(it.getFInsStartDate()));
        		pst.setDate(3, YssFun.toSqlDate(it.getFInsEndDate()));
        		pst.setDate(4, YssFun.toSqlDate(it.getFIssueDate()));
        		pst.setDate(5, YssFun.toSqlDate(it.getFRecordDate()));
        		pst.setDate(6, YssFun.toSqlDate(it.getFExRightDate()));
        		pst.setDouble(7, it.getFFaceRate());
        		pst.setDouble(8, 0);
        		pst.setDouble(9, 0);
        		pst.setDouble(10, it.getID());
        		pst.setDate(11, YssFun.toSqlDate(it.getSettleDate()));
        		pst.setDouble(12, it.getBeforeFaceRate());
        		pst.setDouble(13, it.getPayMoney());
        		pst.setDouble(14, it.getRemainMoney());
        		
        		pst.addBatch();
        	}
        	
            pst.executeBatch();
            
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("批量保存债券期间浮动利率出错", e);
        } finally{
        	dbl.closeStatementFinal(pst);
        	dbl.endTransFinal(conn, bTrans);
        }
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getBeforeEditData() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
        buf.append(this.FSecurityCode).append("\t");
        buf.append(this.FSecurityName).append("\t");
        buf.append(this.dID).append("\t");	//20130325 added by liubo.Story #3528.付息序号
        buf.append(YssFun.formatDate(this.FInsStartDate)).append("\t");
        buf.append(YssFun.formatDate(this.FInsEndDate)).append("\t");
        buf.append(YssFun.formatDate(this.FIssueDate)).append("\t");
        buf.append(YssFun.formatDate(this.FRecordDate)).append("\t");
        buf.append(YssFun.formatDate(this.FExRightDate)).append("\t");
        buf.append(YssFun.formatDate(this.dSettleDate)).append("\t");	//20130325 added by liubo.Story #3528.到账日
        buf.append(this.dBeforeFaceRate).append("\t");	//20130325 added by liubo.Story #3528.税前票面利率
        buf.append(this.FFaceRate).append("\t");
        buf.append(this.FMoneyAutomatic).append("\t");
        buf.append(this.FMoneyControl).append("\t");
        buf.append(this.dPayMoney).append("\t");		//20130325 added by liubo.Story #3528.偿还本金
        buf.append(this.dRemainMoney).append("\t");		//20130325 added by liubo.Story #3528.剩余本金
        buf.append(this.OldFSecurityCode).append("\t");	
        buf.append(super.buildRecLog());
        
        return buf.toString();
	}
	
	/** shashijie 2012-01-19 STORY 1713 */
	public String getOperValue(String sType) throws YssException {
		String reStr = "";
		if ("getMoneyAutomatic".equals(sType)) {
			reStr = getMoneyAutomatic();//获取百元派息金额
		}else if ("getIssueDate".equals(sType)) {
			reStr = getIssueDate();//获取派息日(后一个工作日)
		}else if ("getRecordDate".equals(sType)) {
			reStr = getRecordDate();//获取派息登记日(前一个工作日)
		}
		return reStr;
	}
	
	/**shashijie 2012-1-30 STORY 1713*/
	private String getIssueDate() throws YssException {
		//获取节假日代码
		String dayCode = getDayCode();
		//获取工作日
		Date FBargainDate = getWorkDayByWhere(dayCode, this.FInsEndDate, 1);
		return YssFun.formatDate(FBargainDate);
	}
	
	/**shashijie 2012-04-18 STORY 1713*/
	private String getRecordDate() throws YssException {
		//获取节假日代码
		String dayCode = getDayCode();
		//获取工作日
		Date FBargainDate = getWorkDayByWhere(dayCode, this.FIssueDate, -1);
		return YssFun.formatDate(FBargainDate);
	}
	
	/**shashijie 2012-1-30 STORY 1713 获取节假日代码 */
	private String getDayCode() throws YssException {
		String dayCode = "";
		ResultSet rs = null;
		try {
			String strSql = " Select FHolidaysCode From "+pub.yssGetTableName("Tb_Para_Security")+
				" Where FSecurityCode = "+dbl.sqlString(this.FSecurityCode);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				dayCode = rs.getString("FHolidaysCode");
			}
		} catch (Exception e) {
			throw new YssException("获取证券对应节假日代码出错!");
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return dayCode;
	}

	/**获取工作日
	 * @param sHolidayCode 节假日代码
	 * @param dDate 日期
	 * @param dayInt 当天的偏离天数
	 * @author shashijie ,2012-01-30 , STORY 1713
	 */
	private Date getWorkDayByWhere(String sHolidayCode, Date dDate, int dayInt) throws YssException {
		Date mDate = null;//工作日
		//公共获取工作日类
		BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);
        mDate = operDeal.getWorkDay(sHolidayCode, dDate, dayInt);
        return mDate;
	}
	
	/**shashijie 2012-1-19 STORY 1713 获取百元派息金额*/
	private String getMoneyAutomatic() throws YssException {
		double reVal = 0;//百元派息金额
		
		BaseOperDeal operDeal = new BaseOperDeal();
        operDeal.setYssPub(pub);

        YssBondIns bondIns = new YssBondIns();
        setbondIns(bondIns);
        
        //获取利息算法设置对应的BeanId类
		BaseBondOper bondOper = operDeal.getSpringBeanId(FSecurityCode," a.FTaskMoneyCode = b.FCIMCode ");
		if (bondOper == null) {
			throw new YssException("请设置【基础参数模块的Spring调用】，引用BeanId：BondInsCfgFormula");
		}
		bondOper.setFromDomestic(true);//设置从国内接口导入调用的标志
        bondOper.setYssPub(pub);
        bondOper.flage = false;//不实用优先级方式取值
        bondOper.init2(bondIns,this.FInsStartDate,this.FInsEndDate,this.FIssueDate,this.FFaceRate,
        		this.fixInterest);

        //百元派息金额
        reVal = bondOper.calBondInterest();//计算税后百元派息金额
        
		return String.valueOf(reVal);
	}

	/**shashijie 2012-1-19 STORY 1713*/
	private void setbondIns(YssBondIns bondIns) {
		bondIns.setInsType("FTaskMoneyCode");//计提类型
        bondIns.setSecurityCode(this.FSecurityCode);
        /*bondIns.setInsDate(this.FInsStartDate);//计息日期
        bondIns.setPortCode(portCode);
        bondIns.setHolidaysCode(FHolidaysCode);//节假日群代码
        bondIns.setAttrClsCode(sAttrClsCode);//属性分类
        bondIns.setFactor(dFactor);//报价因子
        bondIns.setIsBeforeRate(false);//设置计算税后利息的标志
        bondIns.setInsAmount(1);//设置成交数量为1
        bondIns.setIsRate100(false);*///表示获取每百元债券利息的公式
	}

	/** shashijie 2011-12-26 STORY  */
	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
        String sTmpStr = "";
        try {
            if (sRowStr.trim().length() == 0) {
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
            //==================end===================
            if (sRowStr.indexOf("\r\t") >= 0) {
                sTmpStr = sRowStr.split("\r\t")[0];
                
                String[] moder = sRowStr.split("\r\t");
                if (moder.length >= 3) {
                    this.fixInterest = moder[2];//基本信息
                }
            } else {
                sTmpStr = sRowStr;
            }

            sRecycled = sRowStr;
            reqAry = sTmpStr.split("\t");
            this.FSecurityCode = reqAry[0];
            this.FSecurityName = reqAry[1];
            this.dID = YssFun.isNumeric(reqAry[2]) ? Double.valueOf(reqAry[2]) : 0;		//20130325 added by liubo.Story #3528.付息序号
            this.FInsStartDate = YssFun.toDate(reqAry[3]);
            this.FInsEndDate = YssFun.toDate(reqAry[4]);
            this.FIssueDate = YssFun.toDate(reqAry[5]);
            this.FRecordDate = YssFun.toDate(reqAry[6]);
            this.FExRightDate = YssFun.toDate(reqAry[7]);
            this.dSettleDate = YssFun.toDate(reqAry[8]);	//20130325 added by liubo.Story #3528.到账日
            this.dBeforeFaceRate = YssFun.isNumeric(reqAry[9]) ? Double.valueOf(reqAry[9]) : 0;		//20130325 added by liubo.Story #3528.税前票面利率
            this.FFaceRate = YssFun.isNumeric(reqAry[10]) ? Double.valueOf(reqAry[10]) : 0;
            this.FMoneyAutomatic = YssFun.isNumeric(reqAry[11]) ? Double.valueOf(reqAry[11]) : 0;
            this.FMoneyControl = YssFun.isNumeric(reqAry[12]) ? Double.valueOf(reqAry[12]) : 0;

            this.dPayMoney = YssFun.isNumeric(reqAry[13]) ? Double.valueOf(reqAry[13]) : 0;		//20130325 added by liubo.Story #3528.偿还本金
            this.dRemainMoney = YssFun.isNumeric(reqAry[14]) ? Double.valueOf(reqAry[14]) : 0;	//20130325 added by liubo.Story #3528.剩余本金

            this.checkStateId = Integer.parseInt(reqAry[15]);
            
            this.OldFSecurityCode = reqAry[16];
            
            if (sRowStr.indexOf("\r\t") >= 0) {
                if (this.filterType == null) {
                    this.filterType = new InterestTime();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) {
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]);
                }
            }
            
        } catch (Exception e) {
            throw new YssException("解析债券期间浮动利率出错", e);
        }
	}

	
	/** shashijie 2011-12-26 STORY 1713 */
	public String getListViewData1() throws YssException {
        return getListViewData3();
	}

	/**获取债券浮息期间
	 * @param strSql
	 * @author shashijie ,2011-12-23 , STORY 1713 
	 */
	private String builderListViewData(String strSql) throws YssException {
		String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = this.getListView1Headers();
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(super.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);

                setSecurityAttr(rs);
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
            rs.close();
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols();
        } catch (Exception e) {
            throw new YssException("获取债券浮息期间出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
	}
	
	/**设置属性值
	 * @param rs 
	 * @author shashijie ,2011-12-26 , STORY 1713 
	 */
	private void setSecurityAttr(ResultSet rs) throws Exception {
		this.FSecurityCode = rs.getString("FSecurityCode");
        this.FSecurityName = rs.getString("FSecurityName").trim().length() > 0 ? rs.getString("FSecurityName") : " " ;
        this.FInsStartDate = rs.getDate("FInsStartDate");
        this.FInsEndDate = rs.getDate("FInsEndDate");
        this.FIssueDate = rs.getDate("FIssueDate");
        this.FRecordDate = rs.getDate("FRecordDate");
        this.FExRightDate = rs.getDate("FExRightDate");
        this.FFaceRate = rs.getDouble("FFaceRate");
        this.FMoneyAutomatic = rs.getDouble("FMoneyAutomatic");
        this.FMoneyControl = rs.getDouble("FMoneyControl");
        this.dID = rs.getDouble("FID");
        this.dSettleDate = rs.getDate("Fsettledate");
        this.dBeforeFaceRate = rs.getDouble("FBeforeFaceRate");
        this.dPayMoney = rs.getDouble("Fpaymoney");
        this.dRemainMoney = rs.getDouble("Fremainmoney");
        super.setRecLog(rs);
        
	}
	
	/**拼接条件
	 * @return 
	 * @author shashijie ,2011-12-23 , STORY 1713 
	 */
	public String buildFilterSql() throws YssException{
		String sResult = "";
		try
		{
	        if (this.filterType != null) {
	            sResult += " where 1=1";
	            if (this.filterType.FSecurityCode.length() != 0) {
	                sResult += " and a.FSecurityCode like '" +
	                    this.filterType.FSecurityCode.replaceAll("'", "''") + "%'";
	            }
	            if (this.filterType.FInsStartDate != null && !this.filterType.FInsStartDate.equals(YssFun.toDate("9998-12-31")) && !this.filterType.FInsStartDate.equals(YssFun.toDate("1900-01-01"))) {
	                sResult += " and a.FInsStartDate = " + dbl.sqlDate(this.FInsStartDate);
	            }
	            if (this.filterType.FInsEndDate != null && !this.filterType.FInsEndDate.equals(YssFun.toDate("9998-12-31")) && !this.filterType.FInsEndDate.equals(YssFun.toDate("1900-01-01"))) {
	            	sResult += " and a.FInsEndDate = " + dbl.sqlDate(this.FInsEndDate);
				}
	            if (this.filterType.FIssueDate != null && !this.filterType.FIssueDate.equals(YssFun.toDate("9998-12-31")) && !this.filterType.FIssueDate.equals(YssFun.toDate("1900-01-01"))) {
	            	sResult += " and a.FIssueDate = " + dbl.sqlDate(this.FIssueDate);
				}
	            if (this.filterType.dSettleDate != null && !this.filterType.dSettleDate.equals(YssFun.toDate("9998-12-31")) && !this.filterType.dSettleDate.equals(YssFun.toDate("1900-01-01"))) {
	            	sResult += " and a.FSETTLEDATE = " + dbl.sqlDate(this.dSettleDate);
				}
	            if (this.filterType.FExRightDate != null && !this.filterType.FExRightDate.equals(YssFun.toDate("9998-12-31")) && !this.filterType.FExRightDate.equals(YssFun.toDate("1900-01-01"))) {
	            	sResult += " and a.FExRightDate = " + dbl.sqlDate(this.FExRightDate);
				}
	            if (this.filterType.FFaceRate != 0) {
	                sResult += " and a.FFaceRate = " +
	                    this.filterType.FFaceRate;
	            }
	            if (this.filterType.FMoneyAutomatic != 0) {
	                sResult += " and a.FMoneyAutomatic = " +
	                    this.filterType.FMoneyAutomatic;
	            }
	            if (this.filterType.FMoneyControl != 0) {
	                sResult += " and a.FMoneyControl = " +
	                    this.filterType.FMoneyControl;
	            }
	            
	        }
		}
		catch(Exception ye)
		{
			throw new YssException();
		}
        return sResult;
	}
	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewData2() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewData3() throws YssException {
		String strSql = "select a.* , " +
            " c.FSecurityName as FSecurityName,b.FUserName as FCreatorName,c.FUserName as FCheckUserName " +
            " from " + pub.yssGetTableName("Tb_Para_InterestTime") + " a" +
            " left join ( select FSecurityCode,FSecurityName from " + 
            pub.yssGetTableName("Tb_Para_Security") + ") c" +
            " on a.FSecurityCode = c.FSecurityCode " +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
            " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
            buildFilterSql() +
            " order by a.FID";
		return builderListViewData(strSql);
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewData4() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewGroupData1() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewGroupData2() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewGroupData3() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewGroupData4() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getListViewGroupData5() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getTreeViewData1() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getTreeViewData2() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getTreeViewData3() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getTreeViewGroupData1() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getTreeViewGroupData2() throws YssException {
		
		return null;
	}

	
	/** shashijie 2011-12-26 STORY  */
	public String getTreeViewGroupData3() throws YssException {
		
		return null;
	}
	
	/**先检查是否已有存在记录
	 * @author shashijie ,2011-12-23 , STORY 1713
	 */
	private boolean isDriftRateInsert() throws YssException {
		ResultSet rs = null;
		
		try {
			String strSql = " select * from " + pub.yssGetTableName("Tb_Para_InterestTime") + 
	        	" where FSecurityCode = "+dbl.sqlString(this.FSecurityCode)+
	        	" And FInsStartDate = " + dbl.sqlDate(this.FInsStartDate);
	        rs = dbl.openResultSet(strSql);
            
	        if (rs.next()) {
	            return true;
	        } else {
				return false;
			}
            
		} catch (Exception e) {
			throw new YssException("先检查是否已有存在记录出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * add by songjie 2013.03.25 
	 * STORY #3528 需求上海-[YSS_SH]QDIIV4.0[中]20130131001
	 * 判断债券计息期间设置表中是否存在相关证券的数据
	 * @param securityCode
	 * @return
	 * @throws YssException
	 */
	public ArrayList existFixInfo(String securityCodes,java.util.Date dDate) throws YssException{
		boolean exist = false;
		ResultSet rs = null;
		String strSql = "";
		ArrayList alExist = new ArrayList();
		try{
			strSql = " select FSecurityCode from " + pub.yssGetTableName("Tb_Para_InterestTime") + 
			" where FSecurityCode in (" + operSql.sqlCodes(securityCodes) + ") and " + 
			dbl.sqlDate(dDate) + " between FInsStartDate and FInsEndDate ";
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				alExist.add(rs.getString("FSecurityCode"));
			}
			return alExist;
		}catch(Exception e){
			throw new YssException("获取债券计息期间设置数据出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}

	public String getFixInterest() {
		return fixInterest;
	}

	public void setFixInterest(String fixInterest) {
		this.fixInterest = fixInterest;
	}

	public Date getFRecordDate() {
		return FRecordDate;
	}

	public void setFRecordDate(Date fRecordDate) {
		FRecordDate = fRecordDate;
	}

	public Date getFExRightDate() {
		return FExRightDate;
	}

	public void setFExRightDate(Date fExRightDate) {
		FExRightDate = fExRightDate;
	}
	
	
	
}

