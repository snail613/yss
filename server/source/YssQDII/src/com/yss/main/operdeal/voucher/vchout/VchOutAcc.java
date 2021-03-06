package com.yss.main.operdeal.voucher.vchout;

import java.sql.*;

import com.yss.log.SingleLogOper;
import com.yss.util.*;
import com.yss.vsub.*;

/**
 *
 * <p>Title: VchOutAcc</p>
 * <p>Description: 导出到财务系统</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author sj
 * @version 1.0
 */
public class VchOutAcc
    extends BaseVchOut {
    public VchOutAcc() {
    }

    public String doInsert() throws YssException {
        ResultSet vchDataRs = null;
        ResultSet vchDataEntityRs = null;
        String strSql = "";
        //StringBuffer vchNumBuf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String vchNums = "";
        PreparedStatement pstm = null;
        //String strDate = "";
        //String[] arrDate = null;
        int vchNum = 1;
        int enId = 1;
        String sTmp = "";
        YssFinance cw = null;
        String[] aryPorts = null;
        String reStr = "";
        StringBuffer queryBuffer = new StringBuffer();
        
		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		java.util.Date logStartTime = new java.util.Date();
		if(logOper == null){//添加非空判断
			logOper = SingleLogOper.getInstance();
		}
		String logInfo = "";
		String portCode = "";//组合代码
		//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            aryPorts = this.portCodes.split(",");
            //runStatus.appendRunDesc("VchRun","  开始向财务系统导入凭证... ...\r\n");
            for (int ports = 0; ports < aryPorts.length; ports++) {
            	//add by songjie 2012.10.16 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
            	portCode = aryPorts[ports];//获取组合代码
            	
                cw = new YssFinance();
                cw.setYssPub(pub);
                Connection conn = dbl.loadConnection();
                
                //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                logInfo += "    开始导入组合【" + aryPorts[ports] + "】的凭证... ...\r\n";
                
                runStatus.appendRunDesc("VchRun", "    开始导入组合【" + aryPorts[ports] + "】的凭证... ...");
                delete(aryPorts[ports]);
                strSql = " select a.FVchNum,a.FVchDate,b.FVchInd,b.FVchTWay,a.FPortCode,trim(to_char(FBookSetCode,'000'))as FBookSetCode from " + pub.yssGetTableName("Tb_Vch_Data") +
                    " a left join (select b1.*,b2.FVchInd from " +
                    pub.yssGetTableName("Tb_Vch_VchTpl") +
                    " b1 left join (select FAttrCode,FVchInd from " +
                    pub.yssGetTableName("Tb_Vch_Attr") +
                    " where FCheckState = 1) b2 on b1.FAttrCode = b2.FAttrCode" +
                    " where FCheckState = 1) b on a.FVchTplCode = b.FVchTplCode" +
                    " where a.FVchTplCode in (" + //operSql.sqlCodes(vchTplCodes) +
                    (isInData ? operSql.sqlCodes(getVchTpl()) : operSql.sqlCodes(this.vchTypes)) +
                    ") and a.FCheckState = 1" +
                    " and a.FVchDate between " + dbl.sqlDate(beginDate) + " and " +
                    dbl.sqlDate(endDate) +
                    //                 " and FPortCode = " + dbl.sqlString(aryPorts[ports]) + //增加组合条件 sj
                    " and a.FPortCode = " + dbl.sqlString(aryPorts[ports]) + //by liyu 因为凭证模板中也有这个字段
//                    " and a.FBookSetCode=" +dbl.sqlString(getBookSet(aryPorts[ports]))+
                    " and trim(to_char(a.FBookSetCode, '000')) =" +dbl.sqlString(getBookSet(aryPorts[ports]))+ //BUG5567 modified by yeshenghong 20120913
                    " order by a.fvchnum asc ";
//                    " and exists(select FBookSetCode from " +
//                    pub.yssGetTableName("tb_vch_bookset") +
//                    " where FBookSetCode= a.FBookSetCode) order by a.fvchnum asc "; //modified by yeshenghong 20120203 BUG 3655 取值顺序不稳定 导致财务系统凭证号紊乱 
                vchDataRs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
                while (vchDataRs.next()) {
                    enId = 1;
//                    strSql =
//                        " select a.* ,b.* from " +
//                        " (select * from " +
//                        pub.yssGetTableName("tb_vch_dataentity") +
//                        ")a left join" +
//                        " (select m.*,n.FAttrCode from " +
//                        " (select * from " + pub.yssGetTableName("tb_vch_data") +
//                        ")m left join" +
//                        " (select FVchTplCode,FAttrCode from " +
//                        pub.yssGetTableName("tb_vch_vchtpl") + ")" +
//                        "  n on m.FVchTplCode=n.FVchTplCode " +
//                        "  )b on b.FVchNum=a.FVchNum " +
//                        " where a.FVchNum = " +
//                        dbl.sqlString(vchDataRs.getString("FVchNum")) +
//                        " order by a.fdcway"; //先借后贷fazmm20071007
                    
                    //【STORY #2387 财务系统，手工修改自动凭证，凭证审核后，如重新生成凭证，被审核的凭证会被覆盖】
                    // add by jsc 20120413添加字段FVCHNUMRELA 用于记录估值系统的凭证编号 
                    //这里String字符串拼接改成StringBuffer,做调优，避免服务器报内存溢出的问题
                    queryBuffer.append(" select a.* ,b.* from ");
                    queryBuffer.append(" (select * from ").append(pub.yssGetTableName("tb_vch_dataentity")).append(" )a ");
                    queryBuffer.append(" left join ");
                    queryBuffer.append(" (select m.*,n.FAttrCode from ");
                    queryBuffer.append(" (select * from ").append(pub.yssGetTableName("tb_vch_data")).append(" )m ");
                    queryBuffer.append(" left join ");
                    queryBuffer.append(" (select FVchTplCode,FAttrCode from ").append(pub.yssGetTableName("tb_vch_vchtpl")).append(")n ");
                    queryBuffer.append(" on m.FVchTplCode=n.FVchTplCode  )b ");
                    queryBuffer.append(" on b.FVchNum=a.FVchNum ");
                    queryBuffer.append(" where a.FVchNum =").append(dbl.sqlString(vchDataRs.getString("FVchNum")));
                    queryBuffer.append(" and not exists(select distinct fvchnumrela from A").append(YssFun.formatDate(vchDataRs.getDate("FVchDate"), "yyyy")).append(vchDataRs.getString("FBookSetCode")).append("fcwvch c where fpzly='HD' and c.fvchnumrela=a.FVchNum)");
                    queryBuffer.append(" order by a.fdcway");
                    vchDataEntityRs = dbl.queryByPreparedStatement(queryBuffer.toString()); //modify by fangjiang 2011.08.14 STORY #788
                    queryBuffer.setLength(0);
                    sTmp = dbFun.getNextInnerCode(cw.getCWTabName(vchDataRs.
                        getString("FPortCode"),
                        vchDataRs.getDate("FVchdate"), "fcwvch")
                                                  ,
                                                  "FVchPdh", "1",
                                                  " where FTerm=" +
                                                  YssFun.getMonth(
                        vchDataRs.getDate("FVchDate")), 1);
                    vchNum = YssFun.toInt(sTmp);

                    strSql = "insert into A" +
                        YssFun.formatDate(vchDataRs.getDate("FVchDate"), "yyyy") +
                        vchDataRs.getString("FBookSetCode") +
                        "fcwvch " +
                        "(Fterm,FvchclsId,Fvchpdh,Fvchbh,Fvchzy," +
                        "	Fkmh,FCyId,FRate,Fyhdzbz,FBal,Fjd,FBBal," +
                        " Fsl,FBsl,Fdj,Fdate,Fywdate,Ffjzs,Fzdr,Fcheckr," +
                        " Fxgr,Fgzr,Fgzbz,Fpzly,fzqjyfs,FMemo,fnumid,fcashid," +
                        " Fpz1,Fpz2,FFromSet,FToLevel,FUpLoad,FAuxiAcc,FConfirmer,FVCHNUMRELA)" +// add by jsc 20120413添加字段FVCHNUMRELA 用于记录估值系统的凭证编号【STORY #2387 财务系统，手工修改自动凭证，凭证审核后，如重新生成凭证，被审核的凭证会被覆盖】
                        " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    pstm = conn.prepareStatement(strSql);
                    while (vchDataEntityRs.next()) {
                        //strDate = vchDataEntityRs.getString("FVchDate");
//             vchNum=vchDataEntityRs.getString("FVchNum");
                       // arrDate = strDate.split("-");
                        pstm.setInt(1, YssFun.getMonth(vchDataRs.getDate("FVchDate")));
                        pstm.setString(2, " ");
                        System.out.println("凭证号：" +
                                           vchDataEntityRs.getString("FVchNum") +
                                           "         分录号:" +
                                           vchDataEntityRs.getString("Fentitynum"));
                        pstm.setInt(3, vchNum);
                        pstm.setInt(4, enId);
                        pstm.setString(5, vchDataEntityRs.getString("FResume"));
                        pstm.setString(6, vchDataEntityRs.getString("FSubjectCode"));
                        pstm.setString(7,
                                       cw.getCWAccountCury(vchDataEntityRs.
                            getString("FSubjectCode"), vchDataRs.getDate("FVchdate"),
                            vchDataRs.
                            getString("FPortCode")));
                        pstm.setDouble(8, vchDataEntityRs.getDouble("FCuryRate"));
                        pstm.setInt(9, 0);
                        pstm.setDouble(10, vchDataEntityRs.getDouble("FBal"));
                        pstm.setString(11,
                                       (vchDataEntityRs.getString("FDCWay").
                                        equalsIgnoreCase("0") ||
                                        vchDataEntityRs.getString("FDCWay").
                                        equalsIgnoreCase("J") ? "J" : "D"));
                        pstm.setDouble(12, vchDataEntityRs.getDouble("FSetBal"));
                        pstm.setDouble(13, vchDataEntityRs.getDouble("FAmount"));
                        pstm.setDouble(14, vchDataEntityRs.getDouble("FAmount"));

                        pstm.setDouble(15, vchDataEntityRs.getDouble("FPrice"));
                        pstm.setDate(16, vchDataEntityRs.getDate("FVchDate"));
                        pstm.setDate(17, vchDataEntityRs.getDate("FVchDate"));
                        pstm.setInt(18, 0);
                        pstm.setString(19, pub.getUserName());
                        pstm.setString(20, " "); //导入到财务里面的凭证是为“未审核”状态
                        pstm.setString(21, " ");
                        pstm.setString(22, " ");
                        pstm.setInt(23, 0);
                        pstm.setString(24,
                                       (vchDataRs.getString("FVchInd") == null ? " " :
                                        vchDataRs.getString("FVchInd")));
                        pstm.setString(25,
                                       (vchDataRs.getString("FVchTWay") == null ? " " :
                                        vchDataRs.getString("FVchTWay")));
                        pstm.setString(26, " ");
                        pstm.setLong(27, 1);
                        pstm.setString(28, " ");
                        pstm.setString(29, " ");
                        pstm.setString(30, " ");
                        pstm.setInt(31, 0);
                        pstm.setInt(32, 0);
                        pstm.setInt(33, 0);
                        pstm.setString(34,
                                       (vchDataEntityRs.getString("FAssistant") == null ?
                                        " " :
                                        vchDataEntityRs.getString("FAssistant")));
                        pstm.setString(35, pub.getUserName());//add by yeshenghong 20120410
						 //【STORY #2387 财务系统，手工修改自动凭证，凭证审核后，如重新生成凭证，被审核的凭证会被覆盖】
                        //  add by jsc 20120413添加字段FVCHNUMRELA 用于记录估值系统的凭证编号
                        pstm.setString(36, vchDataRs.getString("fvchnum"));
                        enId++;
                        pstm.executeUpdate();
                    }
                    dbl.closeResultSetFinal(vchDataEntityRs);
                    dbl.closeStatementFinal(pstm);
                }
                dbl.closeResultSetFinal(vchDataRs);
                
                //add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                logInfo += "    导入组合【" + aryPorts[ports] + "】的凭证完成！... ...\r\n";
                
                runStatus.appendRunDesc("VchRun", "    导入组合【" + aryPorts[ports] + "】的凭证完成！... ...\r\n");
                
                //---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                if(logSumCode.trim().length() > 0){
            		//edit by songjie 2012.11.20 添加非空判断
            		if(logOper != null){
            			logOper.setDayFinishIData(this, 25, "凭证导出", pub, false, portCode, 
            					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
            					YssFun.toDate(this.endDate), logInfo, 
            					logStartTime, logSumCode, new java.util.Date());
            		}
                }
    			//---add by songjie 2012.09.05 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            }

            //runStatus.appendRunDesc("VchRun","  向财务系统导入凭证完成！\r\n");
            reStr = "true";
            return reStr;
        } catch (Exception e) {
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
			try{
				if(logSumCode.trim().length() > 0){
	        		//edit by songjie 2012.11.20 添加非空判断
	        		if(logOper != null){
	        			logOper.setDayFinishIData(this, 25, "凭证导出", pub, true, portCode, 
	        					YssFun.toDate(this.beginDate), YssFun.toDate(this.beginDate), 
	        					YssFun.toDate(this.endDate), 
	        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A start---//
	        					(logInfo + "\r\n凭证导出出错\r\n" + e.getMessage())//处理日志信息 除去特殊符号
	        					.replaceAll("\t", "").replaceAll("&", "").replaceAll("\f\f", ""),
	        					//---edit by songjie 2013.01.14 STORY #2343 QDV4建行2012年3月2日04_A end---//
	        					logStartTime, logSumCode, new java.util.Date());
	        		}
				}
				runStatus.appendRunDesc("VchRun", "导入凭证失败:" + e.getMessage());//add by huangqirong 2012-11-09 bug #6181
				reStr = "false";
			}catch(Exception ex){
				ex.printStackTrace();
			}
			//---add by songjie 2012.09.04 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A start---//
			finally{//添加 finally 保证可以抛出异常
            	throw new YssException(e.getMessage(), e);
            }
            //---edit by songjie 2013.01.11 STORY #2343 QDV4建行2012年3月2日04_A end---//
        } finally {
            dbl.closeStatementFinal(pstm);
            dbl.closeResultSetFinal(vchDataRs);
            dbl.closeResultSetFinal(vchDataEntityRs);
        }
    }

    /**
     * 修改方法的参数列表,加入组合的传入.为的是防止在多组合操作时避免误删除.
     * sj edit 20080328
     * @param sportCode String
     * @throws YssException
     */
    public void delete(String sportCode) throws YssException {
        String strSql = "";
        ResultSet rs = null;
        boolean bTrans = false;
        YssFinance fc = new YssFinance();
        Connection conn = dbl.loadConnection();
        java.util.Date dDate;
        int iYearNum = 0;
        String sVchTWays = "", sVchInds = "";
        try {
            iYearNum = YssFun.getYear(YssFun.toDate(endDate)) -
                YssFun.getYear(YssFun.toDate(beginDate));
            fc.setYssPub(pub);
            conn.setAutoCommit(false);
            //获取删除凭证类型，不能根据凭证数据中存在的凭证类型来删除fazmm20071111
            strSql = "select distinct a.FVchTWay,b.FVchInd from " +
                //"(select a1.*,a2.fattrcode,a2.FVchTWay from " +
                //pub.yssGetTableName("Tb_Vch_Data") +
                //" a1 left join " +
                pub.yssGetTableName("Tb_Vch_VchTpl") +
                //" a2 on a1.fvchtplcode = a2.fvchtplcode)" +
                " a left join (select FAttrCode,FVchInd from " +
                pub.yssGetTableName("Tb_Vch_Attr") +
                " where FCheckState = 1) b on a.FAttrCode = b.FAttrCode" +
                " where a.FVchTplCode in (" + //operSql.sqlCodes(vchTplCodes)
                (this.isInData ? operSql.sqlCodes(getVchTpl()) : operSql.sqlCodes(vchTypes)) +
                ")";
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                sVchTWays += rs.getString("FVchTWay") + ",";
                sVchInds += rs.getString("FVchInd") + ",";
            }
            if (sVchTWays.length() > 0) {
                sVchTWays = sVchTWays.substring(0, sVchTWays.length() - 1);
                if (sVchTWays.equalsIgnoreCase("null")) {
                    sVchTWays = " ";
                }
            }
            if (sVchInds.length() > 0) {
                sVchInds = sVchInds.substring(0, sVchInds.length() - 1);
                if (sVchInds.equalsIgnoreCase("null")) {
                    sVchInds = " ";
                }
            }
            
            dbl.closeResultSetFinal(rs); //add by fangjiang BUG 3442 2012.01.11 bug 3442
            
            //还需判断是否在该组合群中是否有设置帐套组合链接的 zml 2007.12.17
            strSql = "select distinct trim(to_char(a.FBookSetCode,'000')) as  FBookSetCode from " +
                //-----------在此加入组合的查询条件，只查出需要的套帐 sj edit 20080328 -----------------------
                "(select * from " +
                pub.yssGetTableName("Tb_Vch_Data") + " where FPortCode = " + dbl.sqlString(sportCode) +
                ")" +
                //---------------------------------------------------------------------------------------
                //这里也需根据前台操作方法判断：直接取凭证模板代码还是通过属性代码间接算凭证模板代码　QDV4招商证券2009年04月16日02_B MS00384 by leeyu 20090417
                //" a where a.FVchTplCode in (" + operSql.sqlCodes(vchTplCodes) +
                " a where a.FVchTplCode in (" + (this.isInData ? operSql.sqlCodes(getVchTpl()) : operSql.sqlCodes(vchTypes)) +
                ") and a.FCheckState = 1 " ;
//                +"and exists(select * from " +
//                pub.yssGetTableName("tb_vch_bookset") +
//                " where FBookSetCode= a.FBookSetCode)"; //导入财务时，应该只把凭证浏览里面已审核的凭证导入财务，20070919，杨
            rs = dbl.queryByPreparedStatement(strSql); //modify by fangjiang 2011.08.14 STORY #788
            while (rs.next()) {
                dDate = YssFun.toDate(beginDate);
                for (int i = 0; i <= iYearNum; i++) {
                    strSql = " delete from " +
                        fc.getCWTabName(dDate,
                                        rs.getString("FBookSetCode"), "fcwvch") +
                        " where 	Fdate between " +
                        dbl.sqlDate(beginDate) +
                        " and " +
                        dbl.sqlDate(endDate) +
                        " and FPzLy <> 'HD' " +
                        " and FPzLy in (" + operSql.sqlCodes(sVchInds) +
                        ") and FZqJyFs in (" + operSql.sqlCodes(sVchTWays) +
                        ")";
                    dbl.executeSql(strSql);
                    dDate = YssFun.addYear(dDate, 1);
                }
            }
            bTrans = true;
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }


   private String getBookSet(String portcode)throws YssException{
    	
    	ResultSet rs = null;
    	StringBuffer queryBuf = new StringBuffer();
    	String bookSet="";
    	try{
    		if(YssCons.YSS_VCH_DOOUTACC_MODE.equalsIgnoreCase("batch")){
    			queryBuf.append(" select b.fsetcode from ");
        		queryBuf.append(" (select fportcode,fassetcode from  " ).append(pub.yssGetTableName("tb_para_portfolio")).append(" where fcheckstate=1)a ");
                queryBuf.append(" left join ");
                queryBuf.append(" (select fsetid,fyear,fsetcode from lsetlist) b on a.fassetcode = b.fsetid ");
                queryBuf.append(" where a.fportcode=").append(dbl.sqlString(portcode));
                queryBuf.append(" order by  b.fyear desc");
    		}else{
    			queryBuf.append(" select distinct FPortCode, trim(to_char(fsetcode,'000'))  as FBookSetCode " ) 
				.append( " from lsetlist l join " ).append("tb_para_portfolio").append("p on l.fsetid = p.fassetcode ");
    			queryBuf.append(" where p.FCheckState = 1 and p.FPortCode = ").append(dbl.sqlString(portcode));//modified by yeshenghong 20130428 BUG7486 
//    			queryBuf.append(" select FPortCode,FBookSetCode as fsetcode from ").append(pub.yssGetTableName("Tb_Vch_PortSetLink"));
//    			queryBuf.append(" where FCheckState = 1 and FPortCode = ").append(dbl.sqlString(portcode));
    		}
    		
            rs = dbl.openResultSet(queryBuf.toString());
            if(rs.next()){
            	bookSet = rs.getString("fsetcode");
            }else{
            	throw new YssException("匹配不到组合【"+portcode+"】对于的套帐代码... ...");
            }
           
            return bookSet;
    	}catch(Exception e){
    		throw new YssException(e);
    	}finally{
    		 queryBuf.setLength(0);
             dbl.closeResultSetFinal(rs);
    	}
    }
}
