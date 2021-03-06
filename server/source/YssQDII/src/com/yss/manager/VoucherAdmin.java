package com.yss.manager;

import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.main.voucher.*;
import com.yss.util.*;
import com.yss.vsub.*;

public class VoucherAdmin
    extends BaseBean {

    private ArrayList addList = new ArrayList();

    public VoucherAdmin() {
    }

    public ArrayList getAddList() {
        return addList;
    }

    public void setAddList(ArrayList addList) {
        this.addList = addList;
    }

    public void addList(VchDataBean vchData, VchDataEntityBean vchDataEntity) {
        this.addList.add(vchData);

    }

    public void addList(VchDataBean vchData) {
        this.addList.add(vchData);
    }

    public void deleteVoucher(String vchTypes, java.util.Date startDate,
                              java.util.Date endDate, String portCodes) throws
        YssException {
        String strSql = "";
        ResultSet rsVchData = null;
        StringBuffer dataEntity = new StringBuffer();
        boolean bTrans = false;
        try {
            Connection conn = dbl.loadConnection();
            strSql =
                " select * from " + pub.yssGetTableName("Tb_Vch_Data") +
                " a join (select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FCheckState = 1 and FAttrCode in (" +
                operSql.sqlCodes(vchTypes) + ")) b on a.FVchTplCode = b.FVchTplCode" +
                " where " +
                " a.FVchDate between " + dbl.sqlDate(startDate) + " and " + // wdy add 20070903 添加表别名：a
                dbl.sqlDate(endDate) +
                " and  a.FPortCode in (" + operSql.sqlCodes(portCodes) + ")";
            rsVchData = dbl.openResultSet(strSql);
            //=====edit by xuxuming,20091029.MS00762,系统在做凭证生成时，要删除历史数据，系统跑出异常    QDV4招商2009年10月23日02_B 
            int iCount = 0;//临时记录查询出来的记录条数
            while (rsVchData.next()) {         	
                dataEntity.append(rsVchData.getString("FVchNum"));
                if(++iCount==900){//这样来保证每个段内的记录数不超过1000条
            		iCount = 0;
            		dataEntity.append(YssCons.YSS_ITEMSPLITMARK1);
            	}else{
            		dataEntity.append(",");
            	}
            }
            String str = "";
            str = dataEntity.append("A_B_C").toString();//不管dataEntity有没有值，都加上这个字符串，保证str不为空串
//            if (dataEntity.toString().length() > 1) {
//                str = dataEntity.toString().substring(0,
//                    dataEntity.toString().length() -
//                    1);
//            }
//            if (str.length() == 0) {
//                str = "'A_B_C'";
//            }
            String[] strAry = null;
            strAry = str.split(YssCons.YSS_ITEMSPLITMARK1);
            for(int i=0;i<strAry.length;i++){
            	strSql = "delete from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " where FVchNum in(" + operSql.sqlCodes(strAry[i]) +
                ")";
            dbl.executeSql(strSql);
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_Data") +
                " where FVchNum in(" + operSql.sqlCodes(strAry[i]) +
                ")";
            dbl.executeSql(strSql);
            }
            //============edit===end===========================================================
            /*//====== delete by xuxuming,因str中记录大于1000条时报错，屏掉这个方法，上面为修改后的。
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " where FVchNum in(" + operSql.sqlCodes(str) +
                ")";
            dbl.executeSql(strSql);
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_Data") +
                " where FVchNum in(" + operSql.sqlCodes(str) +
                ")";
              //==============delete===end====================    
              */
//         strSql = "delete from " + pub.yssGetTableName("Tb_Vch_Data") +
//               " a join (select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
//               " where FCheckState = 1 and FAttrCode in (" +
//               dbl.sqlString(vchTypes)+ ")) b on a.FVchTplCode = b.FVchTplCode" +
//               " where FVchDate between " +
//               dbl.sqlDate(startDate) + " and " +
//               dbl.sqlDate(endDate) +
//               " and  FPortCode in (" + operSql.sqlCodes(portCodes) + ")";
//            dbl.executeSql(strSql);

            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
        finally{
        	dbl.closeResultSetFinal(rsVchData);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
    }

    public void deleteVoucherWithNoCheck(String vchTypes, java.util.Date startDate,
                                         java.util.Date endDate, String portCodes) throws
        YssException {
        String strSql = "";
        ResultSet rsVchData = null;
        StringBuffer dataEntity = new StringBuffer();
        boolean bTrans = false;
        try {
            Connection conn = dbl.loadConnection();
            strSql =
                " select * from " + pub.yssGetTableName("Tb_Vch_Data") +
                " a join (select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " where FAttrCode in (" +
                operSql.sqlCodes(vchTypes) + ")) b on a.FVchTplCode = b.FVchTplCode" +
                " where " +
                " a.FVchDate between " + dbl.sqlDate(startDate) + " and " + // wdy add 20070903 添加表别名：a
                dbl.sqlDate(endDate) +
                " and  a.FPortCode in (" + operSql.sqlCodes(portCodes) + ")";
            rsVchData = dbl.openResultSet(strSql);
          //=====edit by xuxuming,20091029.MS00762,系统在做凭证生成时，要删除历史数据，系统跑出异常    QDV4招商2009年10月23日02_B 
            int iCount = 0;//临时记录查询出来的记录条数
            while (rsVchData.next()) {        	
                dataEntity.append(rsVchData.getString("FVchNum"));
                if(++iCount==900){//这样来保证每个段内的记录数不超过1000条
            		iCount = 0;
            		dataEntity.append(YssCons.YSS_ITEMSPLITMARK1);
            	}else{
            		dataEntity.append(",");
            	}
            }
            String str = "";
            str = dataEntity.append("A_B_C").toString();//不管dataEntity有没有值，都加上这个字符串，保证str不为空串
            String[] strAry = null;
            strAry = str.split(YssCons.YSS_ITEMSPLITMARK1);
            for(int i=0;i<strAry.length;i++){
            	strSql = "delete from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " where FVchNum in(" + operSql.sqlCodes(strAry[i]) +
                ")";
            dbl.executeSql(strSql);
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_Data") +
                " where FVchNum in(" + operSql.sqlCodes(strAry[i]) +
                ")";
            dbl.executeSql(strSql);
            }
            //============edit===end===========================================================
            /*//===delete by xuxuming,20091029.因下面的删除方法的IN字句中记录数超过1000时，报错。故屏掉，改后的代码添加在上面.
            while (rsVchData.next()) {
                dataEntity.append(rsVchData.getString("FVchNum")).append(",");
            }

            String str = "";
            if (dataEntity.toString().length() > 1) {
                str = dataEntity.toString().substring(0,
                    dataEntity.toString().length() -
                    1);
            }
            if (str.length() == 0) {
                str = "'A_B_C'";
            }
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " where FVchNum in(" + operSql.sqlCodes(str) +
                ")";
            dbl.executeSql(strSql);
            strSql = "delete from " + pub.yssGetTableName("Tb_Vch_Data") +
                " where FVchNum in(" + operSql.sqlCodes(str) +
                ")";

//         strSql = "delete from " + pub.yssGetTableName("Tb_Vch_Data") +
//               " a join (select * from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
//               " where FCheckState = 1 and FAttrCode in (" +
//               dbl.sqlString(vchTypes)+ ")) b on a.FVchTplCode = b.FVchTplCode" +
//               " where FVchDate between " +
//               dbl.sqlDate(startDate) + " and " +
//               dbl.sqlDate(endDate) +
//               " and  FPortCode in (" + operSql.sqlCodes(portCodes) + ")";
            dbl.executeSql(strSql);
*/         //============delete===end=======================================================================================
            conn.setAutoCommit(false);
            bTrans = true;
            dbl.executeSql(strSql);
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);

        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
        finally{
        	dbl.closeResultSetFinal(rsVchData);
        }
        //---add by songjie 2011.06.02 BUG 1965 QDV4工银2011年05月20日02_B---//
    }

    public void insert() throws YssException {
        insert("", null, null, "");
    }

    public void insert(String vchTypes, java.util.Date startDate,
                       java.util.Date endDate, String portCodes) throws
        YssException {
    	
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pstD = null;
//        PreparedStatement pstDEntity = null;
    	YssPreparedStatement yssPstD = null;
    	YssPreparedStatement yssPstDEntity = null;
        //===============end==================
        String strSql = "";
        VchDataBean vchData = null;
        VchDataEntityBean vchDataEntity = null;
        ArrayList vchDataEntitys = null;
        String strNumberDate = "";
        String vchNum = "";
        String entityNum = "";
        try {
            Connection conn = dbl.loadConnection();
            if (vchTypes.trim().length() > 0 && startDate != null && endDate != null && portCodes.trim().length() > 0) {
                deleteVoucher(vchTypes, startDate, endDate, portCodes);
            }
            strSql = " insert into " + pub.yssGetTableName("Tb_Vch_Data") +
                " (FVchNum,FVchDate,FPortCode,FBookSetCode,FCuryCode,FSrcCury," +
                " FCuryRate,FVchTplCode,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser )" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            pstD = conn.prepareStatement(strSql);
            yssPstD = dbl.getYssPreparedStatement(strSql);

            strSql =
                " insert into " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " (FVchNum,FEntityNum,FSubjectCode,FResume,FDCWay,FBookSetCode," +
                " FBal,FSetBal,FAmount,FPrice,FAssistant,FCuryRate,FDesc,FCheckState,FCreator,FCreateTime,FCheckUser )" +
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            pstDEntity = conn.prepareStatement(strSql);
            yssPstDEntity = dbl.getYssPreparedStatement(strSql);

            for (int i = 0; i < this.addList.size(); i++) {
                vchData = (VchDataBean) addList.get(i);
                vchDataEntitys = (ArrayList) vchData.getDataEntity();
                strNumberDate = YssFun.formatDate(
                    vchData.getVchDate(),
                    YssCons.YSS_DATETIMEFORMAT).
                    substring(0, 8);
                //edit by qiuxufeng 20101231 428 QDV4深圳赢时胜2010年12月18日02_A
                //优化凭证生成时的效率，第一个凭证编号通过查表获取，从第二个开始通过算号器算出下一个凭证编号
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B start---//
//                if(i == 0) {
//                	vchNum = "T" + strNumberDate +
//                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Vch_Data"),
//                                           dbl.sqlRight("FVchNum", 6),
//                                           "000000",
//                                        //20120807 modified by liubo.生成凭证时报tb_xxx_vch_data主键冲突
//                                        //===================================
//                                           " where 1=1 and FVchDate = " + dbl.sqlDate(vchData.getVchDate()), 1);
//                    					//==================end=================
//                } else {
//                	vchNum = calNextCode(vchNum);
//                }
                //---delete by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B end---//
                
                //add by songjie 2012.12.24 BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
                vchNum = getNum();
                
//                vchNum = "T" + strNumberDate +
//                    dbFun.getNextInnerCode(pub.yssGetTableName("Tb_Vch_Data"),
//                                           dbl.sqlRight("FVchNum", 6),
//                                           "000000",
//                                           " where 1=1", 1);
                yssPstD.setString(1, vchNum);
                yssPstD.setDate(2, YssFun.toSqlDate(vchData.getVchDate()));
                yssPstD.setString(3, vchData.getPortCode());
                yssPstD.setString(4, vchData.getBookSetCode());
                yssPstD.setString(5, vchData.getCuryCode());
                yssPstD.setString(6, vchData.getSrcCuryCode());
                yssPstD.setDouble(7, vchData.getCuryRate());
                yssPstD.setString(8, vchData.getTplCode());
                yssPstD.setString(9, " ");
                // modify by wangzuochun 2010.11.13  BUG #369 调度方案执行界面生成凭证问题 
                yssPstD.setInt(10, 0); // modify by wangzuochun 2010.06.29 MS01295    凭证方案执行，凭证借贷不平衡的情况下也能“成功导入”（7月份版本发布）    QDV4上海2010年04月27日01_B    
                yssPstD.setString(11, pub.getUserCode());
                yssPstD.setString(12, YssFun.formatDatetime(new java.util.Date()));
                yssPstD.setString(13, " "); // modify by wangzuochun 2010.11.13  BUG #369 调度方案执行界面生成凭证问题 
                yssPstD.executeUpdate();

                for (int j = 0; j < vchDataEntitys.size(); j++) {
                    vchDataEntity = (VchDataEntityBean) vchDataEntitys.get(j);
                    //   vchDataEntity.setYssPub(pub);
//               if (vchDataEntity.getBal()==0 && vchDataEntity.getSetBal()==0 && vchDataEntity.getAmount()==0){
//                  continue;
//               }//QDV4深圳2009年01月15日02_B MS00194 因为在凭证生成时已经判断过了凭证分录中为0的情况，这里不应该再判断处理。
                    //edit by qiuxufeng 20101231 428 QDV4深圳赢时胜2010年12月18日02_A
                    //优化凭证生成时的效率，每张凭证的分录从000001开始，第二条开始加1，不需要从数据库读取
                    if(j == 0) {
                    	entityNum = "000001";
                    } else {
                    	entityNum = calNextCode(entityNum);
                    }
//                    entityNum =
//                        dbFun.getNextInnerCode
//                        (pub.yssGetTableName("Tb_Vch_DataEntity"),
//                         dbl.sqlRight("FEntityNum", 6), "000001",
//                         " where FVchNum=" + dbl.sqlString(vchNum), 1);

                    yssPstDEntity.setString(1, vchNum);
                    yssPstDEntity.setString(2, entityNum);
                    yssPstDEntity.setString(3, vchDataEntity.getSubjectCode());
                    yssPstDEntity.setString(4, vchDataEntity.getResume());
                    yssPstDEntity.setString(5, vchDataEntity.getDcWay());
                    yssPstDEntity.setString(6, vchData.getBookSetCode());
                    yssPstDEntity.setDouble(7, vchDataEntity.getBal());
                    yssPstDEntity.setDouble(8, vchDataEntity.getSetBal());
                    yssPstDEntity.setDouble(9, vchDataEntity.getAmount());
                    yssPstDEntity.setDouble(10, vchDataEntity.getPrice());
                    yssPstDEntity.setString(11, vchDataEntity.getAssistant());
                    yssPstDEntity.setDouble(12, (vchDataEntity.getBal() != 0 ? YssD.div(vchDataEntity.getSetBal(), vchDataEntity.getBal(), 15) : 1));
                    yssPstDEntity.setString(13, " ");
                    yssPstDEntity.setInt(14, 1);
                    yssPstDEntity.setString(15, pub.getUserCode());
                    yssPstDEntity.setString(16,
                                         YssFun.formatDatetime(new java.util.Date()));
                    yssPstDEntity.setString(17, pub.getUserCode());
                    yssPstDEntity.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(yssPstDEntity);
            dbl.closeStatementFinal(yssPstD);

        }
    }

//-----------------------------------------------------------------------

    public void deleteAcc(String sTplCodes, java.util.Date startDate,
                          java.util.Date endDate) throws
        YssException {
        String strSql = "";
        ResultSet rs = null;
        boolean bTrans = false;
        YssFinance fc = new YssFinance();
        Connection conn = dbl.loadConnection();
        java.util.Date dDate;
        int iYearNum = 0;
        String sVchTWays = "", sVchInds = "";
        try {
            iYearNum = YssFun.getYear(endDate) - YssFun.getYear(startDate);
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
                " where a.FVchTplCode in (" + operSql.sqlCodes(sTplCodes) + // wdy add 20070903 添加表别名：a
                ")";
            rs = dbl.openResultSet(strSql);
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
            strSql = "select distinct a.FBookSetCode from " +
                pub.yssGetTableName("Tb_Vch_Data") +
                " a where a.FVchTplCode in (" + operSql.sqlCodes(sTplCodes) +
                ") and a.FCheckState = 1 and exists(select * from lsetlist" +//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
//                pub.yssGetTableName("tb_vch_bookset") +
                " where FSetCode= to_number(a.FBookSetCode))"; //导入财务时，应该只把凭证浏览里面已审核的凭证导入财务，20070919，杨
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                dDate = startDate;
                for (int i = 0; i <= iYearNum; i++) {
                    strSql = " delete from " +
                        fc.getCWTabName(dDate,
                                        rs.getString("FBookSetCode"), "fcwvch") +
                        " where 	Fdate between " +
                        dbl.sqlDate(startDate) +
                        " and " +
                        dbl.sqlDate(endDate) +
                        " and FPzLy <> 'HD' " +
                        " and FPzLy in (" + operSql.sqlCodes(sVchInds) +
                        ") and FZqJyFs in (" + operSql.sqlCodes(sVchTWays) + ")";
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

    public void insert(String vchTplCodes,
                       java.util.Date startDate,
                       java.util.Date endDate) throws YssException {
        ResultSet vchDataRs = null;
        ResultSet vchDataEntityRs = null;
        String strSql = "";
        //StringBuffer vchNumBuf = new StringBuffer();//huangqirong 2012-07-01 STORY #2475 根据FindBugs工具，对系统进行全面检查，修改发现的问题
        String vchNums = "";
        //modified by liubo.Story #1757
        //将原本的PreparedStatement对象换成YssPreparedStatement对象，方便在执行出错时可以捕捉出错的语句和值，然后写进errorlog里面
        //=================================
//        PreparedStatement pstm = null;
        YssPreparedStatement yssPstm = null;
        //==============end===================
        String strDate = "";
        String[] arrDate = null;
        int vchNum = 1;
        int enId = 1;
        String sTmp = "";
        YssFinance cw = null;
        try {
            cw = new YssFinance();
            cw.setYssPub(pub);
            Connection conn = dbl.loadConnection();
            deleteAcc(vchTplCodes, startDate, endDate);
            strSql = " select * from " + pub.yssGetTableName("Tb_Vch_Data") +
                " a left join (select b1.*,b2.FVchInd from " + pub.yssGetTableName("Tb_Vch_VchTpl") +
                " b1 left join (select FAttrCode,FVchInd from " + pub.yssGetTableName("Tb_Vch_Attr") +
                " where FCheckState = 1) b2 on b1.FAttrCode = b2.FAttrCode" +
                " where FCheckState = 1) b on a.FVchTplCode = b.FVchTplCode" +
                " where a.FVchTplCode in (" + operSql.sqlCodes(vchTplCodes) + ") and a.FCheckState = 1" +
                " and a.FVchDate between " + dbl.sqlDate(startDate) + " and " +
                dbl.sqlDate(endDate) +
                " and exists(select FBookSetCode from " + //使用 exists 判断 tb_vch_bookset 表中是否有套帐的数据 2007.12.17 蒋锦
                " lsetlist where FBookSetCode= to_number(a.FBookSetCode))";//modified by yeshenghong 20130428 BUG7486   套账链接设置 无用 去掉
            vchDataRs = dbl.openResultSet(strSql);
            while (vchDataRs.next()) {
//            vchNumBuf.append(vchDataRs.getString("FVchNum")).append(",");

//         }
//         if(vchNumBuf.length()>1)
//         {
//            vchNums=vchNumBuf.substring(0,vchNumBuf.toString().length()-1);
//         }
                enId = 1;
                strSql =
                    " select a.* ,b.* from " +
                    " (select * from " + pub.yssGetTableName("tb_vch_dataentity") +
                    ")a left join" +
                    " (select m.*,n.FAttrCode from " +
                    " (select * from " + pub.yssGetTableName("tb_vch_data") + ")m left join" +
                    " (select FVchTplCode,FAttrCode from " +
                    pub.yssGetTableName("tb_vch_vchtpl") + ")" +
                    "  n on m.FVchTplCode=n.FVchTplCode " +
                    "  )b on b.FVchNum=a.FVchNum " +
                    " where a.FVchNum = " +
                    dbl.sqlString(vchDataRs.getString("FVchNum")) + " order by a.fdcway"; //先借后贷fazmm20071007
                vchDataEntityRs = dbl.openResultSet(strSql);
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
                    " Fpz1,Fpz2,FFromSet,FToLevel,FUpLoad,FAuxiAcc)" +
                    " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//                pstm = conn.prepareStatement(strSql);
                yssPstm = dbl.getYssPreparedStatement(strSql);
                while (vchDataEntityRs.next()) {
                    strDate = vchDataEntityRs.getString("FVchDate");
//             vchNum=vchDataEntityRs.getString("FVchNum");
                    arrDate = strDate.split("-");
                    yssPstm.setInt(1, YssFun.getMonth(vchDataRs.getDate("FVchDate")));
                    yssPstm.setString(2, " ");
                    System.out.println("凭证号：" + vchDataEntityRs.getString("FVchNum") + "         分录号:" + vchDataEntityRs.getString("Fentitynum"));
                    yssPstm.setInt(3, vchNum);
                    yssPstm.setInt(4, enId);
                    yssPstm.setString(5, vchDataEntityRs.getString("FResume"));
                    yssPstm.setString(6, vchDataEntityRs.getString("FSubjectCode"));
                    yssPstm.setString(7, cw.getCWAccountCury(vchDataEntityRs.getString("FSubjectCode"), vchDataRs.getDate("FVchdate"), vchDataRs.
                        getString("FPortCode")));
                    yssPstm.setDouble(8, vchDataEntityRs.getDouble("FCuryRate"));
                    yssPstm.setInt(9, 0);
                    yssPstm.setDouble(10, vchDataEntityRs.getDouble("FBal"));
                    yssPstm.setString(11,
                                   (vchDataEntityRs.getString("FDCWay").equalsIgnoreCase("0") || vchDataEntityRs.getString("FDCWay").equalsIgnoreCase("J") ? "J" : "D"));
                    yssPstm.setDouble(12, vchDataEntityRs.getDouble("FSetBal"));
                    yssPstm.setDouble(13, vchDataEntityRs.getDouble("FAmount"));
                    yssPstm.setDouble(14, vchDataEntityRs.getDouble("FAmount"));

                    yssPstm.setDouble(15, vchDataEntityRs.getDouble("FPrice"));
                    yssPstm.setDate(16, vchDataEntityRs.getDate("FVchDate"));
                    yssPstm.setDate(17, vchDataEntityRs.getDate("FVchDate"));
                    yssPstm.setInt(18, 0);
                    yssPstm.setString(19, pub.getUserName());
                    yssPstm.setString(20, " "); //导入到财务里面的凭证是为“未审核”状态
                    yssPstm.setString(21, " ");
                    yssPstm.setString(22, " ");
                    yssPstm.setInt(23, 0);
                    yssPstm.setString(24, (vchDataRs.getString("FVchInd") == null ? " " : vchDataRs.getString("FVchInd")));
                    yssPstm.setString(25, (vchDataRs.getString("FVchTWay") == null ? " " : vchDataRs.getString("FVchTWay")));
                    yssPstm.setString(26, " ");
                    yssPstm.setLong(27, 1);
                    yssPstm.setString(28, " ");
                    yssPstm.setString(29, " ");
                    yssPstm.setString(30, " ");
                    yssPstm.setInt(31, 0);
                    yssPstm.setInt(32, 0);
                    yssPstm.setInt(33, 0);
                    yssPstm.setString(34,
                                   (vchDataEntityRs.getString("FAssistant") == null ?
                                    " " :
                                    vchDataEntityRs.getString("FAssistant")));
                    enId++;
                    yssPstm.executeUpdate();
                }
                dbl.closeResultSetFinal(vchDataEntityRs);
                dbl.closeStatementFinal(yssPstm);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeStatementFinal(yssPstm);
            dbl.closeResultSetFinal(vchDataRs);
            dbl.closeResultSetFinal(vchDataEntityRs);
        }
    }

    public String getMdbData(String vchTplCodes) throws YssException {
        ResultSet vchDataRs = null;
        ResultSet vchDataEntityRs = null;
        String strSql = "";
        StringBuffer vchNumBuf = new StringBuffer();
        String vchNums = "";
        VchDataBean vchData = null;
        VchDataEntityBean vchDataEntity = null;

        StringBuffer vchDataBuf = new StringBuffer();
        StringBuffer vchDataEntityBuf = new StringBuffer();

        String vchDatas = "";
        String vchDataEntitys = "";
        try {
            strSql = " select * from " + pub.yssGetTableName("Tb_Vch_Data") +
                " where FVchTplCode in (" + operSql.sqlCodes(vchTplCodes) + ")";
            vchDataRs = dbl.openResultSet(strSql);
            while (vchDataRs.next()) {
                vchNumBuf.append(vchDataRs.getString("FVchNum")).append(",");
                vchData = new VchDataBean();
                vchData.setYssPub(pub);
                vchData.setVchNum(vchDataRs.getString("FVchNum"));
                vchData.setVchDate(vchDataRs.getDate("FVchDate"));
                vchData.setPortCode(vchDataRs.getString("FPortCode"));
                vchData.setBookSetCode(vchDataRs.getString("FBookSetCode"));
                vchData.setCuryCode(vchDataRs.getString("FCuryCode"));
                vchData.setCuryRate(vchDataRs.getDouble("FCuryRate"));
                vchData.setTplCode(vchDataRs.getString("FVchTplCode"));
                vchData.setDesc(vchDataRs.getString("FDesc"));

                vchDataBuf.append(vchData.buildRowStr()).append("\r\f");
            }
            if (vchNumBuf.length() > 1) {
                vchNums = vchNumBuf.toString().substring(0,
                    vchNumBuf.toString().length() - 1);
            } else {
                vchNums = "A_B_C";
            }
            strSql = " select * from " + pub.yssGetTableName("Tb_Vch_DataEntity") +
                " where FVchNum in (" + operSql.sqlCodes(vchNums) + ")";
            vchDataEntityRs = dbl.openResultSet(strSql);
            while (vchDataEntityRs.next()) {
                vchDataEntity = new VchDataEntityBean();
                vchDataEntity.setVchNum(vchDataEntityRs.getString("FVchNum"));
                vchDataEntity.setEntityNum(vchDataEntityRs.getString("FEntityNum"));
                vchDataEntity.setSubjectCode(vchDataEntityRs.getString(
                    "FSubjectCode"));
                vchDataEntity.setResume(vchDataEntityRs.getString("FResume"));
                vchDataEntity.setDcWay(vchDataEntityRs.getString("FDCWay"));
                vchDataEntity.setBookSetCode(vchDataEntityRs.getString(
                    "FBookSetCode"));
                vchDataEntity.setBal(vchDataEntityRs.getDouble("FBal"));
                vchDataEntity.setSetBal(vchDataEntityRs.getDouble("FSetBal"));
                vchDataEntity.setAmount(vchDataEntityRs.getDouble("FAmount"));
                vchDataEntity.setPrice(vchDataEntityRs.getDouble("FPrice"));
                vchDataEntity.setDesc(vchDataEntityRs.getString("FDesc"));

                vchDataEntityBuf.append(vchDataEntity.buildRowStr()).append("\r\f");

            }
            if (vchDataBuf.length() > 2) {
                vchDatas = vchDataBuf.toString().substring(0,
                    vchDataBuf.toString().length() - 2);
            }
            if (vchDataEntityBuf.length() > 2) {
                vchDataEntitys = vchDataEntityBuf.toString().substring(0,
                    vchDataEntityBuf.toString().length() - 2);
            }

            return vchDatas + "\f\f" + vchDataEntitys;
        } catch (Exception e) {
            throw new YssException(e.getMessage(), e);
        } finally {
            dbl.closeResultSetFinal(vchDataRs);
            dbl.closeResultSetFinal(vchDataEntityRs);
        }

    }
    
    public String calNextCode(String currentCode) throws YssException {
    	return calNextCode(currentCode, 6, 1);
    }
    
    /**
     * add by qiuxufeng 20101231 428 QDV4深圳赢时胜2010年12月18日02_A
     * 以当前凭证编号计算下一个编号
     * @throws YssException
     * @方法名：calNextCode
     * @参数：currentCode String 当前编号
     * @参数：cutLen int 从编号末尾截取位数
     * @参数：addNum int 编号递加值
     * @返回类型：String
     */
    public String calNextCode(String currentCode, int cutLen, int addNum) throws YssException {
    	String sFormat = "";
    	String tmp1 = currentCode.substring(0, currentCode.length() - cutLen);
    	String tmp2 = currentCode.substring(currentCode.length() - cutLen, currentCode.length());
    	String defaultMaxNum = ""; //add by huangqirong 2012-11-26 bug #6354 默认最大 编号
    	if(YssFun.isNumeric(tmp2)) {
    		int iNum = Integer.parseInt(tmp2);
    		iNum += addNum;
        	if (cutLen > 0) {
                for (int j = 0; j < cutLen; j++) {
                    sFormat += "0";
                    defaultMaxNum +="9";//add by huangqirong 2012-11-26 bug #6354 
                }
                int imaxNum = Integer.parseInt(defaultMaxNum);//add by huangqirong 2012-11-26 bug #6354
                
                if(iNum > imaxNum)//add by huangqirong 2012-11-26 bug #6354
                	sFormat +="0";//add by huangqirong 2012-11-26 bug #6354
                
                tmp2 = YssFun.formatNumber(iNum, sFormat);
            }
    	} else {
    		throw new YssException("计算凭证编号出现异常");
    	}
    	return tmp1 + tmp2;
    }
    
    /**
     * add by songjie 2012.12.24
     * BUG 6694 QDV4赢时胜(上海开发)2012年12月18日02_B
     * @return
     * @throws YssException
     */
    public String getNum() throws YssException{
    	ResultSet rs = null;
    	String strSql = "";
    	String num = "";
    	try{
    		if(!dbl.yssSequenceExist("SEQ_" + pub.getPrefixTB() + "_Vch_Data")){
				int maxNum = 0;
				strSql = " select max(FVchNum) as FNum from " + pub.yssGetTableName("Tb_Vch_Data") + 
				" where subStr(FVchNum,0,1) <> 'T' ";
				rs = dbl.openResultSet(strSql);
				if(rs.next()){
					if(rs.getString("FNum") != null && YssFun.isNumeric(rs.getString("FNum"))){
						maxNum = Integer.parseInt(rs.getString("FNum"));
					}
				}
				
				dbl.closeResultSetFinal(rs);
				
				maxNum++;
				
				strSql = " create sequence SEQ_" + pub.getPrefixTB() + "_Vch_Data " +
				" minvalue 1 " + 
				" maxvalue 99999999999999999999 " + 
				" start with " + maxNum +
				" increment by 1 " +
				" cache 20 " + 
				" order ";
			
				dbl.executeSql(strSql);
			}
    		
    		strSql = " select trim(to_char(SEQ_" + pub.getPrefixTB() + 
    		"_Vch_Data.NextVal,'00000000000000000000')) as FNum from dual ";
    		rs = dbl.openResultSet(strSql);
    		if(rs.next()){
    			num = rs.getString("FNum");
    		}
		
    		return num;
    	}catch(Exception e){
			throw new YssException("获取最大编号出错!\n", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
    }
}
