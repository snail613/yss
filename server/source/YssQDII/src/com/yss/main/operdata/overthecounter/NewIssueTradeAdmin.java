package com.yss.main.operdata.overthecounter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.yss.dsub.BaseDataSettingBean;
import com.yss.main.dao.IDataSetting;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdata.overthecounter.pojo.NewIssueTradeBean;
import com.yss.main.operdeal.BaseOperDeal;
import com.yss.main.operdeal.bond.BaseBondOper;
import com.yss.main.parasetting.FixInterestBean;
import com.yss.main.parasetting.SecurityBean;
import com.yss.pojo.param.bond.YssBondIns;
import com.yss.util.YssCons;
import com.yss.util.YssD;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssOperCons;

/**
 *
 * <p>Title: 新股新债业务界面操作类</p>
 *
 * <p>Description: MS00022    国内债券业务    QDV4.1赢时胜（上海）2009年4月20日22_A</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable 2009.07.03 蒋锦 添加
 * @version 1.0
 */
public class NewIssueTradeAdmin extends BaseDataSettingBean implements IDataSetting {

    private NewIssueTradeBean newIssueTradeBean;
    private String recycled = "";
    public NewIssueTradeAdmin() {
    }

    public void checkInput(byte btOper) throws YssException {
    	//---------------xuqiji 20100406 MS00961 QDV4赢时胜（测试）2010年03月30日03_B 增加重复信息时，提示信息交易类型应该为名称而不应该是代码 ----//
    	try{
    		String[] arrTradeCode = newIssueTradeBean.getTradeTypeCode().split(",");
            for(int i = 0; i < arrTradeCode.length; i++){
                //中签交易的修改无法正确执行数据验证
                if((arrTradeCode[i].equalsIgnoreCase("43") || arrTradeCode[i].equalsIgnoreCase("44")) &&
                    btOper == YssCons.OP_EDIT){
                    continue;
                }
                dbFun.checkInputCommon(btOper,
                                       pub.yssGetTableName("Tb_Data_NewIssueTrade"),
                                       "FNum,FTradeTypeCode",
                                       newIssueTradeBean.getNum() + "," + arrTradeCode[i],
                                       newIssueTradeBean.getOldNum() + "," + arrTradeCode[i]);
            }
    	}catch (Exception e) {
			String []sMessage = e.getMessage().split(",");
			if(sMessage[1].indexOf("40")!= -1){
				throw new YssException(sMessage[0] +","+ sMessage[1].replaceAll("40","申购"));
			}else if(sMessage[1].indexOf("43")!= -1){
				throw new YssException(sMessage[0] +","+ sMessage[1].replaceAll("43","增发中签"));
			}else if(sMessage[1].indexOf("44")!= -1){
				throw new YssException(sMessage[0] +","+ sMessage[1].replaceAll("44","网下中签"));
			}else if(sMessage[1].indexOf("42")!= -1){
				throw new YssException(sMessage[0] +","+ sMessage[1].replaceAll("42","未中返还"));
			}else if(sMessage[1].indexOf("45")!= -1){
				throw new YssException(sMessage[0] +","+ sMessage[1].replaceAll("45","锁定"));
			}else if(sMessage[1].indexOf("46")!= -1){
				throw new YssException(sMessage[0] +","+ sMessage[1].replaceAll("46","新股新债流通"));
			}
		}
    	//---------------------------------------------------end-----------------------------------------------------------------------//
    }

    private double getBondInterest(NewIssueTradeBean newIssue) throws YssException{
        double dbBondIns = 0;
        SecurityBean security = new SecurityBean();
        try {
            security.setStrSecurityCode(newIssue.getSecurityCode());
            security.setYssPub(pub);
            security.getSetting();
            if(security.getStrCategoryCode().equalsIgnoreCase("FI")){
                BaseBondOper bondOper = null;
                BaseOperDeal operDeal = new BaseOperDeal();
                operDeal.setYssPub(pub);
                YssBondIns bondIns = new YssBondIns();

                bondOper = operDeal.getSpringRe(newIssue.getSecurityCode(), "Buy"); //生成BaseBondOper
                if(bondOper == null){
                    throw new YssException("债券【" + newIssue.getSecurityCode() + "】买入利息计算公式没有录入或录入错误，请确认！");
                }
                bondIns.setInsType("Buy");

                bondIns.setSecurityCode(newIssue.getSecurityCode());
                bondIns.setInsDate(newIssue.getLucklyTransDate());
                bondIns.setInsAmount(newIssue.getLucklyAmount());
                bondIns.setPortCode(newIssue.getPortCode());
                bondOper.setYssPub(pub);
                bondOper.init(bondIns);
				//edit by songjie 2012.10.15 修改国内债券业务问题 添加债券利息保留位数
                dbBondIns = YssD.round(YssD.mul(bondOper.calBondInterest(),newIssue.getLucklyAmount()), 2);//modyfy by zhouwei 20120417 类型为buy，公式计算的是每百元利息，所以需要乘数量
            }
            else{
                dbBondIns = 0;
            }
        } catch (Exception ex) {
            throw new YssException("获取债券中签利息出错！", ex);
        }
        return dbBondIns;
    }

    /**shashijie 2012-10-25 BUG 6032 增加重载方法,多传入判断修改状态判断标示,true表示需要判断可编辑状态*/
    private String getNewIssueListForNewIssueEnity(ArrayList alNewIssue) throws YssException{
		String sDelTradeType = getNewIssueListForNewIssueEnity(alNewIssue,true);
        return sDelTradeType;
    }

    /**shashijie 2012-10-25 BUG 6032 增加重载方法,多传入判断修改状态判断标示,true表示需要判断可编辑状态 */
	private String getNewIssueListForNewIssueEnity(ArrayList alNewIssue,
			boolean flag) throws YssException {
		String sDelTradeType = "";
        NewIssueTradeBean newIssue;
        String sNum = "";
        String strNumberDate = "";
        
        boolean ChkApp = false;//申购
        boolean ChkLuckly = false;//中签
        boolean chkReturn = false;//返款
        boolean chkLock = false;//锁定
        boolean chkCurrent = false;//返款
        try {
        	//判断是否可编辑状态,如果是"审核","清除","还原"功能进来的则不需要判断
			if (flag) {
				ChkApp = newIssueTradeBean.getChkApp() == 1;//申购
				ChkLuckly = newIssueTradeBean.getChkLuckly() == 1;//中签
				chkReturn = newIssueTradeBean.getChkReturn() == 1;//返款
		        chkLock = newIssueTradeBean.getChkLock() == 1;//锁定
		        chkCurrent = newIssueTradeBean.getChkCurrent() == 1;//返款
			} else {
				ChkApp = true;//申购
		        ChkLuckly = true;//中签
		        chkReturn = true;//返款
		        chkLock = true;//锁定
		        chkCurrent = true;//返款
			}
			
            if(newIssueTradeBean.getNum().length() == 0 ||
                newIssueTradeBean.getNum().equalsIgnoreCase("null")){
                strNumberDate = YssFun.formatDate(newIssueTradeBean.getBargainDate(), "yyyyMMdd").
                    substring(0, 8);
                sNum = "NSB" + strNumberDate +
                    dbFun.getNextInnerCode(pub.yssGetTableName(
                        "Tb_Data_NewIssueTrade"),
                                           dbl.sqlRight("FNum", 9),
                                           "000000000",
                                           " WHERE FBargainDate =" +
                                           dbl.sqlDate(newIssueTradeBean.getBargainDate()));
            }
            else{
                sNum = newIssueTradeBean.getNum();
            }
            if(!newIssueTradeBean.getAppTransDate().equals(YssFun.toDate("1901-01-01"))
            		&& ChkApp
            ){
                sDelTradeType += YssOperCons.YSS_JYLX_XGSG + ",";
                newIssue = new NewIssueTradeBean();
                newIssue.setBargainDate(newIssueTradeBean.getBargainDate());
                newIssue.setTradeTypeCode(YssOperCons.YSS_JYLX_XGSG);
                newIssue.setNum(sNum);
                newIssue.setSecurityCode(newIssueTradeBean.getSecurityCode());
                newIssue.setPortCode(newIssueTradeBean.getPortCode());
                newIssue.setInvMgrCode(newIssueTradeBean.getInvMgrCode());
                newIssue.setInvestType(newIssueTradeBean.getInvestType());
                newIssue.setAttrClsCode(newIssueTradeBean.getAttrClsCode());
                newIssue.setSecurityType(newIssueTradeBean.getSecurityType());
                newIssue.checkStateId = newIssueTradeBean.checkStateId;
                newIssue.creatorCode = pub.getUserCode();
                newIssue.creatorTime = YssFun.formatDatetime(new java.util.Date());
                newIssue.setAppCashAccCode(newIssueTradeBean.getAppCashAccCode());
                newIssue.setAppMoney(newIssueTradeBean.getAppMoney());
                newIssue.setAppTransDate(newIssueTradeBean.getAppTransDate());
                alNewIssue.add(newIssue);
            }
            
            if(!newIssueTradeBean.getLucklyTransDate().equals(YssFun.toDate("1901-01-01"))
            		&& ChkLuckly
            ){
                sDelTradeType += YssOperCons.YSS_JYLX_ZFZQ + "," + YssOperCons.YSS_JYLX_WXZQ + ",";
                newIssue = new NewIssueTradeBean();
                if(newIssueTradeBean.getTradeTypeCode().indexOf(YssOperCons.YSS_JYLX_ZFZQ) >= 0){
                    newIssue.setTradeTypeCode(YssOperCons.YSS_JYLX_ZFZQ);
                } else {
                    newIssue.setTradeTypeCode(YssOperCons.YSS_JYLX_WXZQ);
                }
                newIssue.setBargainDate(newIssueTradeBean.getBargainDate());
                newIssue.setNum(sNum);
                newIssue.setSecurityCode(newIssueTradeBean.getSecurityCode());
                newIssue.setInvestType(newIssueTradeBean.getInvestType());
                newIssue.setPortCode(newIssueTradeBean.getPortCode());
                newIssue.setInvMgrCode(newIssueTradeBean.getInvMgrCode());
                newIssue.setAttrClsCode(newIssueTradeBean.getAttrClsCode());
                newIssue.setSecurityType(newIssueTradeBean.getSecurityType());
                newIssue.checkStateId = newIssueTradeBean.checkStateId;
                newIssue.creatorCode = pub.getUserCode();
                newIssue.creatorTime = YssFun.formatDatetime(new java.util.Date());
                newIssue.setLucklyAmount(newIssueTradeBean.getLucklyAmount());
                newIssue.setLucklyMoney(newIssueTradeBean.getLucklyMoney());
                newIssue.setLucklyTransDate(newIssueTradeBean.getLucklyTransDate());
                newIssue.setDirectBallot(newIssueTradeBean.getDirectBallot());//直接中签 story3395 20130131 yeshenghong
                //---edit by songjie 2012.02.28 BUG 3869 QDV4赢时胜(测试)2012年02月14日01_B start---//
                //获取债券计息起始日，如果计息起始日 大于 中签日期的话  则 不计算债券买入利息
                FixInterestBean fix = new FixInterestBean();
                fix.setYssPub(pub);
                fix.setStrSecurityCode(newIssueTradeBean.getSecurityCode());
                fix.getSetting();
                
                if(fix.getDtInsStartDate() != null){
                	if(YssFun.dateDiff(newIssueTradeBean.getLucklyTransDate(),fix.getDtInsStartDate()) > 0){
                		newIssue.setBondIns(0);
                	}else{
                		newIssue.setBondIns(getBondInterest(newIssueTradeBean));
                	}
                }
                //---dit by songjie 2012.02.28 BUG 3869 QDV4赢时胜(测试)2012年02月14日01_B end---//
                alNewIssue.add(newIssue);
            }
            if(!newIssueTradeBean.getReturnTransDate().equals(YssFun.toDate("1901-01-01"))
            		&& chkReturn
            ){
                sDelTradeType += YssOperCons.YSS_JYLX_ZQFK + ",";
                newIssue = new NewIssueTradeBean();
                newIssue.setBargainDate(newIssueTradeBean.getBargainDate());
                newIssue.setTradeTypeCode(YssOperCons.YSS_JYLX_ZQFK);
                newIssue.setNum(sNum);
                newIssue.setSecurityCode(newIssueTradeBean.getSecurityCode());
                newIssue.setInvestType(newIssueTradeBean.getInvestType());
                newIssue.setPortCode(newIssueTradeBean.getPortCode());
                newIssue.setInvMgrCode(newIssueTradeBean.getInvMgrCode());
                newIssue.setAttrClsCode(newIssueTradeBean.getAttrClsCode());
                newIssue.setSecurityType(newIssueTradeBean.getSecurityType());
                newIssue.checkStateId = newIssueTradeBean.checkStateId;
                newIssue.creatorCode = pub.getUserCode();
                newIssue.creatorTime = YssFun.formatDatetime(new java.util.Date());
                newIssue.setReturnCashAccCode(newIssueTradeBean.getReturnCashAccCode());
                newIssue.setReturnMoney(newIssueTradeBean.getReturnMoney());
                newIssue.setReturnTransDate(newIssueTradeBean.getReturnTransDate());
                alNewIssue.add(newIssue);
            }
            if(!newIssueTradeBean.getLockBeginDate().equals(YssFun.toDate("1901-01-01"))
            		&& chkLock
            ){
                sDelTradeType += YssOperCons.YSS_JYLX_SD + ",";
                newIssue = new NewIssueTradeBean();
                newIssue.setBargainDate(newIssueTradeBean.getBargainDate());
                newIssue.setTradeTypeCode(YssOperCons.YSS_JYLX_SD);
                newIssue.setNum(sNum);
                newIssue.setSecurityCode(newIssueTradeBean.getSecurityCode());
                newIssue.setInvestType(newIssueTradeBean.getInvestType());
                newIssue.setPortCode(newIssueTradeBean.getPortCode());
                newIssue.setInvMgrCode(newIssueTradeBean.getInvMgrCode());
                newIssue.setAttrClsCode(newIssueTradeBean.getAttrClsCode());
                newIssue.setSecurityType(newIssueTradeBean.getSecurityType());
                newIssue.checkStateId = newIssueTradeBean.checkStateId;
                newIssue.creatorCode = pub.getUserCode();
                newIssue.creatorTime = YssFun.formatDatetime(new java.util.Date());
                newIssue.setLockAmount(newIssueTradeBean.getLockAmount());
                newIssue.setLockBeginDate(newIssueTradeBean.getLockBeginDate());
                newIssue.setLockEndDate(newIssueTradeBean.getLockEndDate());
                newIssue.setLockDays(newIssueTradeBean.getLockDays());
                newIssue.setPriceMoney(newIssueTradeBean.getPriceMoney());
                newIssue.setDirectBallot(newIssueTradeBean.getDirectBallot());//直接中签 story3395 20130131 yeshenghong
                alNewIssue.add(newIssue);
            }
            if(!newIssueTradeBean.getCurrentTransDate().equals(YssFun.toDate("1901-01-01"))
            		&& chkCurrent
            ){
                sDelTradeType += YssOperCons.YSS_JYLX_XGLT + ",";
                newIssue = new NewIssueTradeBean();
                newIssue.setBargainDate(newIssueTradeBean.getBargainDate());
                newIssue.setTradeTypeCode(YssOperCons.YSS_JYLX_XGLT);
                newIssue.setNum(sNum);
                newIssue.setSecurityCode(newIssueTradeBean.getSecurityCode());
                newIssue.setInvestType(newIssueTradeBean.getInvestType());
                newIssue.setPortCode(newIssueTradeBean.getPortCode());
                newIssue.setInvMgrCode(newIssueTradeBean.getInvMgrCode());
                newIssue.setAttrClsCode(newIssueTradeBean.getAttrClsCode());
                newIssue.setSecurityType(newIssueTradeBean.getSecurityType());
                newIssue.checkStateId = newIssueTradeBean.checkStateId;
                newIssue.creatorCode = pub.getUserCode();
                newIssue.creatorTime = YssFun.formatDatetime(new java.util.Date());
                newIssue.setCurrentAmount(newIssueTradeBean.getCurrentAmount());
                newIssue.setCurrentMoney(newIssueTradeBean.getCurrentMoney());
                newIssue.setCurrentTransDate(newIssueTradeBean.getCurrentTransDate());
                newIssue.setDirectBallot(newIssueTradeBean.getDirectBallot());//直接中签 story3395 20130131 yeshenghong
                alNewIssue.add(newIssue);
            }
            if(sDelTradeType.length() > 0){
                sDelTradeType = sDelTradeType.substring(0, sDelTradeType.length() - 1);
            }
        } catch (Exception ex) {
            throw new YssException(ex);
        }
        return sDelTradeType;
	}

	private void saveNewIssueData(ArrayList alNewIssue) throws YssException{
        String strSql = "";
        PreparedStatement pst = null;
        Connection conn = dbl.loadConnection();
        NewIssueTradeBean newIssue = null;
        try {
            strSql = "INSERT INTO " + pub.yssGetTableName("Tb_Data_NewIssueTrade") +
                " (FNUM, FTRADETYPECODE, FSECURITYCODE, FPORTCODE, FBARGAINDATE, FSECURITYTYPE, FINVESTTYPE, FINVMGRCODE, FATTRCLSCODE, FTRANSDATE, FLOCKBEGINDATE, FLOCKENDDATE, FMONEY, FBONDINS, FPRICEMONEY, FCASHACCCODE, FAMOUNT, FLOCKDAYS,FDIRBALLOT, FCHECKSTATE, FCREATOR, FCREATETIME, FCHECKUSER, FCHECKTIME)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(strSql);
            for(int i = 0; i < alNewIssue.size(); i++){
                newIssue = (NewIssueTradeBean)alNewIssue.get(i);
                pst.setString(1, newIssue.getNum());
                pst.setString(2, newIssue.getTradeTypeCode());
                pst.setString(3, newIssue.getSecurityCode());
                pst.setString(4, newIssue.getPortCode());
                pst.setDate(5, YssFun.toSqlDate(newIssue.getBargainDate()));
                pst.setString(6, newIssue.getSecurityType());
                pst.setString(7, newIssue.getInvestType());
                pst.setString(8, newIssue.getInvMgrCode().length() == 0? " " : newIssue.getInvMgrCode());
                pst.setString(9, newIssue.getAttrClsCode());
                if(newIssue.getAppTransDate() != null &&
                   !newIssue.getAppTransDate().equals(YssFun.toDate("1901-01-01"))){
                    pst.setDate(10, YssFun.toSqlDate(newIssue.getAppTransDate()));
                    pst.setDouble(13, newIssue.getAppMoney());
                    pst.setString(16, newIssue.getAppCashAccCode());
                    pst.setDouble(17, 0);
                } else if(newIssue.getLucklyTransDate() != null &&
                          !newIssue.getLucklyTransDate().equals(YssFun.toDate("1901-01-01"))){
                    pst.setDate(10, YssFun.toSqlDate(newIssue.getLucklyTransDate()));
                    pst.setDouble(13, newIssue.getLucklyMoney());
                    pst.setString(16, null);
                    pst.setDouble(17, newIssue.getLucklyAmount());
                } else if(newIssue.getLockBeginDate() != null &&
                          !newIssue.getLockBeginDate().equals(YssFun.toDate("1901-01-01"))) {
                    pst.setDate(10, YssFun.toSqlDate(newIssue.getLockBeginDate()));
                    pst.setDouble(13, 0);
                    pst.setString(16, null);
                    pst.setDouble(17, newIssue.getLockAmount());
                } else if(newIssue.getReturnTransDate() != null &&
                          !newIssue.getReturnTransDate().equals(YssFun.toDate("1901-01-01"))){
                    pst.setDate(10, YssFun.toSqlDate(newIssue.getReturnTransDate()));
                    pst.setDouble(13, newIssue.getReturnMoney());
                    pst.setString(16, newIssue.getReturnCashAccCode());
                    pst.setDouble(17, 0);
                } else if(newIssue.getCurrentTransDate() != null &&
                          !newIssue.getCurrentTransDate().equals(YssFun.toDate("1901-01-01"))){
                    pst.setDate(10, YssFun.toSqlDate(newIssue.getCurrentTransDate()));
                    pst.setDouble(13, newIssue.getCurrentMoney());
                    pst.setString(16, null);
                    pst.setDouble(17, newIssue.getCurrentAmount());
                }
                pst.setDate(11, newIssue.getLockBeginDate() == null ? null : YssFun.toSqlDate(newIssue.getLockBeginDate()));
                pst.setDate(12, newIssue.getLockEndDate() == null ? null : YssFun.toSqlDate(newIssue.getLockEndDate()));
                pst.setDouble(14, newIssue.getBondIns());
                pst.setDouble(15, newIssue.getPriceMoney());
                pst.setDouble(18, newIssue.getLockDays());
                pst.setDouble(19, newIssue.getDirectBallot());//直接中签 story3395 20130131 yeshenghong
                pst.setInt(20, newIssue.checkStateId);
                pst.setString(21, newIssue.creatorCode);
                pst.setString(22, newIssue.creatorTime);
                pst.setString(23, newIssue.checkUserCode);
                pst.setString(24, newIssue.checkTime);

                pst.executeUpdate();
            }

        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
    }

    public String addSetting() throws YssException {
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        ArrayList alNewIssue = new ArrayList();
        try {
            getNewIssueListForNewIssueEnity(alNewIssue);
            conn.setAutoCommit(false);
            bTrans = true;
            saveNewIssueData(alNewIssue);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception ex) {
            throw new YssException("新增新股新债业务出错！", ex);
        } finally{
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        String strSql = "";
        String sDelTradeType = "";
        boolean bTrans = false;
        Connection conn = dbl.loadConnection();
        ArrayList alNewIssue = new ArrayList();
        try {
            sDelTradeType = getNewIssueListForNewIssueEnity(alNewIssue);
            strSql = "DELETE FROM " + pub.yssGetTableName("Tb_Data_NewIssueTrade") +
                " WHERE FNum = " + dbl.sqlString(newIssueTradeBean.getOldNum()) +
                " AND FTradeTypeCode IN (" + operSql.sqlCodes(sDelTradeType) + ")"
                /**shashijie 2012-7-5 BUG 4941 只允许修改未审核数据 */
				+" And FCheckState = 0 ";
				/**end*/
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            saveNewIssueData(alNewIssue);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception ex) {
            throw new YssException("修改新股新债业务出错！", ex);
        } finally{
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        ArrayList alNewIssue = new ArrayList();
        NewIssueTradeBean newIssue = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            getNewIssueListForNewIssueEnity(alNewIssue);
            for (int j = 0; j < alNewIssue.size(); j++) {
                newIssue = (NewIssueTradeBean) alNewIssue.get(j);
                strSql = "update " + pub.yssGetTableName("Tb_Data_NewIssueTrade") +
                    " set FCheckState=" + newIssue.checkStateId +
                    " where FNum=" + dbl.sqlString(newIssue.getNum()) +
                    " AND FTradeTypeCode = " + dbl.sqlString(newIssue.getTradeTypeCode());
                dbl.executeSql(strSql);
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("删除新股新债数据出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        String[] arrData = null;
        ArrayList alNewIssue = new ArrayList();
        NewIssueTradeBean newIssue = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            arrData = recycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                //判断是否可编辑状态,如果是"审核","清除","还原"功能进来的则不需要判断
                getNewIssueListForNewIssueEnity(alNewIssue,false);
                //---add by songjie 2011.04.08 BUG 1557 QDV4赢时胜(测试)2011年03月23日2_B---//
                if(alNewIssue.size() == 0){
                    strSql = "update " + pub.yssGetTableName("Tb_Data_NewIssueTrade") +
                    " set FCheckState=" + newIssueTradeBean.checkStateId + "," +
                    " FCheckUser=" + dbl.sqlString(pub.getUserCode()) + "," +
                    " FCheckTime=" + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                    " where FNum=" + dbl.sqlString(newIssueTradeBean.getNum()) +
                    " AND FTradeTypeCode = " + operSql.sqlCodes(newIssueTradeBean.getTradeTypeCode());
                    dbl.executeSql(strSql);
                }
                //---add by songjie 2011.04.08 BUG 1557 QDV4赢时胜(测试)2011年03月23日2_B---//
                for(int j = 0; j < alNewIssue.size(); j++){
                    newIssue = (NewIssueTradeBean)alNewIssue.get(j);
                    
                    //---add by songjie 2012.12.13 BUG 6610 QDV4南方2012年12月12日01_B start---//
                    if(newIssue.checkStateId == 0){
                    	strSql = "delete from " + pub.yssGetTableName("Tb_Data_Integrated") + 
                    	" where FNumType = 'securitymanage' and FRelaNum = " + dbl.sqlString(newIssue.getNum());
                    	dbl.executeSql(strSql);
                    }
                    //---add by songjie 2012.12.13 BUG 6610 QDV4南方2012年12月12日01_B end---//
                    
                    strSql = "update " + pub.yssGetTableName("Tb_Data_NewIssueTrade") +
                        " set FCheckState=" + newIssue.checkStateId + "," +
                        " FCheckUser=" + dbl.sqlString(pub.getUserCode()) + "," +
                        " FCheckTime=" + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                        " where FNum=" + dbl.sqlString(newIssue.getNum()) +
                        " AND FTradeTypeCode = " + operSql.sqlCodes(newIssue.getTradeTypeCode());
                    dbl.executeSql(strSql);
                }
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;

        } catch (Exception ex) {
            throw new YssException("审核新股新债数据出错！", ex);
        } finally{
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

    public void deleteRecycleData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        String[] arrData = null;
        ArrayList alNewIssue = new ArrayList();
        NewIssueTradeBean newIssue = null;
        try {
            conn = dbl.loadConnection();
            arrData = recycled.split("\r\n");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                //判断是否可编辑状态,如果是"审核","清除","还原"功能进来的则不需要判断
                getNewIssueListForNewIssueEnity(alNewIssue,false);
                for (int j = 0; j < alNewIssue.size(); j++) {
                    newIssue = (NewIssueTradeBean) alNewIssue.get(j);
                    sqlStr = "DELETE FROM " + pub.yssGetTableName("Tb_Data_NewIssueTrade") +
                        " WHERE FNum=" + dbl.sqlString(newIssue.getNum()) +
                        " AND FTradeTypeCode = " + dbl.sqlString(newIssue.getTradeTypeCode());
                    dbl.executeSql(sqlStr);
                }
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("删除新股新债数据出错！", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
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
        ResultSet rs = null;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufSql = new StringBuffer();
        try {
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (newIssueTradeBean.getIsOnlyColumn().equalsIgnoreCase("1")) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType + "," + YssCons.YSS_DATA_SECURITYTYPE);
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr()+ "\r\fvoc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            bufSql.append(" SELECT distinct " +	//20130122 modified by liubo.Bug #6624.当申购、中签时间为一天时，修改数据后会查询出完全重复的数据
            			"cs.*, c.FSecurityName, c.FTradeCury, d.FSGCashAccName, d2.FFKCashAccName, e.FPortName, ");
            bufSql.append(" f.FAttrClsName, g.FInvMgrName, h.Ftradetypename, ");
            bufSql.append(" v1.FVocName AS FInvestTypeName, v2.FVocName AS FSecurityTypeName, ");
            bufSql.append(" CASE WHEN FSGNum IS NULL THEN '' ELSE '√' END AS FSGDATA, ");
            bufSql.append(" CASE WHEN FZQNum IS NULL THEN '' ELSE '√' END AS FZQDATA, ");
            bufSql.append(" CASE WHEN FFKNum IS NULL THEN '' ELSE '√' END AS FFKDATA, ");
            bufSql.append(" CASE WHEN FSDNum IS NULL THEN '' ELSE '√' END AS FSDDATA, ");
            bufSql.append(" CASE WHEN FLTNum IS NULL THEN '' ELSE '√' END AS FLTDATA, ");
            //--- add by songjie 2012.09.25 BUG 5853 QDV4海富通2012年09月25日01_B 网下新股新债业务非自审功能有问题 start---//
            bufSql.append(" s7.Fcreator as FCreator, 'admin' as FCheckUser, 'admin' as FCreatorName, ");
            bufSql.append(" 'admin' as FCheckUserName, '99981231 00:00:00' as FCreateTime, ");
            bufSql.append(" '99981231 00:00:00' as FCheckTime ");
            //--- add by songjie 2012.09.25 BUG 5853 QDV4海富通2012年09月25日01_B 网下新股新债业务非自审功能有问题 end---//
            bufSql.append(" FROM (SELECT * ");
            bufSql.append(" FROM (SELECT DISTINCT FNum, FSecurityCode, FBARGAINDATE, FPortCode, ");
            bufSql.append(" FSecurityType, FInvestType, FAttrClsCode, FInvMgrCode, FDirBallot, FCheckState ");//直接中签 story3395 20130131 yeshenghong
            bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_Newissuetrade") + ") a ");
            bufSql.append(" LEFT JOIN (SELECT FNum AS FSGNum, FTransDate AS FSGTransDate, ");
            bufSql.append(" FMoney AS FSGMoney, FCashAccCode AS FSGCashAccCode, ");
            bufSql.append(" FCheckState AS FSGCheckState ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
            bufSql.append(" WHERE FTradeTypeCode = '40') s1 ON a.FNum = s1.FSGNum ");
            bufSql.append(" AND a.FCheckState = ");
            bufSql.append(" s1.FSGCheckState ");
            bufSql.append(" LEFT JOIN (SELECT FNum AS FZQNum, FTradeTypeCode, ");
            bufSql.append(" FTransDate AS FZQTransDate, FMoney AS FZQMoney, ");
            bufSql.append(" FAmount AS FZQAmount, FCheckState AS FZQCheckState ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
            bufSql.append(" WHERE FTradeTypeCode = '44' ");
            bufSql.append(" OR FTradeTypeCode = '43') s2 ON a.FNum = s2.FZQNum ");
            bufSql.append(" AND a.FCheckState = ");
            bufSql.append(" s2.FZQCheckState ");
            bufSql.append(" LEFT JOIN (SELECT FNum AS FFKNum, FTransDate AS FFKTransDate, ");
            bufSql.append(" FMoney AS FFKMoney, FCashAccCode AS FFKCashAccCode, ");
            bufSql.append(" FBondIns, FCheckState AS FFKCheckState ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
            bufSql.append(" WHERE FTradeTypeCode = '42') s3 ON a.FNum = s3.FFKNum ");
            bufSql.append(" AND a.FCheckState = ");
            bufSql.append(" s3.FFKCheckState ");
            bufSql.append(" LEFT JOIN (SELECT FNum AS FSDNum, FLockBeginDate, FLockEndDate, ");
            bufSql.append(" FAmount AS FSDAmount, FPriceMoney, FLockDays, ");
            bufSql.append(" FCheckState AS FSDCheckState ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
            bufSql.append(" WHERE FTradeTypeCode = '45') s4 ON a.FNum = s4.FSDNum ");
            bufSql.append(" AND a.FCheckState = ");
            bufSql.append(" s4.FSDCheckState ");
            bufSql.append(" LEFT JOIN (SELECT FNum AS FLTNum, FTransDate AS FLTTransDate, ");
            bufSql.append(" FAmount AS FLTAmount, FMoney AS FLTMoney, ");
            bufSql.append(" FCheckState AS FLTCheckState ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
            bufSql.append(" WHERE FTradeTypeCode = '46') s5 ON a.FNum = s5.FLTNum ");
            bufSql.append(" AND a.FCheckState = ");
            bufSql.append(" s5.FLTCheckState) cs ");
            bufSql.append(" LEFT JOIN (SELECT FSecurityCode, FSecurityName, FTradeCury ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Para_Security"));
            bufSql.append(" WHERE FCheckState = 1) c ON cs.FSecurityCode = ");
            bufSql.append(" c.FSecurityCode ");
            bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FCashAccName AS FSGCashAccName ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_CashAccount"));
            bufSql.append(" WHERE FCheckState = 1) d ON cs.FSGCashAccCode = d.FCashAccCode ");
            bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FCashAccName AS FFKCashAccName ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_CashAccount"));
            bufSql.append(" WHERE FCheckState = 1) d2 ON cs.FFKCashAccCode = ");
            bufSql.append(" d2.FCashAccCode ");
            bufSql.append(" LEFT JOIN (SELECT p.FPortCode, p.FPortName ");//MS1270
            //editd by zhouxiang MS01270
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Portfolio p"));
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            bufSql.append(" join (select fportcode ,max(fstartdate) as fstartdate from ").append(pub.yssGetTableName("Tb_Para_Portfolio p"));
//            bufSql.append(" where fcheckstate=1 and p.fstartdate <=").append(dbl.sqlDate(new java.util.Date()));
//            bufSql.append(" group by fportcode ) xi").append(" on p.fportcode=xi.fportcode and p.fstartdate=xi.fstartdate");
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            bufSql.append(" WHERE FCheckState = 1) e ON e.FPortCode = cs.FPortCode ");
            bufSql.append(" LEFT JOIN (SELECT FAttrClsCode, FAttrClsName ");
            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Attributeclass"));
            bufSql.append(" WHERE FCheckState = 1) f ON cs.FAttrClsCode = f.FAttrClsCode ");
            //-------------------xuqiji 20100628 MS01351 新建新股新债业务数据时，会出现多条相同的数据 QDV4赢时胜(测试)2010年6月24日2_B ------//
            bufSql.append(" LEFT JOIN (");//edit by songjie 2011.03.16 不以最大的启用日期查询数据
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
//            bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Investmanager"));
//            bufSql.append(" WHERE FCheckState = 1 "); //and FStartDate <= ").append(dbl.sqlDate(newIssueTradeBean.getBargainDate())); //modify by fangjiang 2010.11.12 BUG #315
//            bufSql.append(" group by FInvMgrCode order by FInvMgrCode, FStartDate) a");
            //----delete by songjie 2011.03.16 不以最大的启用日期查询数据----//
            //edit by songjie 2011.03.16 不以最大的启用日期查询数据
            bufSql.append(" SELECT FInvMgrCode, FInvMgrName, FStartDate from ").append(pub.yssGetTableName("Tb_Para_Investmanager"));
            bufSql.append(" WHERE FCheckState = 1 "); //and FStartDate <= ").append(dbl.sqlDate(newIssueTradeBean.getBargainDate())); //modify by fangjiang 2010.11.12 BUG #315
            //delete by songjie 2011.03.16 不以最大的启用日期查询数据
//            bufSql.append(" ) b on a.FInvMgrCode = b.FInvMgrCode and a.FStartDate = b.FStartDate");
            bufSql.append(") g ON cs.FInvMgrCode = g.FInvMgrCode ");
            //------------------------end-----------------------------------//
            bufSql.append(" LEFT JOIN (SELECT FTradeTypeCode, FTradeTypeName ");
            bufSql.append(" FROM Tb_Base_Tradetype) h ON h.FTradeTypeCode = ");
            bufSql.append(" cs.FTradeTypeCode ");
            bufSql.append(" LEFT JOIN (SELECT FVocCode, FVocName ");
            bufSql.append(" FROM Tb_Fun_Vocabulary ");
            bufSql.append(" WHERE FVocTypeCode = " + dbl.sqlString(YssCons.YSS_InvestType) + ") v1 ON v1.FVocCode = ");
            bufSql.append(" cs.FInvestType ");
            bufSql.append(" LEFT JOIN (SELECT FVocCode, FVocName ");
            bufSql.append(" FROM Tb_Fun_Vocabulary ");
            bufSql.append(" WHERE FVocTypeCode = " + dbl.sqlString(YssCons.YSS_DATA_SECURITYTYPE) + ") v2 ON v2.FVocCode = ");
            bufSql.append(" cs.FSecurityType ");
            //---add by songjie 2012.09.26 BUG 5853 QDV4海富通2012年09月25日01_B 网下新股新债业务非自审功能有问题 start---//
            bufSql.append(" left join (select max(FCreateTime) as FCreateTime,FNUM from ");
            bufSql.append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
            bufSql.append(" group by FNum) s6 on s6.FNum = cs.FNum ");
            bufSql.append(" left join (select FCreator,FCreateTime,FNum from ");
            bufSql.append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
            bufSql.append(") s7 on s6.FCreateTime = s7.FCreateTime and s6.FNum = s7.FNum ");
            //---add by songjie 2012.09.26 BUG 5853 QDV4海富通2012年09月25日01_B 网下新股新债业务非自审功能有问题 end---//
            bufSql.append(getSqlWhereStr()).append(" ORDER BY cs.FNUM");

			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(bufSql.toString());
			yssPageInationBean.setsQuerySQL(bufSql.toString());
			yssPageInationBean.setsTableName("Newissuetrade");
			rs = dbl.openResultSet(yssPageInationBean);
			// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
				//edit by songjie 2012.10.09 BUG 5853 QDV4海富通2012年09月25日01_B
                this.newIssueTradeBean.setNewIssueInfo(rs);
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
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType + "," + YssCons.YSS_DATA_SECURITYTYPE);

        } catch (Exception ex) {
            throw new YssException("获取业务数据出错！", ex);
        } finally{
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
        return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
            this.getListView1ShowCols()+ "\r\f" + yssPageInationBean.buildRowStr()+ "\r\fvoc" + sVocStr;// QDV4赢时胜上海2010年03月17日06_B MS00884 by xuqiji
    }

    public String getListViewData2() throws YssException {
       return "";
    }

    public String getListViewData3() throws YssException {
        ResultSet rs = null;
       String sHeader = "";
       String sShowDataStr = "";
       String sAllDataStr = "";
       String sVocStr = "";
       StringBuffer bufShow = new StringBuffer();
       StringBuffer bufAll = new StringBuffer();
       StringBuffer bufSql = new StringBuffer();
       try {
           sHeader = this.getListView3Headers();
           bufSql.append(" SELECT cs.*, c.FSecurityName, c.FTradeCury, d.FSGCashAccName, d2.FFKCashAccName, e.FPortName, ");
           bufSql.append(" f.FAttrClsName, g.FInvMgrName, h.Ftradetypename, ");
           bufSql.append(" v1.FVocName AS FInvestTypeName, v2.FVocName AS FSecurityTypeName ");
           bufSql.append(" FROM (SELECT a.*, s1.*, s2.*, s3.*, s4.*, s5.*, ");
           bufSql.append(" CASE WHEN a.FCheckState = 0 THEN '未审核' ELSE '已审核' END AS FCheckStateName");
           bufSql.append(" FROM (SELECT DISTINCT FNum, FSecurityCode, FBARGAINDATE, FPortCode, ");
           bufSql.append(" FSecurityType, FInvestType, FAttrClsCode, ");
           bufSql.append(" FInvMgrCode,FDirBallot, FCheckState ");
           bufSql.append(" FROM " + pub.yssGetTableName("Tb_Data_Newissuetrade") + ") a ");
           bufSql.append(" LEFT JOIN (SELECT FNum AS FSGNum, FTransDate AS FSGTransDate, ");
           bufSql.append(" FMoney AS FSGMoney, FCashAccCode AS FSGCashAccCode, ");
           bufSql.append(" FCheckState AS FSGCheckState ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
           bufSql.append(" WHERE FTradeTypeCode = '40') s1 ON a.FNum = s1.FSGNum ");
           bufSql.append(" AND a.FCheckState = ");
           bufSql.append(" s1.FSGCheckState ");
           bufSql.append(" LEFT JOIN (SELECT FNum AS FZQNum, FTradeTypeCode, ");
           bufSql.append(" FTransDate AS FZQTransDate, FMoney AS FZQMoney, ");
           bufSql.append(" FAmount AS FZQAmount, FCheckState AS FZQCheckState ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
           bufSql.append(" WHERE FTradeTypeCode = '44' ");
           bufSql.append(" OR FTradeTypeCode = '43') s2 ON a.FNum = s2.FZQNum ");
           bufSql.append(" AND a.FCheckState = ");
           bufSql.append(" s2.FZQCheckState ");
           bufSql.append(" LEFT JOIN (SELECT FNum AS FFKNum, FTransDate AS FFKTransDate, ");
           bufSql.append(" FMoney AS FFKMoney, FCashAccCode AS FFKCashAccCode, ");
           bufSql.append(" FBondIns, FCheckState AS FFKCheckState ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
           bufSql.append(" WHERE FTradeTypeCode = '42') s3 ON a.FNum = s3.FFKNum ");
           bufSql.append(" AND a.FCheckState = ");
           bufSql.append(" s3.FFKCheckState ");
           bufSql.append(" LEFT JOIN (SELECT FNum AS FSDNum, FLockBeginDate, FLockEndDate, ");
           bufSql.append(" FAmount AS FSDAmount, FPriceMoney, FLockDays, ");
           bufSql.append(" FCheckState AS FSDCheckState ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
           bufSql.append(" WHERE FTradeTypeCode = '45') s4 ON a.FNum = s4.FSDNum ");
           bufSql.append(" AND a.FCheckState = ");
           bufSql.append(" s4.FSDCheckState ");
           bufSql.append(" LEFT JOIN (SELECT FNum AS FLTNum, FTransDate AS FLTTransDate, ");
           bufSql.append(" FAmount AS FLTAmount, FMoney AS FLTMoney, ");
           bufSql.append(" FCheckState AS FLTCheckState ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Data_Newissuetrade"));
           bufSql.append(" WHERE FTradeTypeCode = '46') s5 ON a.FNum = s5.FLTNum ");
           bufSql.append(" AND a.FCheckState = ");
           bufSql.append(" s5.FLTCheckState) cs ");
           bufSql.append(" LEFT JOIN (SELECT FSecurityCode, FSecurityName, FTradeCury ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("TB_Para_Security"));
           bufSql.append(" WHERE FCheckState = 1) c ON cs.FSecurityCode = ");
           bufSql.append(" c.FSecurityCode ");
           bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FCashAccName AS FSGCashAccName ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_CashAccount"));
           bufSql.append(" WHERE FCheckState = 1) d ON cs.FSGCashAccCode = d.FCashAccCode ");
           bufSql.append(" LEFT JOIN (SELECT FCashAccCode, FCashAccName AS FFKCashAccName ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_CashAccount"));
           bufSql.append(" WHERE FCheckState = 1) d2 ON cs.FFKCashAccCode = ");
           bufSql.append(" d2.FCashAccCode ");
           bufSql.append(" LEFT JOIN (SELECT FPortCode, FPortName ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Portfolio"));
           bufSql.append(" WHERE FCheckState = 1) e ON e.FPortCode = cs.FPortCode ");
           bufSql.append(" LEFT JOIN (SELECT FAttrClsCode, FAttrClsName ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Attributeclass"));
           bufSql.append(" WHERE FCheckState = 1) f ON cs.FAttrClsCode = f.FAttrClsCode ");
           bufSql.append(" LEFT JOIN (SELECT FInvMgrCode, FInvMgrName ");
           bufSql.append(" FROM ").append(pub.yssGetTableName("Tb_Para_Investmanager"));
           bufSql.append(" WHERE FCheckState = 1) g ON cs.FInvMgrCode = g.FInvMgrCode ");
           bufSql.append(" LEFT JOIN (SELECT FTradeTypeCode, FTradeTypeName ");
           bufSql.append(" FROM Tb_Base_Tradetype) h ON h.FTradeTypeCode = ");
           bufSql.append(" cs.FTradeTypeCode ");
           bufSql.append(" LEFT JOIN (SELECT FVocCode, FVocName ");
           bufSql.append(" FROM Tb_Fun_Vocabulary ");
           bufSql.append(" WHERE FVocTypeCode = " + dbl.sqlString(YssCons.YSS_InvestType) + ") v1 ON v1.FVocCode = ");
           bufSql.append(" cs.FInvestType ");
           bufSql.append(" LEFT JOIN (SELECT FVocCode, FVocName ");
           bufSql.append(" FROM Tb_Fun_Vocabulary ");
           bufSql.append(" WHERE FVocTypeCode = " + dbl.sqlString(YssCons.YSS_DATA_SECURITYTYPE) + ") v2 ON v2.FVocCode = ");
           bufSql.append(" cs.FSecurityType ");
           bufSql.append(getSqlWhereStr());
           bufSql.append(" AND FCheckState <> 2");
           bufSql.append(" ORDER BY cs.FNUM");

           rs = dbl.openResultSet(bufSql.toString());
           while (rs.next()) {
               bufShow.append(this.buildRowShowStr(rs, this.getListView3ShowCols())).
                   append(YssCons.YSS_LINESPLITMARK);
               this.newIssueTradeBean.setNewIssueAttr(rs);
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
           VocabularyBean vocabulary = new VocabularyBean();
           vocabulary.setYssPub(pub);
           sVocStr = vocabulary.getVoc(YssCons.YSS_InvestType + "," + YssCons.YSS_DATA_SECURITYTYPE);

       } catch (Exception ex) {
           throw new YssException("获取业务数据出错！", ex);
       } finally{
           dbl.closeResultSetFinal(rs);
       }
       return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
           this.getListView3ShowCols() + "\r\fvoc" + sVocStr;
    }
/**
 * add by zhangjun 2012.06.39
 * BUG4879
 * 对中签返款数据进行修改时，应该加载出相关联的申购交易数据中的申购金额。
 */
    public String getListViewData4() throws YssException {
    	String sDateStr = "";
        String strSql = "";
        ResultSet rs = null;
        ResultSet rSet = null;
        ResultSet rSet1 = null;
        String strUserCode = "";
        String sShowDataStr = "";
        //String sAllDataStr = "";
        
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
       
        try {
	        	newIssueTradeBean.setAppTransDate(YssFun.toSqlDate(YssFun.parseDate("1901-01-01"))); //给出默认日期
	        	newIssueTradeBean.setReturnTransDate(YssFun.toSqlDate(YssFun.parseDate("1901-01-01")));
	       	    newIssueTradeBean.setLucklyTransDate(YssFun.toSqlDate(YssFun.parseDate("1901-01-01")));//给出默认日期
           
            	strSql = "select * from   " +    pub.yssGetTableName("Tb_Data_Newissuetrade")+ " cs "        			 
            			+ getSqlWhereStr() + "and FNum = '"+ newIssueTradeBean.getNum()+
            			"'  and (FTradeTypeCode = '40' or FTradeTypeCode = '42' or FTradeTypeCode = '44')";
            	rs = dbl.openResultSet(strSql);
            	while (rs.next()) {                    
                     if(rs.getString("FTradeTypeCode").trim().length()>0 && rs.getString("FTradeTypeCode").equals("42")){ //返款
                    	 
                    	 newIssueTradeBean.setReturnTransDate(rs.getDate("FTransDate"));
                    	 newIssueTradeBean.setReturnMoney(rs.getDouble("FMoney"));
                    	 newIssueTradeBean.setReturnCashAccCode(rs.getString("FCashAccCode"));
                    	 newIssueTradeBean.setReturnCashAccName(getCashAccName(rs.getString("FCashAccCode")));
                    	 newIssueTradeBean.iCheckStateReturn = rs.getByte("FCheckState");
                     }else if(rs.getString("FTradeTypeCode").trim().length()>0 && 
                    		 (rs.getString("FTradeTypeCode").equals("44")||rs.getString("FTradeTypeCode").equals("43") )){ //中签
                    	 
                    	 newIssueTradeBean.setLucklyTransDate(rs.getDate("FTransDate"));
                    	 newIssueTradeBean.setLucklyMoney(rs.getDouble("FMoney"));
                    	 newIssueTradeBean.setLucklyAmount(rs.getDouble("FAmount")); 
                    	 newIssueTradeBean.iCheckStateLucky = rs.getByte("FCheckState");
                    	 
                     }else if(rs.getString("FTradeTypeCode").trim().length()>0 && rs.getString("FTradeTypeCode").equals("40")){  //申购
                    	 
                    	 newIssueTradeBean.setAppTransDate(rs.getDate("FTransDate"));
                    	 newIssueTradeBean.setAppMoney(rs.getDouble("FMoney"));
                    	 newIssueTradeBean.setAppCashAccCode(rs.getString("FCashAccCode"));
                    	 newIssueTradeBean.setAppCashAccName(getCashAccName(rs.getString("FCashAccCode")));
                    	 newIssueTradeBean.iCheckStateAPP = rs.getByte("FCheckState");
                     }
                 }
            	 bufShow.append(this.strBuildRow()).append(YssCons.YSS_LINESPLITMARK);
	            if (bufShow.toString().length() > 2) {
	                sShowDataStr = bufShow.toString().substring(0,
	                    bufShow.toString().length() - 2);
	            }       
	            return  sShowDataStr ;
            
        } catch (Exception e) {
            throw new YssException("获取统计参数信息出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeResultSetFinal(rSet);
            dbl.closeResultSetFinal(rSet1);
           
        }
        
    }
    
    /**
     * add by zhangjun 2012.06.39
     * BUG4879
     * 对中签返款数据进行修改时，应该加载出相关联的申购交易数据中的申购金额。
     */
    private String getCashAccName(String CashAccCode )throws YssException{
    	String strSql = "";
        ResultSet rs = null;
        String CashAccName = "";
        try{
        	strSql = "SELECT FCashAccCode, FCashAccName from "+ 
	          		 pub.yssGetTableName("Tb_Para_CashAccount")+
	                 " where FCheckState = 1 and FCashAccCode = '"+CashAccCode+"'";
	        rs = dbl.openResultSet(strSql);
        	if (rs.next()) {   
        		CashAccName = rs.getString("FCashAccName");
        	}
        }catch(Exception e){
        	throw new YssException("获取现金账户信息出错！", e);
        }finally{
        	dbl.closeResultSetFinal(rs);
        }
        return CashAccName;
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
        if (newIssueTradeBean == null) {
            newIssueTradeBean = new NewIssueTradeBean(pub);
        }
        newIssueTradeBean.parseRowStr(sRowStr);
        recycled = sRowStr;

    }

    private String getSqlWhereStr() throws YssException{
        String sResult = "";
    	//20111027 added by liubo.STORY #1285.  如果要浏览数据，则直接返回
    	//==============================
    	if(pub.isBrown()==true) 
		return " where 1=1";
    	//=============end=================
        if(newIssueTradeBean.getFilterType() != null){
            sResult = " WHERE 1=1";
            if(newIssueTradeBean.getIsOnlyColumn().equalsIgnoreCase("1") && pub.isBrown()==false){	//20111027 modified by liubo.STORY #1285.  如果要浏览数据，则直接返回
                sResult += " AND 1=2";
                return sResult;
            }
            if(newIssueTradeBean.getFilterType().getSecurityCode().length() > 0){
                sResult += " AND cs.FSecurityCode = " + dbl.sqlString(newIssueTradeBean.getFilterType().getSecurityCode());
            }
            if(newIssueTradeBean.getFilterType().getPortCode().length() > 0){
                sResult += " AND cs.FPortCode = " + dbl.sqlString(newIssueTradeBean.getFilterType().getPortCode());
            }
            if(newIssueTradeBean.getFilterType().getAttrClsCode().length() > 0){
                sResult += " AND cs.FAttrClsCode = " + dbl.sqlString(newIssueTradeBean.getFilterType().getAttrClsCode());
            }
            if(newIssueTradeBean.getFilterType().getInvMgrCode().length() > 0){
                sResult += " AND cs.FInvMgrCode = " + dbl.sqlString(newIssueTradeBean.getFilterType().getInvMgrCode());
            }
            if(newIssueTradeBean.getFilterType().getInvestType().length() > 0 &&
                !newIssueTradeBean.getFilterType().getInvestType().equalsIgnoreCase("all")){
                sResult += " AND cs.FInvestType = " + dbl.sqlString(newIssueTradeBean.getFilterType().getInvestType());
            }
            if(newIssueTradeBean.getFilterType().getSecurityType().length() > 0 &&
                !newIssueTradeBean.getFilterType().getSecurityType().equalsIgnoreCase("all")){
                sResult += " AND cs.FSecurityType = " + dbl.sqlString(newIssueTradeBean.getFilterType().getSecurityType());
            }
            if(newIssueTradeBean.getFilterType().getBargainDate() != null &&
                !YssFun.formatDate(newIssueTradeBean.getFilterType().getBargainDate()).equalsIgnoreCase("1901-01-01")){
                sResult += " AND cs.FBargainDate = " + dbl.sqlDate(newIssueTradeBean.getFilterType().getBargainDate());
            }
            if(newIssueTradeBean.getFilterType().getDirectBallot()==1)
            {
            	sResult += " AND cs.FDIRBALLOT = " + newIssueTradeBean.getFilterType().getDirectBallot();//add by  yeshenghong story3395 20130204 直接中签区分
            }
            
        }
        return sResult;
    }

    public String buildRowStr() throws YssException {
        return newIssueTradeBean.buildRowStr();
    }
    /**
     * BUG4879网下新股新债业务中的提示信息存在问题 
     */
    public String strBuildRow() throws YssException {
        return newIssueTradeBean.strBuildRow();
    }
    
    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try{
            if(sType.equalsIgnoreCase("getlockdays")){
                sResult = getLockDays();
            }
            /**shashijie 2012-6-11 BUG 4761 */
            else if (sType.equalsIgnoreCase("getTradeNumFormat")) {
            	sResult = getTradeNumFormat();
			}
			/**end*/
        } catch(Exception ex){
            throw new YssException(ex);
        }
        return sResult;
    }

    /**shashijie 2012-6-11 BUG 4761 */
	private String getTradeNumFormat() throws YssException {
		ResultSet rs = null;
		String str = "";
		try {
			String query = getStringSql();
			rs = dbl.openResultSet(query);
			if (rs.next()) {
				newIssueTradeBean.setAppTransDate(rs.getDate("FTRANSDATE"));
				newIssueTradeBean.setAppMoney(rs.getDouble("FMoney"));
				newIssueTradeBean.setAppCashAccCode(rs.getString("FCASHACCCODE"));
				newIssueTradeBean.setAppCashAccName(rs.getString("FSGcashAccName"));
			}
			str = newIssueTradeBean.buildRowStr();
		} catch (Exception e) {
			throw new YssException("获取申请数据出错！", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return str;
	}
	

	/**shashijie 2012-6-11 BUG 4761 */
	private String getStringSql() {
		String sql = "Select Cs.*, d.FSgCashAccName" +
				" From (Select a.*" +
				" From "+pub.yssGetTableName("Tb_Data_Newissuetrade")+" a" +
				" Where a.Ftradetypecode = '40'" +
				" And a.Finvesttype = "+dbl.sqlString(this.newIssueTradeBean.getInvestType())+
				" And a.Fsecuritytype = "+dbl.sqlString(this.newIssueTradeBean.getSecurityType())+
				" And a.Fbargaindate = "+dbl.sqlDate(this.newIssueTradeBean.getBargainDate())+
				" And a.FPortCode = "+dbl.sqlString(this.newIssueTradeBean.getPortCode())+
				" And a.FAttrClsCode = "+dbl.sqlString(this.newIssueTradeBean.getAttrClsCode())+
				" And a.FSecurityCode = "+dbl.sqlString(this.newIssueTradeBean.getSecurityCode())+
				" And a.FInvMgrCode = "+dbl.sqlString(this.newIssueTradeBean.getInvMgrCode())+
				" And a.Fcheckstate <> 2) Cs" +
				" Left Join (Select Fcashacccode, Fcashaccname As Fsgcashaccname" +
				" From "+pub.yssGetTableName("Tb_Para_Cashaccount")+
				" Where Fcheckstate = 1) d On Cs.Fcashacccode = d.Fcashacccode" +
				" Order By Cs.Fnum , Cs.Fcheckstate Desc";
		return sql;
	}

	/**
     * 计算锁定天数
     * @return String
     * @throws YssException
     */
    private String getLockDays() throws YssException{
        int iLockDays = 0;
        java.util.Date dBeginDate = newIssueTradeBean.getLockBeginDate();
        java.util.Date dEndDate = newIssueTradeBean.getLockEndDate();
        SecurityBean security;
        try {
            security = new SecurityBean();
            security.setYssPub(pub);
            security.setStrSecurityCode(newIssueTradeBean.getSecurityCode());
            security.getSetting();

            iLockDays = this.getSettingOper().workDateDiff(dBeginDate, dEndDate, security.getHolidaysCode(), 0);
        } catch (Exception ex) {
            throw new YssException("获取锁定天数出错！", ex);
        }
        return iLockDays + "";
    }

    public String getRecycled() {
        return recycled;
    }

    public NewIssueTradeBean getNewIssueTradeBean() {
        return newIssueTradeBean;
    }

    public void setRecycled(String recycled) {
        this.recycled = recycled;
    }

    public void setNewIssueTradeBean(NewIssueTradeBean newIssueTradeBean) {
        this.newIssueTradeBean = newIssueTradeBean;
    }
}
