package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.cashmanage.*;
import com.yss.util.*;

public class CashTransAdmin
    extends BaseBean {
    ArrayList addList = new ArrayList();
    ArrayList subaddList = new ArrayList();
    ArrayList tempList = null; //临时arrayList 用于设置在插入子循环中所用的ArrayList的对象。sj add 20080124
    private String transNum = "";
    //-----存放关联编号的容器.MS00141 QDV4交银施罗德2009年01月4日02_B sj modified-------//
    private Hashtable relaOrderNum = null;
    private HashMap taMap = new HashMap();
    public void setRelaOrderNum(Hashtable hRelaOrderNum) {
        relaOrderNum = hRelaOrderNum;
    }

    //-----------------------------------------------------------------------------//
    public void setTransNum(String transNum) {
        this.transNum = transNum;
    }

    public String getTransNum() {
        return transNum;
    }

    public CashTransAdmin() {
    }

    //这个方法是根据编号和编号类型来获得资金调拨编号 以便通过资金调拨编号去删掉资金调拨信息 by sunny
    public String getTransNums(String sRelaNum, String sType) throws
        YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        try {
            strSql = "select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FRelaNum="
                + dbl.sqlString(sRelaNum) + " and FNumType=" +
                dbl.sqlString(sType);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sResult += rs.getString("FNum") + ",";
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }

    }

    public String getTransNums(String sTradeNum, String sSavingNum,
                               String sCprNum) throws YssException {
        return getTransNums(sTradeNum, sSavingNum, sCprNum, "", "", "");
    }

    public String getTransNums(String sTradeNum, String sSavingNum,
                               String sCprNum, String sRateTradeNum,
                               String sNumType, String sRelaNum) throws
        YssException {
        String sResult = "";
        String strSql = "";
        ResultSet rs = null;
        try {
            strSql = "select FNum from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                this.buildWhereSql("", null, null, "",
                                   "", sTradeNum, sSavingNum, sCprNum, "",
                                   sRateTradeNum, sNumType, "",
                                   -1, "", "", 0, "",
                                   "", "", sRelaNum);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                //一条交易记录可能存在多条资金调拨数据所以用''进行处理，后面可以采用in来查找记录fazmm20071108
                sResult += "'" + rs.getString("FNum") + "',";
            }
            return sResult;
        } catch (Exception e) {
            throw new YssException(e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    //根据编号 来进行审核 和反审核资金调拨信息
    public void check(int checkStateId, String sNum) throws YssException {
        String strSql = "";
        boolean bTrans = false; //代表是否开始了事务
        Connection conn = dbl.loadConnection();

        try {

            strSql = "update " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " set FCheckState = " + checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                " where FNum in(" + sNum + ")"; //sNum 中已经带了''，所以不再用dbl.sqlString()处理 fazmm20071108

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);

            //审核调拨子表
            strSql = "update " + pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " set FCheckState = " +
                checkStateId + ", FCheckUser = " +
                dbl.sqlString(pub.getUserCode()) +
                ",FCheckTime = '" +
                YssFun.formatDatetime(new java.util.Date()) + "'" +
                "where FNum in(" + sNum + ")"; //sNum 中已经带了''，所以不再用dbl.sqlString()处理 fazmm20071108
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (Exception e) {
            throw new YssException("审核资金调拨信息出错\r\n" + e.getMessage(), e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public void addList(TransferBean transfer, TransferSetBean transferset) {
        this.addList.add(transfer);
        this.subaddList.add(transferset);
    }

    public void addList(TransferBean transfer, ArrayList transfersets) {
        this.addList.add(transfer);
        this.subaddList.addAll(transfersets);
    }
    //add  by yeshenghong to solve BUG5490 20120911
    public void addTaTradeList(TransferBean transfer, TransferSetBean transferset) throws YssException {
		String sFNum = getNum(transfer);
		transfer.setStrNum(sFNum);
		this.addList.add(transfer);
        this.taMap.put(sFNum, transferset);
    }

    public void addList(TransferBean transfer) { //子资金调拨的值放入TransferBean中的arrayList中 sj add 20080123
        this.addList.add(transfer);
    }

    public void addList(ArrayList list) {
        addList = list;
    }

    public String buildWhereSql(String sNums, java.util.Date transferDate,
                                java.util.Date transDate,
                                String sTsfTypeCode, String sSubTsfTypeCode,
                                String sTradeNum, String sSavingNum,
                                String sCprNum, String FIPRNum,
                                String sRateTradeNum,
                                String sNumType, String sSecurityCode,
                                int iDsInd, String sCashAccCode,
                                String sPortCode, int iInOut,
                                String sAnalysisCode1, String sAnalysisCode2,
                                String sAnalysisCode3, String sRelaNum) {
        String sResult = " where 1=1 ";
        if (sNums.length() > 0) {
            sResult += " and a.FNum in(" + operSql.sqlCodes(sNums) + ")";
        }
        if (transferDate != null) {
            sResult += " and FTransferDate = " + dbl.sqlDate(transferDate);
        }
        if (transDate != null) {
            sResult += " and FTransDate = " + dbl.sqlDate(transDate);
        }
        if (sTsfTypeCode != null && sTsfTypeCode.length() > 0) {
            if (sTsfTypeCode.indexOf(",") > 0) {
                sResult += " and FTsfTypeCode in (" +
                    operSql.sqlCodes(sTsfTypeCode) +
                    ")";
            } else {
                sResult += " and FTsfTypeCode = " + dbl.sqlString(sTsfTypeCode);
            }
        }
        if (sSubTsfTypeCode != null && sSubTsfTypeCode.length() > 0) {
            if (sSubTsfTypeCode.indexOf(",") > 0) {
                sResult += " and FSubTsfTypeCode in (" +
                    operSql.sqlCodes(sSubTsfTypeCode) + ")";
            } else if (sSubTsfTypeCode.indexOf("%") > 0) { //加入like的处理 20070918 胡昆
                sResult += " and FSubTsfTypeCode like " +
                    dbl.sqlString(sSubTsfTypeCode);
            } else {
                sResult += " and FSubTsfTypeCode = " +
                    dbl.sqlString(sSubTsfTypeCode);
            }
        }

//      if (sTsfTypeCode.length() > 0) {
//         sResult += " and FTsfTypeCode = " + dbl.sqlString(sTsfTypeCode);
//      }
//      if (sSubTsfTypeCode.length() > 0) {
//         sResult += " and FSubTsfTypeCode = " + dbl.sqlString(sSubTsfTypeCode);
//      }
        if (sTradeNum.length() > 0) {
            sResult += " and FTradeNum = " + dbl.sqlString(sTradeNum);
        }
        if (sSavingNum.length() > 0) {
            sResult += " and FSavingNum in (" + operSql.sqlCodes(sSavingNum) +
                ")";
        }
        if (sCprNum.length() > 0) {
            sResult += " and FCPRNum in (" + operSql.sqlCodes(sCprNum) + ")";
        }
        if (FIPRNum.length() > 0) {
            sResult += " and FIPRNum in (" + operSql.sqlCodes(FIPRNum) + ")";
        }
        if (sNumType.length() > 0) {
            sResult += " and FNumType in (" + operSql.sqlCodes(sNumType) + ")";
        }

        if (sRateTradeNum.length() > 0) {
            sResult += " and FRateTradeNum in (" +
                operSql.sqlCodes(sRateTradeNum) +
                ")";
        }
        if (sSecurityCode.length() > 0) {
            sResult += " and FSecurityCode in (" +
                operSql.sqlCodes(sSecurityCode) +
                ")";
        }
        if (iDsInd > -1) {
            sResult += " and FDataSource = " + iDsInd;
        }
        if (sCashAccCode.length() > 0) {
            //需要将现金账户代码转换，例如 001,002转换成'001','002'    邱健 20080905 修改
            //sResult += " and FCashAccCode in " + dbl.sqlString(sCashAccCode);
            sResult += " and FCashAccCode in (" + operSql.sqlCodes(sCashAccCode) +
                ")";
        }
        if (sPortCode.length() > 0) {
            sResult += " and FPortCode in( " + operSql.sqlCodes(sPortCode) + ")";//xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
        }
        if (iInOut != 0) {
            sResult += " and FInOut = " + iInOut;
        }
        if (sAnalysisCode1.length() > 0) {
            sResult += " and FAnalysisCode1 = " + dbl.sqlString(sAnalysisCode1);
        }
        if (sAnalysisCode2.length() > 0) {
            sResult += " and FAnalysisCode2 = " + dbl.sqlString(sAnalysisCode2);
        }
        if (sAnalysisCode3.length() > 0) {
            sResult += " and FAnalysisCode3 = " + dbl.sqlString(sAnalysisCode3);
        }
        if (sRelaNum.length() > 0) { 
            /**add---huhuichao 2013-10-31 BUG  82531  期货保证金调整问题（列表中的最大表达式数为 1000）*/
            sResult += " and ( FRelaNum in ( " + this.buildCondition(sRelaNum) + " ))";
			/**end---huhuichao 2013-10-31 BUG  82531 */
        }
        return sResult;
    }

    /**
     * add---huhuichao 2013-10-31 BUG  82531  期货保证金调整问题（列表中的最大表达式数为 1000）
     * 格式化条件
     * @param sRelaNum String
     */
	public String buildCondition(String sRelaNum)  {
		StringBuffer sb = new StringBuffer();
		String[] sRelaNumber = sRelaNum.split(",");
		int inNum = 1; // 已拼装IN条件数量
				for (int i = 0; i < sRelaNumber.length; i++) {
					if (sRelaNumber[i] == null && sRelaNumber[i].length() == 0)
						continue;// sRelaNumber[i]为空，则进行下一个
					if (i == (sRelaNumber.length - 1))
						sb.append(operSql.sqlCodes(sRelaNumber[i])); // SQL拼装，最后一条不加“,”。
					else if (inNum == 1000 && i > 0) {
						sb.append(operSql.sqlCodes(sRelaNumber[i]) + " ) OR FRelaNum IN ( "); // 解决ORA-01795问题
						inNum = 1;
					} else {
						sb.append(operSql.sqlCodes(sRelaNumber[i]) + ", ");
						inNum++;
					}
				}
		return sb.toString();
	}
    
    public void pInsert(java.util.Date transferDate, java.util.Date transDate,
                        String tsfType,
                        String subTsfType, int datasource
        ) throws
        YssException {
        String strSql = "";
        int i = 0;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        PreparedStatement pstSub = null;
        Connection conn = dbl.loadConnection();
        String sFNum = "";
        String sTmpNum = "";
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        ArrayList transfersets = null;
//        ResultSet rs = null;
        int iFNum = 0;
        HashMap htDiffDate = new HashMap(); //根据不同交易日期存放不同的编号 by leeyu
        try {
            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " (FNum,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FTransferDate,FTransferTime,FTransDate,FTradeNum,FRateTradeNum," +
                "FSecurityCode,FDataSource,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FSavingNum,FCPRNum)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            strSql = "insert into " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " (FNum,FSubNum,FInOut,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FMoney," +
                "FBaseCuryRate,FPortCuryRate,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDesc)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstSub = conn.prepareStatement(strSql);
            for (i = 0; i < this.addList.size(); i++) {//modified by yeshenghong 20120725 story2633
                transfer = (TransferBean) addList.get(i);
                transfersets = (ArrayList) subaddList.get(i);
                sFNum = getNum(transfer);
//                if (i == 0) {
//                    sFNum = getNum(transfer); //只取一次
//                    htDiffDate.put(transfer.getDtTransferDate(), sFNum);
//                }
//                if (htDiffDate.get(transfer.getDtTransferDate()) == null) {
//                    //如果业务日期不同的话需要再取一次 by leeyu 080616
//                    sFNum = getNum(transfer);
//                    htDiffDate.put(transfer.getDtTransferDate(), sFNum);
//                }
                /*  strSql = "select Fnum from (select a. *, b.FPortCode, b.FAnalysisCode1, b.FAnalysisCode2,FCashAccCode from " +
                 " (select * from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                        " where FCheckState = 1) a join (select * from " +
                        pub.yssGetTableName("Tb_Cash_SubTransfer") +
                        " where FCheckState = 1) b on a.FNum = b.FNum) " +
                        " where FTsfTypeCode = " + dbl.sqlString(tsfType) +
                        " and FSubTsfTypeCode = " +
                        dbl.sqlString(subTsfType) +
                        " and FTransferDate = " + dbl.sqlDate(transferDate) +
                        " and FTransDate = " + dbl.sqlDate(transDate) +
                 " and FDataSource = " + datasource + " and FCheckState = 1"  +
                 " and FPortCode = " + dbl.sqlString(transferset.getSPortCode()) +
                        " and FAnalysisCode1 = " +
                        dbl.sqlString(transferset.getSAnalysisCode1()) +
                        " and FAnalysisCode2 = " +
                        dbl.sqlString(transferset.getSAnalysisCode2()) +
                        " and FCashAccCode = " +
                        dbl.sqlString(transferset.getSCashAccCode());

                  rs = dbl.openResultSet(strSql);
                  if (rs.next()) {
                     sTmpNum = rs.getString("FNum");
                  }
                  if (rs != null) {
                     dbl.closeResultSetFinal(rs);
                  }*/

//                strSql = "delete from " +
//                    pub.yssGetTableName("Tb_Cash_Transfer") +
//                    " where FNum = " + dbl.sqlString(sTmpNum);
//                dbl.executeSql(strSql);
//
//                strSql = "delete from " +
//                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
//                    " where FNum = " + dbl.sqlString(sTmpNum);
//                dbl.executeSql(strSql);

                /*sFNum = "C" +
                      YssFun.formatDatetime(transfer.getDtTransferDate()).
                      substring(0, 8) +
                 dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
                                             dbl.sqlRight("FNUM", 6), "000001",
                                             " where FTransferDate = " +
                 dbl.sqlDate(transfer.getDtTransferDate()));*/
//                if (sFNum.trim().length() > 0 && sFNum.length() > 9) {
//                    iFNum = YssFun.toInt(YssFun.right(sFNum, 6)); //取出最后六位//BUG4297 获取编号方式改变
//                    sFNum = YssFun.left(sFNum, 9);
//                    iFNum++;
//                    sFNum += YssFun.formatNumber(iFNum, "000000");
//                }
                this.setTransNum(sFNum);
                yssPst.setString(1, sFNum);
                yssPst.setString(2, transfer.getStrTsfTypeCode());
                yssPst.setString(3, transfer.getStrSubTsfTypeCode());
                yssPst.setString(4, " ");
                yssPst.setDate(5, YssFun.toSqlDate(transfer.getDtTransferDate()));
                yssPst.setString(6, "00:00:00");
                yssPst.setDate(7, YssFun.toSqlDate(transfer.getDtTransDate()));
                yssPst.setString(8, " ");
                yssPst.setString(9, transfer.getStrSecurityCode());
                yssPst.setInt(10, 1);
                yssPst.setString(11, " ");
                yssPst.setInt(12, transfer.checkStateId); //FCheckState=3表示是用于监控临时存储的状态
                yssPst.setString(13, pub.getUserCode());
                yssPst.setString(14, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(15, pub.getUserCode());
                yssPst.setString(16, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(17, transfer.getSavingNum());
                yssPst.setString(18, transfer.getCprNum());
                yssPst.setString(19, transfer.getRateTradeNum());
                yssPst.addBatch();

                for (int j = 0; j < transfersets.size(); j++) {
                    transferset = (TransferSetBean) transfersets.get(j);
                    String sNum = YssFun.formatNumber(j + 1, "00000");
                    pstSub.setString(1, sFNum);
                    pstSub.setString(2, sNum); //序号由上面产生
                    pstSub.setInt(3, transferset.getIInOut());
                    pstSub.setString(4, transferset.getSPortCode());
                    pstSub.setString(5, transferset.getSAnalysisCode1());
                    pstSub.setString(6, transferset.getSAnalysisCode2());
                    pstSub.setString(7, " ");
                    pstSub.setString(8, transferset.getSCashAccCode());
                    pstSub.setDouble(9, transferset.getDMoney());
                    pstSub.setDouble(10, transferset.getDBaseRate());
                    pstSub.setDouble(11, transferset.getDPortRate());
                    pstSub.setInt(12, transferset.checkStateId);
                    pstSub.setString(13, pub.getUserCode());
                    pstSub.setString(14,
                                     YssFun.formatDatetime(new java.util.Date()));
                    pstSub.setString(15, pub.getUserCode());
                    pstSub.setString(16,
                                     YssFun.formatDatetime(new java.util.Date()));
                    pstSub.setString(17, " ");
                    pstSub.addBatch();
                }
            }
            if(this.addList.size()>0)//modified by yeshenghong 20120725 story2633 
            {
            	yssPst.executeBatch();
            	pstSub.executeBatch();
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(yssPst);
            dbl.closeStatementFinal(pstSub);
        }
    }

    public void insert(java.util.Date transferDate, java.util.Date transDate,
                       String FIPRNum, String security, int datasource) throws
        YssException {
        insert("", transferDate, transDate, "", "", "", "", "", "", FIPRNum, "",
               security,
               datasource, "", "", 0, "", "", "", true, "");
    }

    /**
     * 重载的方法,增加对组合的处理.现用于在债券支付时使用.sj modified 20081217 MS00089
     * @param transferDate Date
     * @param transDate Date
     * @param tsfType String
     * @param subTsfType String
     * @param portCode String
     * @param security String
     * @param datasource int
     * @throws YssException
     */
    public void insert(java.util.Date transferDate, java.util.Date transDate,
                       String tsfType,
                       String subTsfType, String portCode, String security,
                       int datasource) throws
        YssException {
        insert("", transferDate, transDate, tsfType, subTsfType, "", "", "", "",
               "", "", security,
               datasource, "", portCode, 0, "", "", "", true, "");
    }

    public void insert(java.util.Date transferDate, java.util.Date transDate,
                       String tsfType,
                       String subTsfType, String security, int datasource) throws
        YssException {
        insert("", transferDate, transDate, tsfType, subTsfType, "", "", "", "",
               "", "", security,
               datasource, "", "", 0, "", "", "", true, "");
    }

    /**
     * 重载了方法,加上了账户 sj add 20080326
     * @param transferDate Date
     * @param transDate Date
     * @param tsfType String
     * @param subTsfType String
     * @param security String
     * @param datasource int
     * @param cashacc String
     * @throws YssException
     */
    public void insert(java.util.Date transferDate, java.util.Date transDate,
                       String tsfType,
                       String subTsfType, String security, int datasource,
                       String cashacc) throws
        YssException {
        insert("", transferDate, transDate, tsfType, subTsfType, "", "", "", "",
               "", "", security,
               datasource, cashacc, "", 0, "", "", "", true, "");
    }

    public void insert(String sNums, int iDsInd) throws
        YssException {
        insert(sNums, null, null, "", "", "", "", "", "", "", "", "", iDsInd,
               "",
               "", 0, "", "", "", true, "");
    }

    public void insert(String sSavingNum, String sCprNum) throws
        YssException {
        insert("", null, null, "", "", "", sSavingNum, sCprNum, "", "", "", "",
               -99, "", "", 0, "", "", "", true, ""); //MS00334 add by songjie 2009.03.27 即删除信息时不用判断是否是手动录入还是自动录入全部都删除
    }

    public void insert(String sSavingNum, String sCprNum, String sNumType) throws
        YssException {
        insert("", null, null, "", "", "", sSavingNum, sCprNum, "", "",
               sNumType,
               "", -1, "", "", 0, "", "", "", true, "");
    }

    public void insert(String sSavingNum, String sCprNum, String sRelaNum,
                       String sNumType) throws
        YssException {
        insert("", null, null, "", "", "", sSavingNum, sCprNum, "", "",
               sNumType,
               "", -1, "", "", 0, "", "", "", true, sRelaNum);
    }

    /**
     * 删除资金调拨
     * @param transferDate Date 调拨日期
     * @param sRelaNum String 关联代码
     * @param sRelaType String 关联代码类型
     * @throws YssException
     */
    public void insert(java.util.Date transferDate, String sRelaType, int iDsInd, String sRelaNum) throws YssException {
        insert("", transferDate, null, "", "", "", "", "", "", "", sRelaType,
               "", iDsInd, "", "", 0, "", "", "", true, sRelaNum);
    }

    /**
     * new type
     * @param transDate Date
     * @param sRelaNum String
     * @throws YssException
     */
    public void insert(java.util.Date transDate, String sRelaNum) throws
        YssException {
        insert("", null, transDate, "", "", "", "", "", "", "", "",
               "", -1, "", "", 0, "", "", "", true, sRelaNum);
    }
    /**
     * 方法重载，增加删除条件 编号类型-期权类型为OPtionsTrade
     * @param transDate Date 业务日期
     * @param sNumType String 编号类型
     * @param sRelaNum String 编号
     * @throws YssException
     * xuqiji 20090701 QDV4招商证券2009年06月04日01_A:MS00484 需在系统中增加对期权业务的支持
     */
    public void insert(java.util.Date transDate, String sNumType, String sRelaNum) throws
            YssException {
        insert("", null, transDate, "", "", "", "", "", "", "", sNumType,
               "", -1, "", "", 0, "", "", "", true, sRelaNum);
    }
    
    /**
     * 方法重载，增加删除条件 编号类型-期权类型为OPtionsTrade
     * @param transDate Date 业务日期
     * @param sNumType String 编号类型
     * @param sRelaNum String 编号
     * @param sPortCode String 组合代码
     * @throws YssException
     * add by wangzuochun 2010.11.23  BUG #493 回购业务处理未考虑多组合，报错
     */
    public void insert(java.util.Date transDate, String sNumType, String sPortCode, String sRelaNum) throws
    		YssException {
    	insert("", null, transDate, "", "", "", "", "", "", "", sNumType,
    			"", -1, "", sPortCode, 0, "", "", "", true, sRelaNum);
    }
    
    /**
     * 方法重载，增加删除条件 编号类型-期权类型为OPtionsTrade
     * @param transDate Date 业务日期
     * @param sSubTsfTypeCode String 调拨子类型
     * @param sNumType String 编号类型
     * @param sPortCode String 组合代码
     * @param sRelaNum String 编号
     * @throws YssException
     * xuqiji 20100429 MS01134    在现有的程序版本中增加指数期权及股票期权业务
     */
    public void insert(java.util.Date transDate,String sSubTsfTypeCode,String sNumType,
    		String sPortCode, String sRelaNum) throws
            YssException {
        insert("", null, transDate, "", sSubTsfTypeCode, "", "", "", "", "", sNumType,
               "", -1, "", sPortCode, 0, "", "", "", true, sRelaNum);
    }
    public void insert(String sRateTradeNum) throws
        YssException {
        insert("", null, null, "", "", "", "", "", "", sRateTradeNum, "", "",
               -1,
               "", "", 0, "", "", "", true, "");
    }

    public void insert(String sRateTradeNum, boolean bAutoDel) throws
        YssException {
        insert("", null, null, "", "", "", "", "", "", sRateTradeNum, "", "",
               -1,
               "", "", 0, "", "", "", bAutoDel, "");
    }
    
    /**
     * add by songjie 2013.05.10
     * BUG 7683 QDV4建行2013年05月2日01_B
     * 根据外汇交易编号、自动删除标识、调拨日期 删除数据
     * @param sRateTradeNum 外汇交易编号
     * @param bAutoDel 自动删除标识
     * @param transferDate 调拨日期 删除数据
     * @throws YssException
     */
    public void insert(String sRateTradeNum, boolean bAutoDel, java.util.Date transferDate) throws
    YssException {
    insert("", transferDate, null, "", "", "", "", "", "", sRateTradeNum, "", "",
           -1,
           "", "", 0, "", "", "", bAutoDel, "");
}

    public void insert() throws YssException {
        insert("", null, null, "", "", "", "", "", "", "", "", "", -1, "", "",
               0,
               "", "", "", false, "");
    }

//   public void insert(String sCprNum) throws
//         YssException {
//      insert("", null, null, "", "","","", "", "", -1,"","",0,"","","");
//   }

    public void delete(String sSavingNum, String sCprNum) throws
        YssException {
        delete("", null, null, "", "", "", sSavingNum, sCprNum, "", "", "", "",
               -1, "", "", 0, "", "", "", "");
    }

    public void delete(String sNums, String FRelaNum, String sNumType,
                       String sSavingNum, String sCprNum) throws YssException {
        delete("", null, null, "", "", "", sSavingNum, sCprNum, "", "",
               sNumType,
               "", -1, "", "", 0, "", "", "", FRelaNum);
    }

    //这个方法是根据编号和编号类型来删除资金调拨信息 by sunny
    //陈嘉,这个过程中只删除了资金调拨主表的数据，并没有删除子表的数据
    //请采用通用删除过程fazmm20071021
    /*废弃fazmm20071113
        public void delete(String sRelaNum) throws YssException {
       String strSql = "";
          ResultSet rs = null;
          //alter by sunny
          //可以得到资金调拨编号 根据编号去删除资金调拨 和 资金调拨子编号
          //没有采用重载 因为上面已经含有两个字符串为参数的函数已经存在  这里我提议要统一
       try {
          strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " where FRelaNum="
                + dbl.sqlString(sRelaNum) + " and FNumType=" +
                dbl.sqlString("3");
          dbl.executeSql(strSql);
       }
       catch (Exception e) {
          throw new YssException(e);
       }
        }*/
    /* 废弃fazmm20071113
     public void deleteForward(String sRelaNum,String numType) throws YssException {
      String strSql = "";
      try {
         strSql = "delete from " + pub.yssGetTableName("Tb_Cash_Transfer") +
               " where FRelaNum="
               + dbl.sqlString(sRelaNum) + " and FNumType=" +
               dbl.sqlString(numType);
         dbl.executeSql(strSql);
      }
      catch (Exception e) {
         throw new YssException(e);
      }
       }*/

    public void delete(String sSavingNum, String sCprNum, String sFIPRNum,
                       String sRateTradeNum) throws
        YssException {
        delete("", null, null, "", "", "", sSavingNum, sCprNum, sFIPRNum,
               sRateTradeNum, "", "", -1, "", "", 0, "", "", "", "");
    }

    public void delete(String sSavingNum, String sCprNum, String sRateTradeNum) throws
        YssException {
        delete("", null, null, "", "", "", sSavingNum, sCprNum, "",
               sRateTradeNum,
               "", "", -1, "", "", 0, "", "", "", "");
    }


	/**
	 * @param sNums
	 * @param transferDate
	 * @param transDate
	 * @param sTsfTypeCode
	 * @param sSubTsfTypeCode
	 * @param sTradeNum
	 * @param sSavingNum
	 * @param sCprNum
	 * @param sFIPRNum
	 * @param sRateTradeNum
	 * @param sNumType
	 * @param sSecurityCode
	 * @param iDsInd
	 * @param sCashAccCode
	 * @param sPortCode
	 * @param iInOut
	 * @param sAnalysisCode1
	 * @param sAnalysisCode2
	 * @param sAnalysisCode3
	 * @param sRelaNum
	 * @throws YssException
	 */
    public void delete(String sNums, java.util.Date transferDate,
                       java.util.Date transDate,
                       String sTsfTypeCode, String sSubTsfTypeCode,
                       String sTradeNum, String sSavingNum, String sCprNum,
                       String sFIPRNum, String sRateTradeNum,
                       String sNumType, String sSecurityCode,
                       int iDsInd, String sCashAccCode, String sPortCode,
                       int iInOut,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3, String sRelaNum) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        String sWhereSql = "";

        try {
            sWhereSql = this.buildWhereSql(sNums, transferDate, transDate,
                                           sTsfTypeCode,
                                           sSubTsfTypeCode, sTradeNum,
                                           sSavingNum,
                                           sCprNum, sFIPRNum, sRateTradeNum,
                                           sNumType, sSecurityCode,
                                           iDsInd, sCashAccCode, sPortCode,
                                           iInOut,
                                           sAnalysisCode1,
                                           sAnalysisCode2, sAnalysisCode3,
                                           sRelaNum);
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return;
            }
            strSql =
                "select a.FNum,FTransferDate,FTransDate,FTradeNum,FSecurityCode,b.* from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a join (select * from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                ") b on a.FNum = b.FNum" + sWhereSql;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sNums += rs.getString("FNum") + ",";
            }
            dbl.closeResultSetFinal(rs);

            sNums = operSql.sqlCodes(sNums);
			if (sNums.trim().length() > 0) {
				/**
				 * add---huhuichao 2013-10-31 BUG 82531 期货保证金调整问题（列表中的最大表达式数为
				 * 1000）
				 */
				StringBuffer sb = new StringBuffer();
				String[] sNumbers = sNums.split(",");
				int inNum = 1; // 已拼装IN条件数量
				/**add---huhuichao 2013-10-31 BUG  82531  期货保证金调整问题（列表中的最大表达式数为 1000）*/
					for (int i = 0; i < sNumbers.length; i++) {
						if (sNumbers[i] == null && sNumbers[i].length() == 0)
							continue;// sRelaNumber[i]为空，则进行下一个
						if (i == (sNumbers.length - 1))
							sb.append(operSql.sqlCodes(sNumbers[i])); // SQL拼装，最后一条不加“,”。
						else if (inNum == 1000 && i > 0) {
							sb.append(operSql.sqlCodes(sNumbers[i]) + " ) OR FNum IN ( "); // 解决ORA-01795问题
							inNum = 1;
						} else {
							sb.append(operSql.sqlCodes(sNumbers[i]) + ",");
							inNum++;
						}
					}
				/**end---huhuichao 2013-10-31 BUG  82531  */
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FNum in (" + sb.toString() + " )";
                dbl.executeSql(strSql);

                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum in (" + sb.toString() + " )";
                dbl.executeSql(strSql);
                /**end---huhuichao 2013-10-31 BUG  82531 */
            }
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    
	public void insert(String sNums, java.util.Date transferDate,
			java.util.Date transDate, String sTsfTypeCode,
			String sSubTsfTypeCode, String sTradeNum, String sSavingNum,
			String sCprNum, String sFIPRNum, String sRateTradeNum,
			String sNumType, String sSecurityCode, int iDsInd,
			String sCashAccCode, String sPortCode, int iInOut,
			String sAnalysisCode1, String sAnalysisCode2,
			String sAnalysisCode3, boolean bAutoDel, String sRelaNum
			) throws YssException {
		if(this.taMap.size()>0)//  by yeshenghong to solve BUG5490 20120911
		{
			insertTaTrade(sNums,transferDate,transDate,sTsfTypeCode,sSubTsfTypeCode,sTradeNum,sSavingNum,
					sCprNum,sFIPRNum,sRateTradeNum,sNumType,sSecurityCode,iDsInd,sCashAccCode,sPortCode,
					iInOut,sAnalysisCode1,sAnalysisCode2,sAnalysisCode3,bAutoDel,sRelaNum,"");
		}else
		{
			insert (sNums,transferDate,transDate,sTsfTypeCode,sSubTsfTypeCode,sTradeNum,sSavingNum,
					sCprNum,sFIPRNum,sRateTradeNum,sNumType,sSecurityCode,iDsInd,sCashAccCode,sPortCode,
					iInOut,sAnalysisCode1,sAnalysisCode2,sAnalysisCode3,bAutoDel,sRelaNum,"");
		}
	}
    
    /**************************************************************
     * 添加所属分类  NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22
     * @param sNums
     * @param transferDate
     * @param transDate
     * @param sTsfTypeCode
     * @param sSubTsfTypeCode
     * @param sTradeNum
     * @param sSavingNum
     * @param sCprNum
     * @param sFIPRNum
     * @param sRateTradeNum
     * @param sNumType
     * @param sSecurityCode
     * @param iDsInd
     * @param sCashAccCode
     * @param sPortCode
     * @param iInOut
     * @param sAnalysisCode1
     * @param sAnalysisCode2
     * @param sAnalysisCode3
     * @param bAutoDel
     * @param sRelaNum
     * @param sAttrClsCode   
     * @throws YssException
     */
    public void insert(String sNums, java.util.Date transferDate,
                       java.util.Date transDate,
                       String sTsfTypeCode, String sSubTsfTypeCode,
                       String sTradeNum, String sSavingNum, String sCprNum,
                       String sFIPRNum, String sRateTradeNum,
                       String sNumType, String sSecurityCode,
                       int iDsInd, String sCashAccCode, String sPortCode,
                       int iInOut,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3, boolean bAutoDel, String sRelaNum,String sAttrClsCode) throws
        YssException {
        String strSql = "";
        int i = 0;
        int j = 0;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
//        PreparedStatement pstSub = null;
        YssPreparedStatement yssPst = null;
        YssPreparedStatement yssPstSub = null;
        //===============end==================
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String sFNum = "";
        String sTmpNum = "";
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        //ResultSet rs = null;//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        ArrayList subTrans = null;
        int iFNum = 0;
        HashMap htDiffDate = new HashMap(); //根据不同的交易编号存放不同的编号 by leeyu
        boolean bAddFlag = false;//以此标记是否向主表中插入数据
        try {
            if (bAutoDel) {
                delete(sNums, transferDate, transDate, sTsfTypeCode,
                       sSubTsfTypeCode, sTradeNum, sSavingNum,
                       sCprNum, sFIPRNum, sRateTradeNum, sNumType,
                       sSecurityCode,
                       iDsInd, sCashAccCode, sPortCode, iInOut,
                       sAnalysisCode1,
                       sAnalysisCode2, sAnalysisCode3, sRelaNum);
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " (FNum,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FTransferDate,FTransferTime,FTransDate,FTradeNum," +
                "FSecurityCode,FDataSource,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FSavingNum,FCPRNum,FIPRNum,FRateTradeNum,FRelaNum,FNumType)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            strSql = "insert into " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " (FNum,FSubNum,FInOut,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FMoney," +
                "FBaseCuryRate,FPortCuryRate,FATTRCLSCODE,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDesc)" +// NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 添加所属分类属性
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            pstSub = conn.prepareStatement(strSql);
            yssPstSub = dbl.getYssPreparedStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                transfer = (TransferBean) addList.get(i);
                sFNum = getNum(transfer); //BUG4297 编号改为从序列取值 yeshenghong 20120725
//                if (i == 0) {
//                    sFNum = getNum(transfer); //只取一次
//                    htDiffDate.put(transfer.getDtTransferDate(), sFNum);
//                }
//                if (htDiffDate.get(transfer.getDtTransferDate()) == null) {
//                    sFNum = getNum(transfer); //只取一次
//                    htDiffDate.put(transfer.getDtTransferDate(), sFNum);
//                }else {
//                	sFNum = (String)htDiffDate.get(transfer.getDtTransferDate());//add by guolongchao 20110830 STORY 1207 资金调拨违反唯一约束
//                }
                
                /* sFNum = "C" +
                       YssFun.formatDatetime(transfer.getDtTransferDate()).
                       substring(0, 8) +
                 dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Cash_Transfer"),
                 dbl.sqlRight("FNUM", 6), "000001",
                                              " where FTransferDate = " +
                 dbl.sqlDate(transfer.getDtTransferDate()));
                 */
                //这个地方在上面处理掉
//                if (sFNum.trim().length() > 0 && sFNum.length() > 9) { //BUG4297 编号改为从序列取值 yeshenghong 20120725
////                    iFNum = YssFun.toInt(YssFun.right(sFNum, 6)); //取出最后六位
////                    sFNum = YssFun.left(sFNum, 9); //取出最左边9位
////                    iFNum++;
////                    sFNum += YssFun.formatNumber(iFNum, "000000");
//                    htDiffDate.put(transfer.getDtTransferDate(), sFNum);//add by guolongchao 20110830 STORY 1207 资金调拨违反唯一约束
//                }
                yssPst.setString(1, sFNum);
                yssPst.setString(2, transfer.getStrTsfTypeCode());
                yssPst.setString(3, transfer.getStrSubTsfTypeCode());
                //fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB   
                yssPst.setString(4, transfer.getStrAttrClsCode().length()>1?transfer.getStrAttrClsCode():" ");
                //------------------end --------------------------------------
                yssPst.setDate(5, YssFun.toSqlDate(transfer.getDtTransferDate()));
                yssPst.setString(6, "00:00:00");
                yssPst.setDate(7, YssFun.toSqlDate(transfer.getDtTransDate()));
                yssPst.setString(8, transfer.getStrTradeNum());
                yssPst.setString(9, transfer.getStrSecurityCode());
                yssPst.setInt(10, transfer.getDataSource());
                yssPst.setString(11, " ");
                yssPst.setInt(12, transfer.checkStateId); //FCheckState=3表示是用于监控临时存储的状态
                yssPst.setString(13, pub.getUserCode());
                yssPst.setString(14, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(15, pub.getUserCode());
                yssPst.setString(16, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(17, transfer.getSavingNum());
                yssPst.setString(18, transfer.getCprNum());
                yssPst.setString(19, transfer.getFIPRNum());
                yssPst.setString(20, transfer.getRateTradeNum());
//            pst.setString(21, transfer.getFRelaNum());
                //--------MS00141 QDV4交银施罗德2009年01月4日02_B sj modified 若关联编号的容器有生成,则使用关联编号容器中的编号,其值是从现金支付中的应收应付中获取的 ---//
                yssPst.setString(21,
                              relaOrderNum == null ? transfer.getFRelaNum() :
                              (String)
                              relaOrderNum.get(transfer.getRelaOrderNum())); //资金调拨中的排序编号和现金应收应付中的排序编号是同步的。
                //----------------------------------------------------------------------------------------------------------------------------------------//
                yssPst.setString(22, transfer.getFNumType());
//                pst.executeUpdate();//delete by xuxuming,20091208.移到后面执行。此处执行，会导致主表插入数据了，而子表因金额都为0而没有插入数据。
                                      //此种情形会导致这些插入到主表中的数据无法删除了
                //--------------------若要多条资金调拨配多条资金子调拨,则在资金调拨的bean中放入一个资金子调拨的arrayList,在此bean中的subaddList中就不放值 sj add 20080123//
                //--------------------将不同的ArrayList放入到tempList中----------------//sj
                if (subaddList.size() != 0) {
                    tempList = subaddList;
                } else {
                    subTrans = transfer.getSubTrans();
                    tempList = subTrans;
                }
                //-----------------------------------------------------------------------------------------------------------------------------------
                //=====add by xuxuming,20091208.MS00845,股指期货交易数据估值后，在资金调拨界面查询不到数据    QDV4赢时胜上海2009年12月3日01_B===================
                   //===========因为某些交易数据估值后的金额为0，而没有插入子表中，界面上是关联子表来查询的，当然就不会显示，但在插入主表之前已经将数据插入主表了，而
                   //=================删除时也是关联子表来删除，导致这些主表数据无法删除，每估值一次就会多一条这种记录==================================
                   //===============此处加上判断，当对应子表金额不全为0时，才执行主表插入数据=======================
               
                for (j = 0; j < this.tempList.size(); j++) {
                	transferset = (TransferSetBean) tempList.get(j);//取对应子表数据
                	if (transferset.getDMoney() != 0){
                		bAddFlag = true;//子表中有任何一条记录的金额不为0，则标记为trueE
                	}
                }
                if(bAddFlag){
                	yssPst.addBatch();//此时才执行插入数据操作  modified by yeshenghong 20120726 story 2633
                }
                //======================end=============================================================================================================
                for (j = 0; j < this.tempList.size(); j++) { //大循环中也是采用i变量，会导致大循环提前退出或重复大循环fazmm20071021
                    transferset = (TransferSetBean) tempList.get(j); //将所用的ArrayList用已赋值的tempList代替 sj edit 20080124
                    if (transferset.getDMoney() != 0) { //如果资金调拨的金额为0时，不用插入到资金调拨表中fazmm20071008
                    	yssPstSub.setString(1, sFNum);
                    	yssPstSub.setString(2, YssFun.formatNumber(j + 1, "00000")); // by ly 080221 ,因为如果 j 值大于9,位数就会大于6位.
                    	yssPstSub.setInt(3, transferset.getIInOut());
                    	yssPstSub.setString(4, transferset.getSPortCode());
                    	yssPstSub.setString(5,
                                         transferset.getSAnalysisCode1().trim().
                                         length() ==
                                         0 ? " " :
                                         transferset.getSAnalysisCode1()); //如果没有分析代码时必须要保存一个空格，否则净值统计表的应收应付获取有问题fazmm20071020
                    	yssPstSub.setString(6,
                                         transferset.getSAnalysisCode2().trim().
                                         length() ==
                                         0 ? " " :
                                         transferset.getSAnalysisCode2());
                    	yssPstSub.setString(7, " ");
                    	yssPstSub.setString(8, transferset.getSCashAccCode());
                    	yssPstSub.setDouble(9, transferset.getDMoney());
                        //gyc测试
                       /* pstSub.setDouble(10,
                               0.5); //hxqdii
               pstSub.setDouble(11,
                                0.5); //hxqdii
*/                        //
                        
                        
                        
                    	yssPstSub.setDouble(10,
                                         YssFun.roundIt(transferset.
                            getDBaseRate(),
                            15)); //hxqdii
                    	yssPstSub.setDouble(11,
                                         YssFun.roundIt(transferset.
                            getDPortRate(),
                            15)); //hxqdii
                        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                    	yssPstSub.setString(12, (transferset.getStrAttrClsCode().trim().length()==0?" ":transferset.getStrAttrClsCode()));
                        //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                    	yssPstSub.setInt(13, transfer.checkStateId);
                        yssPstSub.setString(14, pub.getUserCode());
                        yssPstSub.setString(15,
                                         YssFun.formatDatetime(new java.util.
                            Date()));
                        yssPstSub.setString(16, pub.getUserCode());
                        yssPstSub.setString(17,
                                         YssFun.formatDatetime(new java.util.
                            Date()));
//                  pstSub.setString(17, " ");
                        yssPstSub.setString(18,
                                         transferset.getSDesc().trim().length() >
                                         0 ? transferset.getSDesc() : " "); //添加對描述的輸入.sj 20081222 MS00114
                        yssPstSub.addBatch();
                    }
                }

            }
            //------------------------------------------------------------------------------------------
            if(bAddFlag){
            	yssPst.executeBatch();
            	yssPstSub.executeBatch();
            }
            
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(yssPst);
            dbl.closeStatementFinal(yssPstSub);
        }

    }
    
    
    /**************************************************************
     * Method Added for TA Settlement, To Solve BUG5490 modified by yeshenghong 20120911
     * @param sNums
     * @param transferDate
     * @param transDate
     * @param sTsfTypeCode
     * @param sSubTsfTypeCode
     * @param sTradeNum
     * @param sSavingNum
     * @param sCprNum
     * @param sFIPRNum
     * @param sRateTradeNum
     * @param sNumType
     * @param sSecurityCode
     * @param iDsInd
     * @param sCashAccCode
     * @param sPortCode
     * @param iInOut
     * @param sAnalysisCode1
     * @param sAnalysisCode2
     * @param sAnalysisCode3
     * @param bAutoDel
     * @param sRelaNum
     * @param sAttrClsCode   
     * @throws YssException
     */
    public void insertTaTrade(String sNums, java.util.Date transferDate,
                       java.util.Date transDate,
                       String sTsfTypeCode, String sSubTsfTypeCode,
                       String sTradeNum, String sSavingNum, String sCprNum,
                       String sFIPRNum, String sRateTradeNum,
                       String sNumType, String sSecurityCode,
                       int iDsInd, String sCashAccCode, String sPortCode,
                       int iInOut,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3, boolean bAutoDel, String sRelaNum,String sAttrClsCode) throws
        YssException {
        String strSql = "";
        YssPreparedStatement yssPst = null;
        YssPreparedStatement yssPstSub = null;
        //===============end==================
        //Connection conn = dbl.loadConnection();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String sFNum = "";
        TransferBean transfer = null;
        TransferSetBean transferset = null;
        //ResultSet rs = null;//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
//        HashMap htDiffDate = new HashMap(); //根据不同的交易编号存放不同的编号 by leeyu
        boolean bAddFlag = false;//以此标记是否向主表中插入数据
        try {
            if (bAutoDel) {
                delete(sNums, transferDate, transDate, sTsfTypeCode,
                       sSubTsfTypeCode, sTradeNum, sSavingNum,
                       sCprNum, sFIPRNum, sRateTradeNum, sNumType,
                       sSecurityCode,
                       iDsInd, sCashAccCode, sPortCode, iInOut,
                       sAnalysisCode1,
                       sAnalysisCode2, sAnalysisCode3, sRelaNum);
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Cash_Transfer") +
                " (FNum,FTsfTypeCode,FSubTsfTypeCode,FAttrClsCode,FTransferDate,FTransferTime,FTransDate,FTradeNum," +
                "FSecurityCode,FDataSource,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FSavingNum,FCPRNum,FIPRNum,FRateTradeNum,FRelaNum,FNumType)" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            yssPst = dbl.getYssPreparedStatement(strSql);

            strSql = "insert into " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                " (FNum,FSubNum,FInOut,FPortCode,FAnalysisCode1,FAnalysisCode2,FAnalysisCode3,FCashAccCode,FMoney," +
                "FBaseCuryRate,FPortCuryRate,FATTRCLSCODE,FCheckState,FCreator,FCreateTime,FCheckUser,FCheckTime,FDesc)" +// NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 添加所属分类属性
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            yssPstSub = dbl.getYssPreparedStatement(strSql);

            for (int i = 0; i < this.addList.size(); i++) {
                transfer = (TransferBean) addList.get(i);
                sFNum = transfer.getStrNum(); //BUG4297 编号改为从序列取值 yeshenghong 20120725
                yssPst.setString(1, sFNum);
                yssPst.setString(2, transfer.getStrTsfTypeCode());
                yssPst.setString(3, transfer.getStrSubTsfTypeCode());
                //fanghaoln 20100511 MS01125 QDV4赢时胜上海2010年04月27日01_AB   
                yssPst.setString(4, transfer.getStrAttrClsCode().length()>1?transfer.getStrAttrClsCode():" ");
                //------------------end --------------------------------------
                yssPst.setDate(5, YssFun.toSqlDate(transfer.getDtTransferDate()));
                yssPst.setString(6, "00:00:00");
                yssPst.setDate(7, YssFun.toSqlDate(transfer.getDtTransDate()));
                yssPst.setString(8, transfer.getStrTradeNum());
                yssPst.setString(9, transfer.getStrSecurityCode());
                yssPst.setInt(10, transfer.getDataSource());
                yssPst.setString(11, " ");
                yssPst.setInt(12, transfer.checkStateId); //FCheckState=3表示是用于监控临时存储的状态
                yssPst.setString(13, pub.getUserCode());
                yssPst.setString(14, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(15, pub.getUserCode());
                yssPst.setString(16, YssFun.formatDatetime(new java.util.Date()));
                yssPst.setString(17, transfer.getSavingNum());
                yssPst.setString(18, transfer.getCprNum());
                yssPst.setString(19, transfer.getFIPRNum());
                yssPst.setString(20, transfer.getRateTradeNum());
//            pst.setString(21, transfer.getFRelaNum());
                //--------MS00141 QDV4交银施罗德2009年01月4日02_B sj modified 若关联编号的容器有生成,则使用关联编号容器中的编号,其值是从现金支付中的应收应付中获取的 ---//
                yssPst.setString(21,
                              relaOrderNum == null ? transfer.getFRelaNum() :
                              (String)
                              relaOrderNum.get(transfer.getRelaOrderNum())); //资金调拨中的排序编号和现金应收应付中的排序编号是同步的。
                //----------------------------------------------------------------------------------------------------------------------------------------//
                yssPst.setString(22, transfer.getFNumType());
                
                //-----------------------------------------------------------------------------------------------------------------------------------
                //=====add by xuxuming,20091208.MS00845,股指期货交易数据估值后，在资金调拨界面查询不到数据    QDV4赢时胜上海2009年12月3日01_B===================
                   //===========因为某些交易数据估值后的金额为0，而没有插入子表中，界面上是关联子表来查询的，当然就不会显示，但在插入主表之前已经将数据插入主表了，而
                   //=================删除时也是关联子表来删除，导致这些主表数据无法删除，每估值一次就会多一条这种记录==================================
                   //===============此处加上判断，当对应子表金额不全为0时，才执行主表插入数据=======================
               
                //modified by yeshenghong 20120911 因为TA交易结算和资金调拨是一对一的关系  所以不采用LIST 循环
                transferset = (TransferSetBean)this.taMap.get(sFNum);

            	if (transferset.getDMoney() != 0){
            		bAddFlag = true;//子表中有记录的金额不为0，则标记为trueE
            	}else
            	{
            		bAddFlag = false;
            	}
                if(bAddFlag){
                	yssPst.addBatch();//此时才执行插入数据操作  modified by yeshenghong 20120911 story 2633
                }
                //======================end=============================================================================================================
                
                if (transferset.getDMoney() != 0) { //如果资金调拨的金额为0时，不用插入到资金调拨表中fazmm20071008
                	yssPstSub.setString(1, sFNum);
                	yssPstSub.setString(2, YssFun.formatNumber(1, "00000")); // by ly 080221 ,因为如果 j 值大于9,位数就会大于6位.
                	yssPstSub.setInt(3, transferset.getIInOut());
                	yssPstSub.setString(4, transferset.getSPortCode());
                	yssPstSub.setString(5,
                                     transferset.getSAnalysisCode1().trim().
                                     length() ==
                                     0 ? " " :
                                     transferset.getSAnalysisCode1()); //如果没有分析代码时必须要保存一个空格，否则净值统计表的应收应付获取有问题fazmm20071020
                	yssPstSub.setString(6,
                                     transferset.getSAnalysisCode2().trim().
                                     length() ==
                                     0 ? " " :
                                     transferset.getSAnalysisCode2());
                	yssPstSub.setString(7, " ");
                	yssPstSub.setString(8, transferset.getSCashAccCode());
                	yssPstSub.setDouble(9, transferset.getDMoney());
                	yssPstSub.setDouble(10,
                                     YssFun.roundIt(transferset.
                        getDBaseRate(),
                        15)); //hxqdii
                	yssPstSub.setDouble(11,
                                     YssFun.roundIt(transferset.
                        getDPortRate(),
                        15)); //hxqdii
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  add by jiangshichao 2010.11.22 ---//
                	yssPstSub.setString(12, (transferset.getStrAttrClsCode().trim().length()==0?" ":transferset.getStrAttrClsCode()));
                    //--- NO.125 用户需要对组合按资本类别进行子组合的分类  end ------------------------------//
                	yssPstSub.setInt(13, transfer.checkStateId);
                    yssPstSub.setString(14, pub.getUserCode());
                    yssPstSub.setString(15,
                                     YssFun.formatDatetime(new java.util.
                        Date()));
                    yssPstSub.setString(16, pub.getUserCode());
                    yssPstSub.setString(17,
                                     YssFun.formatDatetime(new java.util.
                        Date()));
                    yssPstSub.setString(18,
                                     transferset.getSDesc().trim().length() >
                                     0 ? transferset.getSDesc() : " "); //添加對描述的輸入.sj 20081222 MS00114
                    yssPstSub.addBatch();
                }

            }
            //------------------------------------------------------------------------------------------
            if(bAddFlag){
            	yssPst.executeBatch();
            	yssPstSub.executeBatch();
            }
            
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(yssPst);
            dbl.closeStatementFinal(yssPstSub);
        }

    }
    

    //添加获取最大编号的方法  by liyu 080330
    private String getNum(TransferBean transfer) throws YssException {
        String sFNum = "";
        try {
            sFNum = "C" +
                YssFun.formatDatetime(transfer.getDtTransferDate()).
                substring(0, 8) + dbFun.getNextDataInnerCode();
//                dbFun.getNextInnerCode(pub.yssGetTableName(
//                    "Tb_Cash_Transfer"),
//                                       dbl.sqlRight("FNUM", 6), "000000",
//                                       " where FTransferDate = " +
//                                       dbl.sqlDate(transfer.getDtTransferDate()));
            return sFNum;
        } catch (Exception e) {
            throw new YssException("获取最大编号出错", e);
        }
    }

    /**
     * 重载方法,增加对资金方向的判断.
     * @param sNumType String
     * @param sRelaNum String
     * @param iInOut int
     * @return int
     * @throws YssException
     */
    public int deleteWithReturnRows(String sNumType, String sRelaNum,
                                    int iInOut) throws YssException {
        return deleteWithReturnRows("", null,
                                    null,
                                    "", "",
                                    "", "",
                                    "",
                                    "", "",
                                    sNumType, "",
                                    -99, "", //MS00335 QDV4中保2009年03月25日01_B 与之前修改的数据来源的调整相一致.
                                    "",
                                    iInOut,
                                    "", "",
                                    "", sRelaNum);
    }

    /**
     * 重载方法，以关联编号和编号类型进行操作。
     * sj 20081222
     * @param sNumType String
     * @param sRelaNum String
     * @return int
     * @throws YssException
     */
    public int deleteWithReturnRows(String sNumType, String sRelaNum) throws
        YssException {
        return deleteWithReturnRows("", null,
                                    null,
                                    "", "",
                                    "", "",
                                    "",
                                    "", "",
                                    sNumType, "",
                                    -99, "", //MS00335 QDV4中保2009年03月25日01_B 与之前修改的数据来源的调整相一致.
                                    "",
                                    -1,
                                    "", "",
                                    "", sRelaNum);
    }

    /**
     * 返回删除行数的方法。
     * sj 20081222
     * @param FRelaNum String
     * @param sNumType String
     * @return int
     * @throws YssException
     */
    public int deleteWithReturnRows(String sNums, java.util.Date transferDate,
                                    java.util.Date transDate,
                                    String sTsfTypeCode, String sSubTsfTypeCode,
                                    String sTradeNum, String sSavingNum,
                                    String sCprNum,
                                    String sFIPRNum, String sRateTradeNum,
                                    String sNumType, String sSecurityCode,
                                    int iDsInd, String sCashAccCode,
                                    String sPortCode,
                                    int iInOut,
                                    String sAnalysisCode1,
                                    String sAnalysisCode2,
                                    String sAnalysisCode3, String sRelaNum) throws
        YssException {
        int excuteRows = 0;
        String strSql = "";
        ResultSet rs = null;
        String sWhereSql = "";
        try {
            sWhereSql = this.buildWhereSql(sNums, transferDate, transDate,
                                           sTsfTypeCode,
                                           sSubTsfTypeCode, sTradeNum,
                                           sSavingNum,
                                           sCprNum, sFIPRNum, sRateTradeNum,
                                           sNumType, sSecurityCode,
                                           iDsInd, sCashAccCode, sPortCode,
                                           iInOut,
                                           sAnalysisCode1,
                                           sAnalysisCode2, sAnalysisCode3,
                                           sRelaNum);
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return 0;
            }
            strSql =
                "select a.FNum,FTransferDate,FTransDate,FTradeNum,FSecurityCode,b.* from " +
                pub.yssGetTableName("Tb_Cash_Transfer") +
                " a join (select * from " +
                pub.yssGetTableName("Tb_Cash_SubTransfer") +
                ") b on a.FNum = b.FNum" + sWhereSql;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                sNums += rs.getString("FNum") + ",";
            }
            dbl.closeResultSetFinal(rs);

            sNums = operSql.sqlCodes(sNums);
            if (sNums.trim().length() > 0) {
                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_Transfer") +
                    " where FNum in (" + sNums + ")";
                dbl.executeSql(strSql);

                strSql = "delete from " +
                    pub.yssGetTableName("Tb_Cash_SubTransfer") +
                    " where FNum in (" + sNums + ")";
                excuteRows = dbl.executeSqlwithReturnRows(strSql); //有返回执行行数的sql执行方法。
            }
        } catch (Exception e) {
            throw new YssException(e);
        }
        return excuteRows;
    }
    //------ add by wangzuochun 2010.08.25  MS01606    定存业务处理后，不能删除历史资金调拨数据    QDV4赢时胜(测试)2010年08月12日07_B
	public ArrayList getAddList() {
		return addList;
	}

	public void setAddList(ArrayList addList) {
		this.addList = addList;
	}
	//----------MS01606---------//
}
