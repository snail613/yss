package com.yss.manager;

import com.yss.dsub.BaseBean;
import com.yss.dsub.YssPreparedStatement;
import com.yss.main.operdata.*;
import java.util.*;
import java.sql.*;
import com.yss.util.*;

/**
 *
 * <p>Title:业务资料--交易关联表 </p>
 *
 * <p>Description: QDV4.1赢时胜（上海）2009年4月20日25_A   MS00025</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ysstech</p>
 *
 * @author by leeyu 20090703
 * @version 1.0
 */
public class TradeRelaDataAdmin extends BaseBean {
    public ArrayList addList = new ArrayList();  //保存交易关联主表的数据
    public ArrayList subaddList = new ArrayList();//保存交易关联子表的数据
    public String subTradeNum="";//业务资料(交易子表)的编号
    public TradeRelaDataAdmin() {
    }

    /**
     * 删除条件
     * @param sNums String
     * @param sRelaType String
     * @param sSecurityCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @return String
     */
    public String buildWhereSql(String sNums, String sRelaType,
                                String sSecurityCode,String sPortCode,
                                String sAnalysisCode1, String sAnalysisCode2,
                                String sAnalysisCode3) {
        String sResult = " where 1=1 ";
        if (sNums.length() > 0) {
            sResult += " and FNum in(" + operSql.sqlCodes(sNums) + ")";
        }
        if (sRelaType.length() > 0) {
            sResult += " and FRelaType in (" +
                    operSql.sqlCodes(sRelaType) +
                    ")";
        }
        if (sSecurityCode.length() > 0) {
            sResult += " and FSecurityCode in (" +
                    operSql.sqlCodes(sSecurityCode) +
                    ")";
        }
        if (sPortCode.length() > 0) {
            sResult += " and FPortCode in(" + operSql.sqlCodes(sPortCode)+")";
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
        return sResult;
    }
    /**
     * 删除数据方法
     * @param sNums String
     * @param sRelaType String
     * @param sSecurityCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @throws YssException
     */
    public void delete(String sNums,String sRelaType,
                       String sSecurityCode,String sPortCode,
                       String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3 ) throws
            YssException {
        String strSql = "";
        ResultSet rs = null;
        String sWhereSql = "";
        String nums = "";//add by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B
        try {
            sWhereSql = this.buildWhereSql(sNums, sRelaType,
                                           sSecurityCode, sPortCode,
                                           sAnalysisCode1,
                                           sAnalysisCode2, sAnalysisCode3);
            if (sWhereSql.trim().length() == 0 ||
                sWhereSql.trim().equalsIgnoreCase("where 1=1")) {
                return;
            }
            strSql =
                    "select FNum from " +
                    pub.yssGetTableName("Tb_Data_Traderela") +
                    sWhereSql;
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
			//edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B
            	nums += rs.getString("FNum") + ",";
            }
            dbl.closeResultSetFinal(rs);
            //---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B start---//
            if(nums.length() > 1){
            	nums = nums.substring(0, nums.length() - 1);
            }
            
            nums = operSql.sqlCodes(nums);
			//---edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B end---//
            if (sNums.trim().length() > 0) {
                strSql = "delete from " +
                         pub.yssGetTableName("Tb_Data_Traderela") +
						 //edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B
                         " where FNum in (" + nums + ")";
                dbl.executeSql(strSql);

                strSql = "delete from " +
                         pub.yssGetTableName("Tb_Data_TradeRelaSub") +
						 //edit by songjie 2012.02.06 BUG 3791 QDV4赢时胜(测试)2012年02月03日03_B
                         " where FNum in (" + nums + ")";
                dbl.executeSql(strSql);
            }
        } catch (Exception e) {
            throw new YssException(e);
        }
    }

    public void insert() throws YssException{
        insert("","","","","","","",false);
    }
    public void insert(String sNums) throws YssException{
        insert(sNums,"","","","","","",true);
    }
    /**
     * 插入数据方法
     * @param sNums String
     * @param sRelaType String
     * @param sSecurityCode String
     * @param sPortCode String
     * @param sAnalysisCode1 String
     * @param sAnalysisCode2 String
     * @param sAnalysisCode3 String
     * @param bAutoDel boolean
     * @throws YssException
     */
    public void insert(String sNums,
                       String sRelaType,String sSecurityCode,
                       String sPortCode,String sAnalysisCode1, String sAnalysisCode2,
                       String sAnalysisCode3,boolean bAutoDel) throws
            YssException {
        String strSql = "";
        int i = 0;
        int j = 0;
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pst = null;
        YssPreparedStatement yssPst = null;
        //===============end==================
        PreparedStatement pstSub = null;
        Connection conn = dbl.loadConnection();
        TradeRelaBean tradeRela = null;
        TradeRelaSubBean tradeRelaSub = null;
        try {
            if (bAutoDel) {
                delete(sNums, sRelaType,
                       sSecurityCode,sPortCode,
                       sAnalysisCode1,
                       sAnalysisCode2, sAnalysisCode3);
            }
            strSql = "insert into " + pub.yssGetTableName("Tb_Data_Traderela") +
                "(FNUM,FRELATYPE,FPORTCODE,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FSECURITYCODE,"+
                "FAMOUNT,FCOST,FMCOST,FVCOST,FBASECURYCOST,FMBASECURYCOST,FVBASECURYCOST,FPORTCURYCOST,"+
                "FMPORTCURYCOST,FVPORTCURYCOST,FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FInOut,FATTRCLSCODE) "+//22
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//22
//            pst = conn.prepareStatement(strSql);
            yssPst = dbl.getYssPreparedStatement(strSql);

            strSql = "insert into " +
                     pub.yssGetTableName("Tb_Data_TradeRelaSub") +
                     "(FNUM,FRELATYPE,FPORTCODE,FANALYSISCODE1,FANALYSISCODE2,FANALYSISCODE3,FSECURITYCODE,FTSFTYPECODE,FSUBTSFTYPECODE,"+
                     "FCURYCODE,FBAL,FMBAL,FVBAL,FPORTCURYBAL,FMPORTCURYBAL,FVPORTCURYBAL,FBASECURYBAL,FMBASECURYBAL,FVBASECURYBAL,"+
                     "FDESC,FCHECKSTATE,FCREATOR,FCREATETIME,FInOut,FATTRCLSCODE)"+//24
                     " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";//24
            pstSub = conn.prepareStatement(strSql);

            for (i = 0; i < this.addList.size(); i++) {
                tradeRela = (TradeRelaBean) addList.get(i);
                yssPst.setString(1, tradeRela.getSNum());
                yssPst.setString(2, tradeRela.getSRelaType());
                yssPst.setString(3, tradeRela.getSPortCode());
                yssPst.setString(4, tradeRela.getSAnalysisCode1().trim().length()==0?" ":tradeRela.getSAnalysisCode1());
                yssPst.setString(5, tradeRela.getSAnalysisCode2().trim().length()==0?" ":tradeRela.getSAnalysisCode2());
                yssPst.setString(6, tradeRela.getSAnalysisCode3().trim().length()==0?" ":tradeRela.getSAnalysisCode3());
                yssPst.setString(7, tradeRela.getSSecurityCode());
                yssPst.setDouble(8, tradeRela.getDAmount());
                yssPst.setDouble(9,tradeRela.getDCost());
                yssPst.setDouble(10, tradeRela.getDMCost());
                yssPst.setDouble(11, tradeRela.getDVCost());
                yssPst.setDouble(12, tradeRela.getDBaseCuryCost());
                yssPst.setDouble(13, tradeRela.getDMBaseCuryCost());
                yssPst.setDouble(14, tradeRela.getDVBaseCuryCost());
                yssPst.setDouble(15, tradeRela.getDPortCuryCost());
                yssPst.setDouble(16, tradeRela.getDMPortCuryCost());
                yssPst.setDouble(17, tradeRela.getDVPortCuryCost());
                yssPst.setString(18, tradeRela.getSDesc());
                yssPst.setInt(19, tradeRela.checkStateId);
                yssPst.setString(20, pub.getUserCode());
                yssPst.setString(21,YssFun.formatDatetime(new java.util.Date()));
                yssPst.setInt(22,tradeRela.getIInOut());
                yssPst.setString(23,tradeRela.getAttrClsCode().trim().length()==0?" ":tradeRela.getAttrClsCode());//24
                yssPst.executeUpdate();
            }
            for (j = 0; j < this.subaddList.size(); j++) {
                tradeRelaSub = (TradeRelaSubBean) subaddList.get(j);
                pstSub.setString(1, tradeRelaSub.getSNum());
                pstSub.setString(2, tradeRelaSub.getSRelaType());
                pstSub.setString(3, tradeRelaSub.getSPortCode());
                pstSub.setString(4, tradeRelaSub.getSAnalysisCode1().trim().length() == 0 ? " " : tradeRelaSub.getSAnalysisCode1());
                pstSub.setString(5, tradeRelaSub.getSAnalysisCode2().trim().length() == 0 ? " " : tradeRelaSub.getSAnalysisCode2()); //如果没有分析代码时必须要保存一个空格，否则净值统计表的应收应付获取有问题fazmm20071020
                pstSub.setString(6, tradeRelaSub.getSAnalysisCode3().trim().length() == 0 ? " " : tradeRelaSub.getSAnalysisCode3());
                pstSub.setString(7, tradeRelaSub.getSSecurityCode());
                pstSub.setString(8, tradeRelaSub.getSTsfTypeCode());
                pstSub.setString(9, tradeRelaSub.getSSubTsfTypeCode());
                pstSub.setString(10, tradeRelaSub.getSCuryCode());
                pstSub.setDouble(11, tradeRelaSub.getDBal());
                pstSub.setDouble(12, tradeRelaSub.getDMBal());
                pstSub.setDouble(13, tradeRelaSub.getDVBal());
                pstSub.setDouble(14, tradeRelaSub.getDBaseCuryBal());
                pstSub.setDouble(15, tradeRelaSub.getDMBaseCuryBal());
                pstSub.setDouble(16, tradeRelaSub.getDVBaseCuryBal());
                pstSub.setDouble(17, tradeRelaSub.getDPortCuryBal());
                pstSub.setDouble(18, tradeRelaSub.getDMPortCuryBal());
                pstSub.setDouble(19, tradeRelaSub.getDVPortCuryBal());
                pstSub.setString(20, tradeRelaSub.getSDesc());
                pstSub.setInt(21, tradeRelaSub.checkStateId);
                pstSub.setString(22, pub.getUserCode());
                pstSub.setString(23, YssFun.formatDatetime(new java.util.Date()));
                pstSub.setInt(24, tradeRelaSub.getIInOut());
                pstSub.setString(25, tradeRelaSub.getAttrClsCode().trim().length() == 0 ? " " : tradeRelaSub.getAttrClsCode()); //24
                pstSub.executeUpdate();
            }

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(yssPst);
            dbl.closeStatementFinal(pstSub);
        }

    }

    public ArrayList getSubaddList() {
        return subaddList;
    }

    public String getSubTradeNum() {
        return subTradeNum;
    }

    public void setList(ArrayList rela){
        this.addList.addAll(rela);
    }

    public void setSubList(ArrayList subRela){
        this.subaddList.addAll(subRela);
    }

    public void setAddList(TradeRelaBean tradeRela) {
        this.addList.add(tradeRela);
    }

    public void setSubaddList(TradeRelaSubBean tradeRelaSub) {
        this.subaddList.add(tradeRelaSub);
    }

    public void setSubTradeNum(String subTradeNum) {
        this.subTradeNum = subTradeNum;
    }

    public ArrayList getAddList() {
        return addList;
    }
}
