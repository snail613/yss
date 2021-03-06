package com.yss.vsub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.yss.dsub.BaseBean;
import com.yss.main.parasetting.PortfolioBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class YssFinance
    extends BaseBean {
    public YssFinance() {
    }

    /**
     * 通过套帐号获取组合代码 sj add 20080422
     * @param sSetCode String
     * @return String
     * @throws YssException
     */
    public String getPortCode(String sSetCode) throws YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        ResultSet portRs = null;
        try {
            if (!dbl.yssTableExist("Lsetlist")) {
                return "";
            }
            if (sSetCode.trim().length() > 0) {
                strSql = "Select FSetId from Lsetlist where FSetCode = " +
                    String.valueOf(sSetCode);
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    strSql = "select FPortCode,FAssetCode from " + pub.yssGetTableName("Tb_Para_Portfolio") +
                        " where FAssetCode = " + dbl.sqlString(rs.getString("FSetId")) + " and FCheckState = 1";
                    portRs = dbl.openResultSet(strSql);
                    if (portRs.next()) {
                        sResult = portRs.getString("FPortCode");
                    }
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(portRs);
        }

    }

    /**
     * 通过组合代码获取套帐名称  liubo add 20110516.Story 850 
     * @param sSetCode String
     * @return String
     * @throws YssException
     */
    public String getBookSetName(String sPortCode) throws YssException {
        String sResult = "";
        String strSql = "";
        //YssPub rspub = new YssPub();
        ResultSet portRs = null;
        //YssDbOperSql opersql = new YssDbOperSql(pub);//无用注释
        //DbBase dbBookSet = new DbBase();//无用注释
        //this.setYssPub(pub);
        //CommonRepBean commonRepBean = new CommonRepBean();
        //commonRepBean.setYssPub(pub);
        try {
            //if (!dbBookSet.yssTableExist("Lsetlist")) {
                //return "";
            //}
        	/**add---shashijie 2013-5-28 BUG 7967 QDV4赢时胜(上海)2013年05月22日04_B*/
        	 strSql = getFSetNameSQL(sPortCode);
			/**end---shashijie 2013-5-28 BUG 7967 增值功能管理-报表批量导出界面导出报表时
												    系统报“工作薄至少含有一张可视工作表”的错误*/
			
            portRs = dbl.openResultSet(strSql);
            if (portRs.next()) {
                sResult = portRs.getString("FSETNAME");//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
        	dbl.closeResultSetFinal(portRs);
        }
    }
    
	/**shashijie 2013-5-28 BUG 7967 根据组合获取套帐名称SQL */
	private String getFSetNameSQL(String sPortCode) {
		YssDbOperSql opersql = new YssDbOperSql(pub);
		String sql = " select l.FSETNAME from lsetlist l join " +
	        pub.yssGetTableName("Tb_Para_Portfolio") + " p on l.fsetid = p.fassetcode WHERE p.FPORTCODE IN (" + 
	        opersql.sqlCodes(sPortCode) + 
	       ") and FCHECKSTATE = 1 order by FCHECKTIME";
		return sql;
	}
	

	/**
     * 通过组合代码获取套帐
     * add by huangqirong 2012-05-24 bug 4542
     * @param sSetCode String
     * @return String
     * @throws YssException
     */
    public String getBookSetId(String sPortCode) throws YssException {
        String sResult = "";
        String strSql = "";
        ResultSet portRs = null;
        //DbBase dbBookSet = new DbBase();//无用注释
        try {        			
        	//20121127 modified by liubo.56SP3海富通测试问题：跨组合群导出报表报XX年无XX套账的财务数据
        	//在跨组合群的情况下，在固定的组合群中通过获取组合套账链接设置来获取套账代码，在跨组合群的情况下，无法获取其他组合群的组合套账链接设置
        	//在此采用通过资产代码查询财务系统套账表的形式来获取
        	//=================================
//                    strSql = " select distinct to_Number(FbookSetCode) as FbookSetCode from " +
//                    	pub.yssGetTableName("Tb_Vch_PortSetLink")+" WHERE FPORTCODE IN ( "+dbl.sqlString(sPortCode)+
//                    	" ) and FCHECKSTATE = 1 ";
        	strSql = " select distinct FSetCode " +
			 " from lsetlist " +
			 " where fsetid in (select FAssetCode " +
			 " from " + pub.yssGetTableName("tb_para_portfolio") + 
			 " where FPORTCODE IN ( "+dbl.sqlString(sPortCode) + "))";
                    portRs = dbl.openResultSet(strSql);
                    if (portRs.next()) {
//                        sResult = portRs.getString("FbookSetCode");
                    	sResult = portRs.getString("FSetCode");
                    }
             //================end=================
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
        	dbl.closeResultSetFinal(portRs);
        }

    }
    
    /**
     * 通过组合代码获取套帐
     * add by yeshenghong 2013-03-15  story3715
     * @param sSetCode String
     * @return String
     * @throws YssException
     */
    public String getBookSetId(String sGroupCode,String sPortCode) throws YssException {
        String sResult = "";
        String strSql = "";
        ResultSet portRs = null;
        //DbBase dbBookSet = new DbBase();//无用注释
        try {        			
        	//20121127 modified by liubo.56SP3海富通测试问题：跨组合群导出报表报XX年无XX套账的财务数据
        	//在跨组合群的情况下，在固定的组合群中通过获取组合套账链接设置来获取套账代码，在跨组合群的情况下，无法获取其他组合群的组合套账链接设置
        	//在此采用通过资产代码查询财务系统套账表的形式来获取
        	//=================================
//                    strSql = " select distinct to_Number(FbookSetCode) as FbookSetCode from " +
//                    	pub.yssGetTableName("Tb_Vch_PortSetLink")+" WHERE FPORTCODE IN ( "+dbl.sqlString(sPortCode)+
//                    	" ) and FCHECKSTATE = 1 ";
        	strSql = " select distinct FSetCode " +
			 " from lsetlist " +
			 " where fsetid in (select FAssetCode " +
			 " from tb_" + sGroupCode + "_para_portfolio" + 
			 " where FPORTCODE IN ( "+dbl.sqlString(sPortCode) + "))";
                    portRs = dbl.openResultSet(strSql);
                    if (portRs.next()) {
//                        sResult = portRs.getString("FbookSetCode");
                    	sResult = portRs.getString("FSetCode");
                    }
             //================end=================
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
        	dbl.closeResultSetFinal(portRs);
        }

    }

    public String getCWSetCode(String sPortCode) throws YssException {
        String sResult = "";
        String strSql = "";
        PortfolioBean port = new PortfolioBean();
        ResultSet rs = null;
        try {
            port.setYssPub(pub);
            if (!dbl.yssTableExist("Lsetlist")) {
                return "";
            }
            if (sPortCode.trim().length() > 0) {
                port.setPortCode(sPortCode);
                port.getSetting();
                strSql = "Select FSetCode from Lsetlist where FSetID = " +
                    dbl.sqlString(port.getAssetCode());
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    sResult = YssFun.formatNumber(rs.getInt("FSetCode"), "000");
                }
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getCWTabName(java.util.Date dDate, int iSetCode,
                               String sTabInd) throws YssException {
        String sResult = "";
        String sSetCode = "";
        sSetCode = YssFun.formatNumber(iSetCode, "000");
        sResult = getCWTabName(dDate, sSetCode, sTabInd);
        return sResult;
    }

    public String getCWTabName(String sSetCode, String sTabInd) throws
        YssException {
        String sResult = "";
        sResult = getCWTabName(null, sSetCode, sTabInd);
        return sResult;
    }

    public String getCWTabName(java.util.Date dDate, String sSetCode,
                               String sTabInd) throws YssException {
        String sResult = "";
        if (dDate != null) {
            sResult = "A" + YssFun.formatDate(dDate, "yyyy");
            if (sSetCode.trim().length() > 0) {
                sResult = "A" + YssFun.formatDate(dDate, "yyyy") + sSetCode;
            }
        }
        sResult = sResult + sTabInd;
        return sResult;
    }

    //取财务的表名，根据组合设置中的资产代码和财务中的套帐表资产代码关联
    public String getCWTabName(String sPortCode, java.util.Date dDate,
                               String sTabInd) throws YssException {
        String sResult = "";
        String strSql = "";
        String sSet = "", sYear = "";
        PortfolioBean port = new PortfolioBean();
        ResultSet rs = null;
        try {
            //不存在对应的估值表就直接返回
            port.setYssPub(pub);
            if (!dbl.yssTableExist("Lsetlist")) {
                return "";
            }
            if (sPortCode.trim().length() > 0) {
                port.setPortCode(sPortCode);
                port.getSetting();
                strSql = "Select FSetCode from Lsetlist where FSetID = " +
                    dbl.sqlString(port.getAssetCode());
                rs = dbl.openResultSet(strSql);
                if (rs.next()) {
                    sSet = YssFun.formatNumber(rs.getInt("FSetCode"), "000");
                    if (dDate != null) {
                        sYear = YssFun.formatDate(dDate, "yyyy");
                    }
                    sResult = "A" + sYear + sSet + sTabInd;
                } else {
                    throw new YssException("未找到对应的资产代码【" + port.getAssetCode() + "】");
                }
            } else if (dDate != null) {
                sYear = YssFun.formatDate(dDate, "yyyy");
                sResult = "A" + sYear + sTabInd;
            } else {
                sResult = sTabInd;
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    public String getCWAccountCury(String sAccCode, java.util.Date dDate,
                                   String sPortCode) throws YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        String sTabName = "";
        try {
            sTabName = this.getCWTabName(sPortCode, dDate, "LACCOUNT");
            strSql = "select * from " + sTabName +
                " where FAcctCode = " + dbl.sqlString(sAccCode);
            rs = dbl.openResultSet(strSql);
            if (rs.next()) {
                sResult = rs.getString("FCurCode");
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 余额表需要的临时登帐公用方法
     * @param date int   登帐日期
     * @param portCode int   登帐的组合
     * @throws YssException
     * @throws SQLException
     * add by yeshenghong  20121120
     */
    public void PostAccB(java.util.Date date,String portCode){
       try
       {
	       String SqlStr = "";
	       String pcName  = pub.getUserCode();
	       SimpleDateFormat formate= new SimpleDateFormat("MM"); ; 
	       int lMonth = Integer.parseInt(formate.format(date));
	       String tblPrefix = this.getCWTabName(portCode, date, "");

           if (!dbl.yssTableExist("Tmp_lbalance")) { //临时表
               String createStr = " create table Tmp_lbalance " +
            	   	" (FADDR       VARCHAR2(30) not null, " +
            	    " FMONTH      NUMBER(3) not null, " +
            	    " FACCTCODE   VARCHAR2(50) not null, " +
            	    " FCURCODE    VARCHAR2(3) not null, " +
            	    " FSTARTBAL   NUMBER(19,4) default 0 not null, " + 
            	    " FDDEBIT      NUMBER(19,4) default 0 not null, " + /***注意：原币借方日发生额 新加***/
            	    " FDCREDIT     NUMBER(19,4) default 0 not null, " + /***原币贷方日发生额 新加***/
            	    " FMACCDEBIT   NUMBER(19,4) default 0 not null, " + /**注意：原币月发生额  与余额表的FDEBIT字段对应***/
            	    " FMACCCREDIT  NUMBER(19,4) default 0 not null, " + /**原币借方月发生额  与余额表的 FCREDIT字段对应***/
            	    " FYACCDEBIT  NUMBER(19,4) default 0 not null, " +  /**原币借方年发生额 与余额表的FACCDEBIT字段对应***/
            	    " FYACCCREDIT  NUMBER(19,4) default 0 not null, " + /**原币贷方年发生额 与余额表的FACCCREDIT字段对应***/
            	    " FENDBAL     NUMBER(19,4) default 0 not null, " +
            	    " FBSTARTBAL  NUMBER(19,4) default 0 not null, " +
            	    " FBDDEBIT     NUMBER(19,4) default 0 not null, " + /**本位币  日发生额  新加***/
            	    " FBDCREDIT    NUMBER(19,4) default 0 not null, " + /**本位币 日发生额  新加***/
            	    " FBMACCDEBIT  NUMBER(19,4) default 0 not null, " + /**本位币 月发生额  余额表FBDEBIT字段 ***/
            	    " FBMACCCREDIT NUMBER(19,4) default 0 not null, " + /**本位币  贷方月发生额  余额表FBCREDIT字段*/
            	    " FBYACCDEBIT  NUMBER(19,4) default 0 not null, " + /**本位币 年发生额 余额表FBACCDEBIT字段***/
            	    " FBYACCCREDIT  NUMBER(19,4) default 0 not null, " + /**本位币 年发生额 余额表FBACCCREDIT字段***/
            	    " FBENDBAL    NUMBER(19,4) default 0 not null, " +
            	    " FASTARTBAL  NUMBER(19,4) default 0 not null, " +
            	    " FADDEBIT     NUMBER(19,4) default 0 not null, " + /**数量  借日发生额  新加***/
            	    " FADCREDIT    NUMBER(19,4) default 0 not null, " + /**数量  贷日发生额  新加***/
            	    " FAMACCDEBIT  NUMBER(19,4) default 0 not null, " + /**数量 月发生额  余额表FADEBIT字段 ***/
            	    " FAMACCCREDIT NUMBER(19,4) default 0 not null, " + /**数量  贷方月发生额  余额表FACREDIT字段*/
            	    " FAYACCDEBIT  NUMBER(19,4) default 0 not null, " + /**数量  年发生额 余额表FAACCDEBIT字段***/
            	    " FAYACCCREDIT  NUMBER(19,4) default 0 not null, " + /**数量 年发生额 余额表FAACCCREDIT字段***/
            	    " FAENDBAL    NUMBER(19,4) default 0 not null, " +
            	    " FISDETAIL   NUMBER(3) not null, " +
            	    " FAUXIACC    VARCHAR2(100) not null	)";
                   dbl.executeSql(createStr);
           }
	       SqlStr = "Delete from Tmp_lbalance where FAddr= '" + pcName + "'";
	       dbl.executeSql(SqlStr);
	       //从余额表中导入年初数及上月数据到临时余额表中
	
	       SqlStr = " insert into Tmp_lbalance select '" + pcName + "', " +
	                " FMONTH, FACCTCODE,FCURCODE, FSTARTBAL, 0,0,FDEBIT, FCREDIT, FACCDEBIT, FACCCREDIT, FENDBAL, " +
	       			" FBSTARTBAL, 0,0,FBDEBIT, FBCREDIT, FBACCDEBIT, FBACCCREDIT, FBENDBAL, FASTARTBAL, 0,0,FADEBIT, " +
	       			" FACREDIT, FAACCDEBIT, FAACCCREDIT,FAENDBAL, FISDETAIL, FAUXIACC  from " + tblPrefix +
	       			"lbalance a where fmonth=0" +
	             ( (lMonth > 1) ? " or fmonth=" + (lMonth - 1) : "");
	       //System.out.println("SqlStr======="+SqlStr);
	       dbl.executeSql(SqlStr);
	
	       //'登记本月凭证到临时余额表中
	
	       PostVch(lMonth, date,tblPrefix);
	       
	       //删除期初数 因为期初数已经加在科目中
	       SqlStr = " delete from Tmp_lbalance  where faddr = '" + pcName + "' and (fmonth=0" +
  			((lMonth > 1) ? " or fmonth= " + (lMonth - 1) + ")" : ")");
	       dbl.executeSql(SqlStr);
	
       }catch(Exception e)
       {
    	   	try {
        	  throw new YssException("临时登账错误！\r\n");
			} catch (YssException e1) {
				e1.printStackTrace();
			}
       }
    }
    
    private void PostVch(int lMonth, java.util.Date date,String tblPrefix) throws YssException,SQLException {
    	// ADD BY YESHENGHONG STORY3241 临时登帐公用方法
		String sSql = "";
		String pcName = pub.getUserCode();
		String sTmp = "Tmp_lbalance";
		String sBal = "'" + pcName + "',";; // '如果是余额表，需要的多余字段
		ResultSet rs = null;
		boolean bTrans = false;
		Connection conn = dbl.loadConnection();

		try {
			conn.setAutoCommit(false);
			bTrans = true;
			// 明细科目凭证登帐(为了oracle，改成case when....)
			sSql = "insert into "
					+ sTmp
					+ " select "
					+ sBal
					+ lMonth
					+ ","
					+ " NVL(NVL(a.fkmh, b.facctcode), e.fkmh), "
					+ " NVL(NVL(a.fcyid, b.fcurcode), e.fcyid), "
					+ " NVL(b.fendbal, 0), "
					+ " nvl(e.fbalJ, 0), "
					+ " nvl(e.fbalD, 0), "
					+ " NVL(a.fjje, 0), "
					+ " NVL(a.fdje, 0), "
					+ " NVL(a.fjje, 0) + NVL(b.faccdebit, 0), "
					+ " NVL(a.fdje, 0) + NVL(b.facccredit, 0), "
					+ " NVL(b.fendbal, 0) + NVL(a.fjje, 0) - NVL(a.fdje, 0), "
					+ " NVL(b.fbendbal, 0), "
					+ " nvl(e.FBBalJ, 0) , "
					+ " nvl(e.FBBalD, 0), "
					+ " NVL(a.fbjje, 0), "
					+ " NVL(a.fbdje, 0), "
					+ " NVL(a.fbjje, 0) + NVL(b.fbaccdebit, 0), "
					+ " NVL(a.fbdje, 0) + NVL(b.fbacccredit, 0), "
					+ " NVL(b.fbendbal, 0) + NVL(a.fbjje, 0) - NVL(a.fbdje, 0), "
					+ " NVL(b.faendbal, 0), "
					+ " nvl(e.fslJ, 0), "
					+ " nvl(e.FslD, 0), "
					+ " NVL(a.fjsl, 0), "
					+ " NVL(a.fdsl, 0), "
					+ " NVL(a.fjsl, 0) + NVL(b.faaccdebit, 0), "
					+ " NVL(a.fdsl, 0) + NVL(b.faacccredit, 0), "
					+ " NVL(b.faendbal, 0) + NVL(a.fjsl, 0) - NVL(a.fdsl, 0), "
					+ " 1, "
					+ " ' ' "
					+ "from (select fkmh,fcyid, sum(case when fjd='J' then fbal else 0 end) as fjje,"
					+ "sum(case when fjd='D' then fbal else 0 end) as fdje,sum(case when fjd='J' then fsl else 0 end) as fjsl,"
					+ "sum(case when fjd='D' then fsl else 0 end) as fdsl,"
					+ "sum(case when fjd='J' then fbbal else 0 end) as fbjje,"
					+ "sum(case when fjd='D' then fbbal else 0 end) as fbdje "
					+ "from "
					+ tblPrefix 
					+ "fcwvch where fterm="
					+ lMonth
					+ " and (fconfirmer <> ' ' or fconfirmer is null) "
					+ // add by yeshenghong 20120410 story2425
					((YssFun.dateDiff(date,getAccountingPeriodEndDate(
									YssFun.toInt(tblPrefix .substring(tblPrefix.length() - 3)),
									YssFun.toInt(tblPrefix .substring(tblPrefix.length() - 7,tblPrefix.length() - 3)),
									lMonth))) > 0 ? " and fdate" + "<="
							+ dbl.sqlDate(date) : "")
					+ " group by fkmh,fcyid) a ";

			// '考虑余额表临时登帐，上月数据固定从余额表获取
			sSql = sSql
					+ "full join (" 
//					+ " select c.facctcode,fmonth,c.fcurcode,faccdebit,facccredit,"
//					+ "fendbal,fbaccdebit,fbacccredit,fbendbal,faaccdebit,faacccredit,faendbal,c.fauxiacc from "
//					+ "(select * from "
//					+ tblPrefix 
//					+ "LBalance where fmonth="
//					+ (lMonth - 1)
					
					+ " select c.* from (select facctcode,fmonth, fcurcode, "
					+ " sum(faccdebit) as faccdebit, sum(facccredit) as facccredit, "
					+ " sum(fendbal) as fendbal, sum(fbaccdebit) as fbaccdebit, "
					+ " sum(fbacccredit) as fbacccredit, sum(fbendbal) as fbendbal, "
					+ " sum(faaccdebit) as faaccdebit, sum(faacccredit) as faacccredit, "
					+ " sum(faendbal) as faendbal "
					+ " from " + tblPrefix  + "LBalance where fmonth = "
					+ (lMonth - 1)
					+ " group by facctcode,fmonth,fcurcode "
					+ " ) c join (select facctcode,facctdetail from "
					+ tblPrefix 
					+ "laccount where facctdetail=1) d on c.facctcode=d.facctcode)";

			sSql = sSql
					+ " b on a.fkmh=b.facctcode and a.fcyid=b.fcurcode ";
			//计算本日余额
			sSql = sSql + "  full join  ( select case " +
                " when FkmhD is null then FkmhJ else FkmhD end Fkmh, " +
                " case  when FCyIdD is null then FCyIdJ else FCyIdD end FCyId,  'J' fjdJ, " +
                " case  when fbalJ is null then 0 else  fbalJ end fbalJ, " +
                " case  when FslJ is null then  0 else FslJ end FslJ, " +
                " case  when FBBalJ is null then 0 else FBBalJ end FBBalJ, " +
                " case  when FBslJ is null then 0  else  FBslJ end FBslJ, 'D' fjdD, " +
                " case  when fbalD is null then 0 else  fbalD end fbalD, " +
                " case  when FslD is null then 0 else  FslD end FslD, " +
                " case  when FBBalD is null then  0 else FBBalD end FBBalD, " +
                " case  when FBslD is null then  0 else FBslD end FBslD " +
                " from (select p.*, q.*  from (select Fkmh FkmhJ, FCyId FCyIdJ, " +
                " fjd fjdJ, sum(FBal) fbalJ,  sum(fbsl) FslJ,  sum(FBBal) FBBalJ, " +
                " sum(Fbsl) FBslJ from " + tblPrefix + "fcwvch where fdate = " +
                dbl.sqlDate(date) +
                " and fjd = 'J' group by Fkmh, FCyId, Fjd order by Fkmh) p  " +
                " full join (select Fkmh FkmhD, FCyId FCyIdD,fjd fjdD,  sum(FBal) fbalD, " +
                " sum(fbsl) FslD,  sum(FBBal) FBBalD, sum(Fbsl) FBslD from " + tblPrefix + "fcwvch where fdate = " +
                dbl.sqlDate(date) + " and fjd = 'D'  group by Fkmh, FCyId, Fjd " +
                " order by Fkmh) q on p.FkmhJ = q.FkmhD and p.FCyIdJ = q.FCyIdD)) e on a.fkmh = e.fkmh ";
			
			dbl.executeSql(sSql);

			int kmLength = 0;
			String strSql = "";
			SimpleDateFormat formate = new SimpleDateFormat("yyyy");
			int year = Integer.parseInt(formate.format(date));
			int setcode = YssFun.toInt(tblPrefix .substring(tblPrefix.length() - 3,tblPrefix.length()));
			for (int i = 1; i <= accLenLevel(-1,year,setcode) - 1; i++) {
				kmLength = accLenLevel(i,year,setcode);
				strSql = "insert into "
						+ sTmp
						+ " select "
						+ sBal
						+ lMonth
						+ ",a.facctcode"
						+ ",b.fcurcode,sum(b.fstartbal),sum(b.fddebit),sum(b.fdcredit),"
						+ "sum(b.fmaccdebit),sum(b.fmacccredit),sum(b.fyaccdebit),sum(b.fyacccredit),sum(b.fendbal),sum(b.fbstartbal),sum(b.fbddebit),"
						+ "sum(b.fbdcredit),sum(b.fbmaccdebit),sum(b.fbmacccredit),sum(b.fbyaccdebit),sum(b.fbyacccredit),sum(b.fbendbal),sum(b.fastartbal),"
						+ "sum(b.faddebit),sum(b.fadcredit),sum(b.famaccdebit),sum(b.famacccredit),sum(b.fayaccdebit),sum(b.fayacccredit),sum(b.faendbal),0 ,' ' from "
						+ tblPrefix
						+ "laccount a join "
						+ sTmp
						+ " b on a.facctcode ="
						+ dbl.sqlLeft("b.facctcode", kmLength)
						+ " where b.fmonth="
						+ lMonth
						+ " and "
						+ dbl.sqlLen("a.facctcode")
						+ "="
						+ kmLength
						+ " and a.facctdetail=0  "
						+ " and FAddr='" + pcName + "'" 
						+ " group by a.facctcode,b.fcurcode order by a.facctcode";

				dbl.executeSql(strSql);
			}

			// date=new java.util.Date();
			// System.out.println("start commit date==="+date);
			conn.commit();
			bTrans = false;
			if (rs != null) {
				rs.getStatement().close();
			}
			// System.out.println(" end commmit==="+date);
			// stmt.close();
		} catch (SQLException es) {
			throw new YssException("Error:\r\n", es);
		} finally {
			try {
				if (bTrans) {
					conn.rollback();
				}
			} catch (Exception e) {
			}
			dbl.closeResultSetFinal(rs);
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
				throw new YssException(e.getMessage());
			}
		}
    }
    
    protected AccountingPeriod yearPeriod[]; //年度所有会计期间列表
    class AccountingPeriod { //（内部类）
        public java.util.Date startPeriodDate;
        public java.util.Date endPeriodDate;
     }
    
    /*
     *通过会计期间查找当前期间的截止日期
     *setCode 表示套账号
     *setYear 表示会计年份
     *period表示会计期间
     *返回截止日期
     */
    private java.util.Date getAccountingPeriodEndDate(int setCode, int setYear, int period) throws YssException {
       //此方法的设计思想为如果setCode等于当前套账，setYear等于当前会计年度则直接从yearPeriod中提取相关信息返回，否则从1.1.1AccountingPeriod表中提取
       //  从yearPeriod中取数的方法为：
       loadYearAccountingPeriod(setCode,setYear);
       java.util.Date endPeriodDate = new java.util.Date();
       if(period<1 || period>yearPeriod.length-1){
          throw new YssException("会计期间错误！");
       }
       if (yearPeriod[period] != null) {
          endPeriodDate = yearPeriod[period].endPeriodDate;
       }
       else {
          //从AccountingPeriod表中取数
          String SQL = "";
          ResultSet rs = null;
          try {
             SQL = "Select * from AccountingPeriod where fsetcode= " + setCode + " and fyear=" + setYear + " and fterm=" + period;
             rs = dbl.openResultSet(SQL);
             if (rs.next()) {
                endPeriodDate = rs.getDate("FendDate");
             }
          }
          catch (SQLException sqle) {
             throw new YssException("获取第" + setCode + "套账" + setYear + "年度" + period + "会计期间截止日期出错！", sqle);
          }
          catch(YssException ysse){
             throw ysse;
          }
          catch (Exception e) {
             throw new YssException("获取第" + setCode + "套账" + setYear + "年度" + period + "会计期间截止日期出错！", e);
          }
          finally {
             try {
                if (rs != null) {
                   rs.getStatement().close();
                }
             }
             catch (Exception e) {e.printStackTrace();}
          }
       }
       return endPeriodDate;
    }

    /*
     *将指setCode套账setYear年份的所有会计期信息加载到公用变量备用
     *setCode 为套账号
     *setYear 为指定年份
     */
    private void loadYearAccountingPeriod(int setCode, int setYear) throws YssException {
       //通过SLQ语句从AccountingPeriod中取出set套账，date年份的所有会计期间信息，按会计期间从小到大排序，然后将对应的值赋给strArry。日期的格式为”yyyy-MM-dd”
       ResultSet rs = null;
       String strSql = "";
       try {
          if(setCode>0){
             strSql = "Select * from AccountingPeriod where fsetcode=" + setCode + " and fyear=" + setYear;
             rs = dbl.openResultSet(strSql,ResultSet.TYPE_SCROLL_INSENSITIVE);
             rs.last();
             yearPeriod = new AccountingPeriod[rs.getInt("Fterm") + 1];
             rs.beforeFirst();
             while (rs.next()) {
                yearPeriod[rs.getInt("Fterm")] = new AccountingPeriod();
                yearPeriod[rs.getInt("Fterm")].startPeriodDate = rs.getDate("FstartDate");
                yearPeriod[rs.getInt("Fterm")].endPeriodDate = rs.getDate("FendDate");
             }
          }
       }
       catch (SQLException sqle) {
          throw new YssException("加载第" + setCode + "套账" + setYear + "年份的所有会计期信息出错！", sqle);
       }
       catch (Exception e) {
          throw new YssException("加载第" + setCode + "套账" + setYear + "年份的所有会计期信息出错！", e);
       }
       finally {
          try {
             if (rs != null) {
                rs.getStatement().close();
             }
          }
          catch (Exception e) {e.printStackTrace();}
       }
    }
    
    /** 返回level级科目的总长度
     * 如果partial=true的话只返回本级长度
     *  level<=0，则返回科目级数;
     * @throws YssException 
     * @throws SQLException 
     */
    private int accLenLevel(int level,int currentYear,int currentSet) throws SQLException, YssException {
       int ltmp;
       int total = 0;
       String accLen = "";
       ResultSet rstmp = dbl.openResultSet("select * from lsetlist where fyear=" +
               currentYear + " and fsetcode=" + currentSet);
	   if (rstmp.next()) {
		   accLen = rstmp.getString("facclen");
	   }
	   dbl.closeResultSetFinal(rstmp);
       ltmp = accLen.length();
       if (level <= 0)
          return ltmp; //返回科目级数

       if (level > ltmp)
          level = ltmp;
       
       for (; level > 0; level--) {
          total += Integer.parseInt(accLen.substring(level - 1, level));
       }
       return total;
    }
    
    /**
     * 通过套帐号获取组合相关的信息 
     * add by huangqirong 2013-04-25 bug #7486
     * 此方法用于套帐和组合相关方便使用，因此放在这个类里面
     * @param sSetCode String
     * @return String
     * @throws YssException
     */
    public String getPortCodeAbout(String portCode , String field) throws YssException {
        String sResult = "";
        String strSql = "";
        ResultSet portRs = null;
        try {           
            strSql = "select p2.* from (select FPortCode,max(FStartDate) as FStartDate from " + 
            			pub.yssGetTableName("Tb_Para_Portfolio") +" where FPortCode = " + dbl.sqlString(portCode) + 
            			" and FCheckState = 1 group by FPortCode ) p1 " +
            			" left join " + pub.yssGetTableName("Tb_Para_Portfolio") +
            			" p2 on p1.FPortCode = p2.fportcode and p1.FStartDate = p2.fstartdate";
            portRs = dbl.openResultSet(strSql);
            if (portRs.next()) {
                sResult = portRs.getString(field);
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(portRs);
        }
    }
    
}
