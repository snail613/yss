package com.yss.main.operdata.futures;

import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.cashmanage.*;
import com.yss.main.dao.*;
import com.yss.main.funsetting.*;
import com.yss.main.operdata.futures.pojo.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

public class FutureFillBailAdmin
    extends BaseDataSettingBean implements IDataSetting {
    public FutureFillBailAdmin() {
        //FurtureFillBail =new FurtureFillBailBean();
        //FurtureFillBail.setYssPub(pub);
    }

    private FutureFillBailBean FurtureFillBail = null;
    private String sRecycled = ""; //处理回收站的数据 by leeyu 0923
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper,
                               pub.yssGetTableName("TB_Data_FuturesFillBail"),
                               "FNum",
                               FurtureFillBail.getSNum(),
                               FurtureFillBail.getSOldNum());
    }

    public String addSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        try {
            /*if(FurtureFillBail.getSNum().length()==0){
               String strNumDate = YssFun.formatDate(FurtureFillBail.
                                                     getDTransferDate(),
                                                     "yyyyMMdd");
               FurtureFillBail.setSNum(strNumDate +
                                       dbFun.getNextInnerCode(pub.yssGetTableName(
                     "Tb_Cash_SubTransfer"),
                     dbl.sqlRight("FNUM", 6), "000001",
                     " where FNum like 'C"
                     + strNumDate + "%'", 1));
               FurtureFillBail.setSNum("C" + FurtureFillBail.getSNum());
                      }*/
            FurtureFillBail.setSNum(addTransfer(FurtureFillBail.getSNum()));
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            strSql = "insert into " + pub.yssGetTableName("TB_Data_FuturesFillBail") +
                " (FNum,FCashInd,FCashPortCode,FBailPortCode,FCashAnalysisCode1,FCashAnalysisCode2,FCashAnalysisCode3,FTransferDate,FTransferTime," +
                "FCashAcctCode,FBailAcctCode,FBailAnalysisCode1,FBailAnalysisCode2,FBailAnalysisCode3,FBaseCuryRate,FPortCuryRate," +
                "FMoney,FBaseCuryMoney,FPortCuryMoney,FDesc,FTransDate,FCheckState,FCreator,FCreateTime) values(" + //增加字段FTransDate  edit by jc
                //" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                dbl.sqlString(FurtureFillBail.getSNum()) + "," +
                dbl.sqlString(FurtureFillBail.getSCashInd()) + "," +
                dbl.sqlString(FurtureFillBail.getSCashPortCode()) + "," +
                dbl.sqlString(FurtureFillBail.getSBailPortCode()) + "," +
                dbl.sqlString(FurtureFillBail.getSCashAnalysisCode1()) + "," +
                dbl.sqlString(FurtureFillBail.getSCashAnalysisCode2()) + "," +
                dbl.sqlString(FurtureFillBail.getSCashAnalysisCode3()) + "," +
                dbl.sqlDate(FurtureFillBail.getDTransferDate()) + "," +
                dbl.sqlString(FurtureFillBail.getSTransferTime()) + "," +
                dbl.sqlString(FurtureFillBail.getSCashAcctCode()) + "," +
                dbl.sqlString(FurtureFillBail.getSBailAcctCode()) + "," +
                dbl.sqlString(FurtureFillBail.getSBailAnalysisCode1()) + "," +
                dbl.sqlString(FurtureFillBail.getSBailAnalysisCode2()) + "," +
                dbl.sqlString(FurtureFillBail.getSBailAnalysisCode3()) + "," +
                FurtureFillBail.getDBaseCuryRate() + "," +
                FurtureFillBail.getDPortCuryRate() + "," +
                FurtureFillBail.getDMoney() + "," +
                FurtureFillBail.getDBaseCuryMoney() + "," +
                FurtureFillBail.getDPortCuryMoney() + "," +
                dbl.sqlString(FurtureFillBail.getSDesc()) + "," +
                dbl.sqlDate(FurtureFillBail.getDTransDate()) + "," + //增加字段FTransDate  edit by jc
                (pub.getSysCheckState() ? 0 : 1) + "," +
                /**shashijie 2011.05.23 BUG1890“用户可以审核自己的维护参数”选项不起作用 */
                //dbl.sqlString(pub.getSysCheckState() ? "' '" : FurtureFillBail.creatorCode) + "," +
                dbl.sqlString(FurtureFillBail.creatorCode) + "," +
                /**end*/
                dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) + ")";
            dbl.executeSql(strSql);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("新增期货保证金补交数据出错", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public String editSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        try {
            /*if(FurtureFillBail.getSNum().length()==0){
               String strNumDate = YssFun.formatDate(FurtureFillBail.
                                                     getDTransferDate(),
                                                     "yyyyMMdd");
               FurtureFillBail.setSNum(strNumDate +
                                       dbFun.getNextInnerCode(pub.yssGetTableName(
                     "Tb_Cash_SubTransfer"),
                     dbl.sqlRight("FNUM", 6), "000001",
                     " where FNum like 'C"
                     + strNumDate + "%'", 1));
               FurtureFillBail.setSNum("C" + FurtureFillBail.getSNum());
                      }*/
            FurtureFillBail.setSNum(addTransfer(FurtureFillBail.getSNum()));
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("TB_Data_FuturesFillBail") +
                " set " +
                "FNum=" + dbl.sqlString(FurtureFillBail.getSNum()) + "," +
                "FCashInd=" + dbl.sqlString(FurtureFillBail.getSCashInd()) + "," +
                "FCashPortCode=" + dbl.sqlString(FurtureFillBail.getSCashPortCode()) + "," +
                "FBailPortCode=" + dbl.sqlString(FurtureFillBail.getSBailPortCode()) + "," +
                "FCashAnalysisCode1=" + dbl.sqlString(FurtureFillBail.getSCashAnalysisCode1()) + "," +
                "FCashAnalysisCode2=" + dbl.sqlString(FurtureFillBail.getSCashAnalysisCode2()) + "," +
                "FCashAnalysisCode3=" + dbl.sqlString(FurtureFillBail.getSCashAnalysisCode3()) + "," +
                "FTransferDate=" + dbl.sqlDate(FurtureFillBail.getDTransferDate()) + "," +
                "FTransferTime=" + dbl.sqlString(FurtureFillBail.getSTransferTime()) + "," +
                "FCashAcctCode=" + dbl.sqlString(FurtureFillBail.getSCashAcctCode()) + "," +
                "FBailAcctCode=" + dbl.sqlString(FurtureFillBail.getSBailAcctCode()) + "," +
                "FBailAnalysisCode1=" + dbl.sqlString(FurtureFillBail.getSBailAnalysisCode1()) + "," +
                "FBailAnalysisCode2=" + dbl.sqlString(FurtureFillBail.getSBailAnalysisCode2()) + "," +
                "FBailAnalysisCode3=" + dbl.sqlString(FurtureFillBail.getSBailAnalysisCode3()) + "," +
                "FBaseCuryRate=" + FurtureFillBail.getDBaseCuryRate() + "," +
                "FPortCuryRate=" + FurtureFillBail.getDPortCuryRate() + "," +
                "FMoney=" + FurtureFillBail.getDMoney() + "," +
                "FBaseCuryMoney=" + FurtureFillBail.getDBaseCuryMoney() + "," +
                "FPortCuryMoney=" + FurtureFillBail.getDPortCuryMoney() + "," +
                "FDesc=" + dbl.sqlString(FurtureFillBail.getSDesc()) + "," +
                "FTransDate=" + dbl.sqlDate(FurtureFillBail.getDTransDate()) + "," + //增加字段FTransDate  edit by jc
                "FCreateTime=" + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                " where FNum=" + dbl.sqlString(FurtureFillBail.getSOldNum());
            dbl.executeSql(strSql);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("修改期货保证金补交数据出错", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    public void delSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        TransferBean trans = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            strSql = "update " + pub.yssGetTableName("TB_Data_FuturesFillBail") +
                " set FCheckState=" + FurtureFillBail.checkStateId +
                " where FNum=" + dbl.sqlString(FurtureFillBail.getSOldNum());
            dbl.executeSql(strSql);
            trans = new TransferBean();
            trans.setYssPub(pub);
            trans.setStrNum(FurtureFillBail.getSOldNum());
            trans.setCheckStateId(FurtureFillBail.checkStateId);
            trans.delSetting();
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("删除保证金补交数据出错", ex);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }

    //修改方法,添加回收站功能　by leeyu 0923
    public void checkSetting() throws YssException {
        String strSql = "";
        boolean bTrans = false;
        Connection conn = null;
        TransferBean trans = null;
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            arrData = sRecycled.split("\r\n");
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                strSql = "update " + pub.yssGetTableName("TB_Data_FuturesFillBail") +
                    " set FCheckState=" + FurtureFillBail.checkStateId + "," +
                    " FCheckUser=" + (pub.getSysCheckState() ? "' '" : FurtureFillBail.checkUserCode) + "," +
                    " FCheckTime=" + dbl.sqlString(YssFun.formatDatetime(new java.util.Date())) +
                    " where FNum=" + dbl.sqlString(FurtureFillBail.getSNum());
                dbl.executeSql(strSql);
                trans = new TransferBean();
                trans.setYssPub(pub);
                trans.setStrNum(FurtureFillBail.getSNum());
                trans.setCheckStateId(FurtureFillBail.checkStateId);
                trans.checkSetting();
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("审核保证金补交数据出错", ex);
        } finally {
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

    //添加回收站功能　by leeyu 0923
    public void deleteRecycleData() throws YssException {
        Connection conn = null;
        boolean bTrans = false;
        String sqlStr = "";
        String[] arrData = null;
        try {
            conn = dbl.loadConnection();
            arrData = sRecycled.split("\r\n");
            conn.setAutoCommit(bTrans);
            bTrans = true;
            for (int i = 0; i < arrData.length; i++) {
                if (arrData[i].length() == 0) {
                    continue;
                }
                this.parseRowStr(arrData[i]);
                sqlStr = "delete from " + pub.yssGetTableName("TB_Data_FuturesFillBail") +
                    " where FNum=" + dbl.sqlString(FurtureFillBail.getSNum());
                dbl.executeSql(sqlStr);
                sqlStr = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FNum=" + dbl.sqlString(FurtureFillBail.getSNum());
                dbl.executeSql(sqlStr);
                sqlStr = "delete from " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum=" + dbl.sqlString(FurtureFillBail.getSNum());
                dbl.executeSql(sqlStr);
            }
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("删除期货补交数据表及关联表数据出错!");
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

    public String getListViewData1() throws YssException {
        String strSql = "";
        ResultSet rs = null;
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String sVocStr = "";
        String sCashAnalysis = "", sBailAnalysis = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        try {
            if (FurtureFillBail == null) {
                FurtureFillBail = new FutureFillBailBean();
                FurtureFillBail.setYssPub(pub);
            }
            sCashAnalysis = getCashStorageAnalysisSql(YssOperCons.YSS_KCLX_Cash, "Cash");
            sBailAnalysis = getCashStorageAnalysisSql(YssOperCons.YSS_KCLX_Cash, "Bail");
            sHeader = this.getListView1Headers();
          //fanghaoln MS01310 QDV4赢时胜(测试)2010年06月18日01_A  20100708
            //优化加载菜单的时候不去进行分页的视图的查询加快打开菜单的速度
            if (FurtureFillBail.isBShow() == false) {
            	VocabularyBean vocabulary = new VocabularyBean();
                vocabulary.setYssPub(pub);
                sVocStr = vocabulary.getVoc(YssCons.YSS_FURTURE_CASHINOUT);
                return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                    this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\fvoc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            }
            //--------------------------------------end MS01310--------------------------------------------------------
            strSql = "select a.*,cport.FPortName as FCashPortName,bport.FPortName as FBailPortName,fiv.FVocName as FCashIndName," +
                " cc.FCashAccName as FCashAcctName,bc.FCashAccName as FBailAcctName," +
                /**shashijie 2011.05.23 BUG1890“用户可以审核自己的维护参数”选项不起作用*/
                " b.FUserName as FCreatorName, c.FUserName as FCheckUserName " +
                /**end*/
                ( (sCashAnalysis.trim().length() == 0) ?
                 ", ' ' as FCashAnalysisName1, ' ' as FCashAnalysisName2, ' ' as FCashAnalysisName3 " :
                 ", FCashAnalysisName1 as FCashAnalysisName1, FCashAnalysisName2 as FCashAnalysisName2, FCashAnalysisName3 as FCashAnalysisName3 ") +
                ( (sBailAnalysis.trim().length() == 0) ?
                 ", ' ' as FBailAnalysisName1, ' ' as FBailAnalysisName2, ' ' as FBailAnalysisName3 " :
                 ", FBailAnalysisName1 as FBailAnalysisName1, FBailAnalysisName2 as FBailAnalysisName2, FBailAnalysisName3 as FBailAnalysisName3 ") +
                " from " + pub.yssGetTableName("TB_Data_FuturesFillBail") + " a " +
                
                //------ modify by wangzuochun  2010.08.21  MS01601    期货保证金补交页面，筛选和新建有问题    QDV4赢时胜(测试)2010年08月12日02_B     
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate, FPortCury from " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") + 
                " where FCheckState = 1) cport on a.FCashPortCode = cport.FPortCode " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_Portfolio") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " select FPortCode, FPortName, FStartDate, FPortCury from " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_Portfolio") + 
                " where FCheckState = 1) bport on a.FBailPortCode = bport.FPortCode " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                
                
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) cc on a.FCashAcctCode = cc.FCashAccCode " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                
                " left join (" +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
//                pub.yssGetTableName("Tb_Para_CashAccount") +
//                " where FStartDate <= " + dbl.sqlDate(new java.util.Date()) +
                //----delete by songjie 2011.03.15 不以最大的启用日期查询数据----//
                " select FCashAccCode, FCashAccName, FStartDate from " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                pub.yssGetTableName("Tb_Para_CashAccount") +
                " where FCheckState = 1) bc on a.FBailAcctCode = bc.FCashAccCode " +//edit by songjie 2011.03.15 不以最大的启用日期查询数据
                //-------------------------------------------- MS01601 -------------------------------------------//
                
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) b on a.FCreator = b.FUserCode" +
                " left join (select FUserCode,FUserName from Tb_Sys_UserList) c on a.FCheckUser = c.FUserCode" +
                " left join Tb_Fun_Vocabulary fiv on a.FCashInd = fiv.FVocCode and fiv.FVocTypeCode = " + dbl.sqlString(YssCons.YSS_FURTURE_CASHINOUT) +
                sCashAnalysis + " " + sBailAnalysis +
                filterSql() +
                " order by a.FCheckTime desc, a.FCreateTime desc";
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            //rs = dbl.openResultSet(strSql);
            yssPageInationBean.setsQuerySQL(strSql);
            yssPageInationBean.setsTableName("FuturesFillBail");
            rs =dbl.openResultSet(yssPageInationBean);
            //QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
            while (rs.next()) {
                bufShow.append(this.buildRowShowStr(rs, this.getListView1ShowCols())).
                    append(YssCons.YSS_LINESPLITMARK);
                FurtureFillBail.setFurtureFillBailAttr(rs);
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
            sVocStr = vocabulary.getVoc(YssCons.YSS_FURTURE_CASHINOUT);
            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + yssPageInationBean.buildRowStr()+ "\r\fvoc" + sVocStr;//QDV4赢时胜上海2010年03月16日06_B MS00884 by xuqiji
        } catch (Exception ex) {
            throw new YssException("获取股指期货补交数据出错");
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.closeStatementFinal(dbl.getProcStmt());
        }
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

    public String getBeforeEditData() throws YssException {
        return "";
    }

    public void parseRowStr(String sRowStr) throws YssException {
        if (FurtureFillBail == null) {
            FurtureFillBail = new FutureFillBailBean();
            FurtureFillBail.setYssPub(pub);
        }
        FurtureFillBail.parseRowStr(sRowStr);
        sRecycled = sRowStr;
    }

    public String buildRowStr() throws YssException {
        /*StringBuffer buf =new StringBuffer();
               buf.append(FurtureFillBail.getSCashInd()).append(YssCons.YSS_ITEMSPLITMARK1);;
               buf.append(FurtureFillBail.getSCashIndName()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashPortCode()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashPortName()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailPortCode()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailPortName()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAnalysisCode1()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAnalysisName1()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAnalysisCode2()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAnalysisName2()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAnalysisCode3()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAnalysisName3()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getDTransferDate()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSTransferTime()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAcctCode()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSCashAcctName()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAcctCode()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAcctName()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAnalysisCode1()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAnalysisName1()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAnalysisCode2()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAnalysisName2()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAnalysisCode3()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSBailAnalysisName3()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getDBaseCuryRate()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getDPortCuryRate()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getDMoney()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getDBaseCuryMoney()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getDPortCuryMoney()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSDesc()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.getSNum()).append(YssCons.YSS_ITEMSPLITMARK1);
               buf.append(FurtureFillBail.buildRecLog());
               return buf.toString();*/
        return FurtureFillBail.buildRowStr();
    }

    public String getOperValue(String sType) throws YssException {
        return "";
    }

    private String filterSql() throws YssException {
        String sFilterSql = "";
        if (FurtureFillBail.getFilterType() != null) {
            sFilterSql = " where 1=1 ";
            if (FurtureFillBail.isBShow() == false) {
                sFilterSql += " and 1=2 ";
            }
            if (FurtureFillBail.getFilterType().getSCashAcctCode() != null &&
                FurtureFillBail.getFilterType().getSCashAcctCode().length() > 0) {
                sFilterSql += " and a.FCashAcctCode like '" + FurtureFillBail.getFilterType().getSCashAcctCode().replaceAll("'", "''") + "%'";
            }
            if (FurtureFillBail.getFilterType().getSBailAcctCode() != null &&
                FurtureFillBail.getFilterType().getSBailAcctCode().length() > 0) {
                sFilterSql += " and a.FBailAcctCode like '" + FurtureFillBail.getFilterType().getSBailAcctCode().replaceAll("'", "''") + "%'";
            }
            if (FurtureFillBail.getFilterType().getSCashPortCode() != null &&
                FurtureFillBail.getFilterType().getSCashPortCode().length() > 0) {
                sFilterSql += " and a.FCashPortCode like '" + FurtureFillBail.getFilterType().getSCashPortCode().replaceAll("'", "''") + "%'";
            }
            if (FurtureFillBail.getFilterType().getSBailPortCode() != null &&
                FurtureFillBail.getFilterType().getSBailPortCode().length() > 0) {
                sFilterSql += " and a.FBailPortCode like '" + FurtureFillBail.getFilterType().getSBailPortCode().replaceAll("'", "''") + "%'";
            }
            if (FurtureFillBail.getFilterType().getDTransferDate() != null &&
                !FurtureFillBail.getFilterType().getDTransferDate().equals(YssFun.toDate("9998-12-31"))) {
                sFilterSql += " and a.FTransferDate =" + dbl.sqlDate(FurtureFillBail.getFilterType().getDTransferDate());
            }
            //edit by jc 添加业务日期为查询条件
            if (FurtureFillBail.getFilterType().getDTransDate() != null &&
                !FurtureFillBail.getFilterType().getDTransDate().equals(YssFun.toDate("9998-12-31"))) {
                sFilterSql += " and a.FTransDate =" + dbl.sqlDate(FurtureFillBail.getFilterType().getDTransDate());
            }
            //-------------jc
        }
        return sFilterSql;
    }

    /**
     * 获取辅助字段之查询Sql语句
     * @return String
     */
    private String getCashStorageAnalysisSql(String sType, String sAssistant) throws YssException, SQLException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        strSql = "select FAnalysisCode1,FAnalysisCode2,FAnalysisCode3 from " +
            pub.yssGetTableName("Tb_Para_StorageCfg") +
            " where FCheckState = 1 and FStorageType = " +
            dbl.sqlString(sType);
        rs = dbl.openResultSet(strSql);
        if (rs.next()) {
            for (int i = 1; i <= 3; i++) {
                if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                    rs.getString("FAnalysisCode" + String.valueOf(i)).
                    equalsIgnoreCase("002")) {
                    sResult = sResult +
                        " left join (select y.FBrokerCode ,y.FBrokerName  as F" + sAssistant + "AnalysisName" +
                        i +
                        " from  (select FBrokerCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_broker") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FBrokerCode )x " +
                        " join (select * from " +
                        pub.yssGetTableName("tb_para_broker") + ") y on x.FBrokerCode = y.FBrokerCode and x.FStartDate = y.FStartDate) broker" + sAssistant + " on a.F" +
                        sAssistant + "AnalysisCode" +
                        i + " = broker" + sAssistant + ".FBrokerCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("003")) {
                    sResult = sResult +
                        " left join (select FExchangeCode,FExchangeName as F" + sAssistant + "AnalysisName" +
                        i +
                        " from tb_base_exchange) e on a.F" + sAssistant + "AnalysisCode" + i +
                        " = e.FExchangeCode " +
                     // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                  
                        
                        " left join (select n.FInvMgrCode ,n.FInvMgrName as FAnalysisName3 from  " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " n where  n.FCheckState = 1 ) exchange" + sAssistant + " on a.F" +
                        sAssistant + "AnalysisCode" +
                        i + " = exchange" + sAssistant + ".FInvMgrCode";
                    
                    //end by lidaolong
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("004")) {
                    sResult = sResult +
                        " left join (select FCatCode,FCatName as F" + sAssistant + "AnalysisName" + i + " from Tb_Base_Category where FCheckState = 1) category" + sAssistant +
                        " on a.F" + sAssistant + "AnalysisCode" +
                        i + " = category" + sAssistant + ".FCatCode";
                } else if (rs.getString("FAnalysisCode" + String.valueOf(i)) != null &&
                           rs.getString("FAnalysisCode" + String.valueOf(i)).
                           equalsIgnoreCase("001")) {
                    sResult = sResult +
                 // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                      /*  " left join (select n.FInvMgrCode ,n.FInvMgrName as F" + sAssistant + "AnalysisName" +
                        i +
                        "  from  (select FInvMgrCode,max(FStartDate) as FStartDate  from " +
                        pub.yssGetTableName("tb_para_investmanager") +
                        " where FStartDate < " +
                        dbl.sqlDate(new java.util.Date()) +
                        " and FCheckState = 1 group by FInvMgrCode )m " +
                        "join (select * from " +
                        pub.yssGetTableName("tb_para_investmanager") + ") n on m.FInvMgrCode = n.FInvMgrCode and m.FStartDate = n.FStartDate) invmgr" + sAssistant + " on a.F" +
                        sAssistant + "AnalysisCode" +
                        i + " = invmgr" + sAssistant + ".FInvMgrCode ";
                        */
                    
                    " left join (select n.FInvMgrCode ,n.FInvMgrName as F" + sAssistant + "AnalysisName" +
                    i +
                    "  from " +
                    pub.yssGetTableName("tb_para_investmanager") +
                    " n where  n.FCheckState = 1 ) invmgr" + sAssistant + " on a.F" +
                    sAssistant + "AnalysisCode" +
                    i + " = invmgr" + sAssistant + ".FInvMgrCode ";
                    
                    
                    //end by lidaolong
                }

                else {
                    sResult = sResult +
                        " left join (select ' ' as FAnalysisNull , ' ' as F" + sAssistant + "AnalysisName" +//调整为有空格的字段，防止在创建分页表时报错 by leeyu 20100813 合并太平版本时调整
                        i + " from  " +
                        pub.yssGetTableName("Tb_Para_StorageCfg") +
                        " where 1=2) " + sAssistant + i + " on a.F" + sAssistant + "AnalysisCode" + i + " = " + sAssistant +
                        i + ".FAnalysisNull ";
                }
            }
        }
        dbl.closeResultSetFinal(rs);//QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 add by songjie 2009-07-09 关闭结果集
        return sResult;
    }

    /***
     * 新增产生资金调拨方法
     */
    private String addTransfer(String sNum) throws YssException {
        String sqlStr = "";
        Connection conn = null;
        boolean bTrans = false;
        try {
            conn = dbl.loadConnection();
            conn.setAutoCommit(bTrans);
            bTrans = true;
            TransferBean tran = new TransferBean();
            TransferSetBean transfersetIn = new TransferSetBean();
            TransferSetBean transfersetOut = new TransferSetBean();
            sqlStr = " delete from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " where FNum =" + dbl.sqlString(sNum);
            dbl.executeSql(sqlStr);
            sqlStr = " delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where  FNum=" + dbl.sqlString(sNum);
            dbl.executeSql(sqlStr);
            conn.commit();
            conn.setAutoCommit(bTrans);
            bTrans = false;
            PortfolioBean bPort = new PortfolioBean();
            bPort.setYssPub(pub);
            bPort.setPortCode(FurtureFillBail.getSCashPortCode());
            bPort.getSetting();
            PortfolioBean sPort = new PortfolioBean();
            sPort.setYssPub(pub);
            sPort.setPortCode(FurtureFillBail.getSBailPortCode());
            sPort.getSetting();
            tran.setYssPub(pub);
            transfersetIn.setYssPub(pub);
            transfersetOut.setYssPub(pub);
            tran.setDtTransDate(FurtureFillBail.getDTransDate()); //业务日期  edit by jc
            tran.setDtTransferDate(FurtureFillBail.getDTransferDate()); //调拨日期
            tran.setStrTsfTypeCode(YssOperCons.YSS_ZJDBLX_InnerAccount);
            tran.setStrSubTsfTypeCode(YssOperCons.YSS_ZJDBZLX_COST_FACT);
            tran.setStrTransferTime(FurtureFillBail.getSTransferTime());
            tran.setDataSource(0); //这里应为手工标记为0 by leeyu BUG:MS00020 2008-11-24
            tran.setFRelaNum(""); //这里去掉空格。因为库存统计时不能正确的处理资金调拨 by leeyu 20090622
            tran.setFNumType(""); //QDV4中金2009年6月17日03_B MS00519 by leeyu 20090622
            tran.setStrDesc("");
            tran.checkStateId = 0;
            tran.creatorTime = YssFun.formatDate(new java.util.Date(),
                                                 "yyyyMMdd HH:mm:ss");
            tran.setDataSource(0); //这里应为手工标记为0 by leeyu BUG:MS00020 2008-11-24
            tran.addSetting();
            //资金流入帐户
            transfersetIn.setSNum(tran.getStrNum());
            transfersetIn.setSSubNum("00001");
            transfersetIn.setDMoney(FurtureFillBail.getDMoney());
            transfersetIn.setSPortCode(FurtureFillBail.getSCashPortCode());
            transfersetIn.setSAnalysisCode1(FurtureFillBail.getSCashAnalysisCode1());
            transfersetIn.setSAnalysisCode2(FurtureFillBail.getSCashAnalysisCode2());
            transfersetIn.setSAnalysisCode3(FurtureFillBail.getSCashAnalysisCode3());
            transfersetIn.setDBaseRate(FurtureFillBail.getDBaseCuryRate());
            transfersetIn.setDPortRate(FurtureFillBail.getDPortCuryRate());
            transfersetIn.setSCashAccCode(FurtureFillBail.getSCashAcctCode()); //改为流入帐户
            transfersetIn.setIInOut(YssFun.toInt(FurtureFillBail.getSCashInd()) == 1 ? -1 : 1);
            transfersetIn.setSDesc("");
            transfersetIn.checkStateId = 0;
            transfersetIn.addSetting(true);

            //资金流出帐户
            transfersetOut.setSNum(tran.getStrNum());
            transfersetOut.setSSubNum("00002");
            transfersetOut.setDMoney(FurtureFillBail.getDMoney());
            transfersetOut.setSPortCode(FurtureFillBail.getSBailPortCode());
            transfersetOut.setSAnalysisCode1(FurtureFillBail.getSBailAnalysisCode1());
            transfersetOut.setSAnalysisCode2(FurtureFillBail.getSBailAnalysisCode2());
            transfersetOut.setSAnalysisCode3(FurtureFillBail.getSBailAnalysisCode3());
            transfersetOut.setDPortRate(FurtureFillBail.getDPortCuryRate()); //by leeyu 修改汇率的取值　0924
            transfersetOut.setDBaseRate(FurtureFillBail.getDBaseCuryRate());
            transfersetOut.setSCashAccCode(FurtureFillBail.getSBailAcctCode());
            transfersetOut.setIInOut(YssFun.toInt(FurtureFillBail.getSCashInd()) == 1 ? 1 : -1);
            transfersetOut.setSDesc("");
            transfersetOut.checkStateId = 0;
            transfersetOut.addSetting(false);
            return tran.getStrNum();
        } catch (Exception e) {
            throw new YssException("生成资金调拨出错");
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

}
