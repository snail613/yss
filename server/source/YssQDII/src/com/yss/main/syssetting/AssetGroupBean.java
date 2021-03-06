package com.yss.main.syssetting;

/**
 *
 * <p>Title: AssetGroupBean</p>
 * <p>Description: 组合群设置</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
import java.sql.*;

import com.yss.dsub.*;
import com.yss.main.dao.IYssConvert;
import com.yss.main.funsetting.*;
import com.yss.util.*;

public class AssetGroupBean
    extends BaseDataSettingBean implements IYssConvert {  //--- modify by wangzuochun 2010.10.23 BUG #177::删除组合群信息的时候报错----QDV4赢时胜(测试)2010年10月21日03_B.xls

    private String grouNodeCode;
    private String grouNodeName;
    private String grouParentCode;
    private int oldorderCode;
    private String grouSectorCode;
    private String grouDesc;
    private String grouCheckTime;
    private String grouCheckUserName;
    private String grouCheckUserCode;
    private String grouCreatorTime;
    private String grouCreatorName;
    private String grouCreatorCode;
    private String grouCheckStateName;
    private int grouCheckStateId;
    
    private boolean isMultiGroup=false;//add by guyichuan 20110608 STORY #897 标识是否是多组合处理
    private boolean bETF = false;//判断是否只加载资产类型和资产子类型为ETF基金的组合 panjunfang add 20091010， MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A

    public AssetGroupBean() {
    }
    
    public String getListViewData() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        try {
            sHeader = "代码\t名称\t描述";
            strSql = "select * from Tb_Sys_AssetGroup order by FAssetGroupCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FAssetGroupCode")).append("\t");
                bufShow.append(rs.getString("FAssetGroupName")).append("\t");
                bufShow.append(rs.getString("FDesc")).append(YssCons.
                    YSS_LINESPLITMARK);

                bufAll.append(rs.getString("FAssetGroupCode")).append("\t");
                bufAll.append(rs.getString("FAssetGroupName")).append("\t");
                bufAll.append(rs.getString("FMaxNum")).append("\t");
                bufAll.append(rs.getString("FStartDate")).append("\t");
                bufAll.append(rs.getString("FBaseCury")).append("\t");
                bufAll.append(rs.getString("FBaseRateSrcCode")).append("\t");
                bufAll.append(rs.getString("FBaseRateCode")).append("\t");
                bufAll.append(rs.getBoolean("FLocked")).append("\t");
                bufAll.append(rs.getString("FDesc")).append("\t").append(YssCons.
                    YSS_LINESPLITMARK);
            }
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }

            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
            }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取组合群信息出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * getListViewData1
     * by guyichuan 20110608 STORY #897 
     * 根据“多组合处理”的选择加载组合群
     * @return String
     * @throws YssException 
     */
    public String getListViewData1() throws YssException {
        String sHeader = "";
        String sShowDataStr = "";
        String sAllDataStr = "";
        StringBuffer bufShow = new StringBuffer();
        StringBuffer bufAll = new StringBuffer();
        ResultSet rs = null;
        StringBuffer bufSql=null;
        try {
            sHeader = "组合群代码\t组合群名称";   
            bufSql=new StringBuffer();
            
            if(this.isMultiGroup){//多组合群
            	bufSql.append("select * from TB_SYS_ASSETGROUP order by FAssetGroupCode");
            }else{
            	bufSql.append("select * from TB_SYS_ASSETGROUP");
            	bufSql.append(" where FAssetGroupCode="+dbl.sqlString(pub.getAssetGroupCode()));
            }
            	rs = dbl.openResultSet(bufSql.toString());
                while (rs.next()) {
                	bufShow.append(rs.getString("FAssetGroupCode")).append("\t");
                    bufShow.append(rs.getString("FAssetGroupName")).append("\t").append(YssCons.
                            YSS_LINESPLITMARK);
                   

                    bufAll.append(rs.getString("FAssetGroupCode")).append("\t");
                    bufAll.append(rs.getString("FAssetGroupName")).append("\t");
                    bufAll.append(rs.getString("FMaxNum")).append("\t");
                    bufAll.append(rs.getString("FStartDate")).append("\t");
                    bufAll.append(rs.getString("FBaseCury")).append("\t");
                    bufAll.append(rs.getString("FBaseRateSrcCode")).append("\t");
                    bufAll.append(rs.getString("FBaseRateCode")).append("\t");
                    bufAll.append(rs.getBoolean("FLocked")).append("\t");
                    bufAll.append(rs.getString("FDesc")).append("\t").append(YssCons.
                        YSS_LINESPLITMARK);
                }
                if (bufShow.toString().length() > 2) {
                    sShowDataStr = bufShow.toString().substring(0,
                        bufShow.toString().length() - 2);
                }

                if (bufAll.toString().length() > 2) {
                    sAllDataStr = bufAll.toString().substring(0,
                        bufAll.toString().length() - 2);
                }

            return sHeader + "\r\f" + sShowDataStr + "\r\f" + sAllDataStr;
        } catch (Exception e) {
            throw new YssException("获取组合群数据出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    /**
     * TreeView获取组合群信息 MS00036
     * 加载全部可用组合群下的当前用户下的可用组合
     * panjunfang modify 20090917 如果操作组合下没有对应的明细组合，则前台界面无需显示该操作组合
     * 20081203  王晓光
     * @return String
     * @throws YssException
     */
    public String getTreeViewData() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        ResultSet prs = null;
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufSql = new StringBuffer();
        StringBuffer bufTmp = new StringBuffer();//panjunfang modify 20090917 用于临时存放操作组合，当操作组合下有明细组合时才将操作组合传至前台
        int tmpInt = 0;//判断操作组合下是否有明细组合,值大于0代表有 panjunfang add 20090916
        int GroupOrderCode = 0;
        int PortinfoOrderCode = 0;
        int detailPortOrderCode = 0; //操作组合中明晰组合的排序代码 2009.04.29 蒋锦 添加 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
        String parentCode = "";
        String NodeCode = "";
        String NodeName = "";
        String NodeOrderCode = "";
        String OperPortCode = ""; //操作组合的菜单代码 2009.04.29 蒋锦 添加 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
        String sPortCode = ""; //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 添加一个组合代码传到前台
        boolean bFirstRow = true;//用于判断是否将该组合群传送到前台界面显示，如果该组合群没有对应的操作组合，则前台界面不显示该组合群
        

        //20121128 added by liubo.海富通测试问题：调度方案执行、收益支付等界面的组合选择框没有考虑权限继承的问题
        //============================================
        RightBean right = new RightBean();
        try {
        	right.setYssPub(pub);
        	right.setUserCode(pub.getUserCode());
            //==============end==============================
        	
            strSql = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1 order by FAssetGroupCode ";
            rs = dbl.openResultSet(strSql); //取组合群
            while (rs.next()) {

                //判断操作组合表是否存在，如果不存在，提示用户要更新完所有组合群才可进行跨组合群操作 sunkey@Modify 20090818
                if(!dbl.yssTableExist("Tb_" + rs.getString("FTabPreFix") + "_Para_Operportfolio")){
                    throw new YssException("对不起，请更新完所有组合群后再进行跨组合群处理!");
                }

                //2009-5-21 蒋锦 修改
                //MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合
                //在数的节点中增加操作组合，并要查出在操作组合中的组合和不在操作组合中的组合
                if (dbl.yssTableExist("Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio")) {
                    bufSql.append(" SELECT * ");
                    bufSql.append(" FROM (SELECT a.FOperPortCode, a.FOperPortName, b.FPortCode, b.FPortName, ");
                    bufSql.append(" a.fporttype, 0 AS FType ");
                    bufSql.append(" FROM (SELECT * ");
                    bufSql.append(" FROM Tb_" + rs.getString("FTabPreFix") + "_Para_Operportfolio ");
                    bufSql.append(" WHERE FCheckState = 1) a ");
                    bufSql.append(" LEFT JOIN (select y.FPortCode, y.FPortName ");
                 
                    bufSql.append(" from (select FPortCode ");
                    bufSql.append(" from Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio ");
                    bufSql.append(" where  ");
                    
                    //end by lidaolong
                    bufSql.append("  FCheckState = 1 ");
                    bufSql.append(" and FEnabled = 1 ");
                    if(bETF){
                        bufSql.append(" AND FAssetType = '01' AND FSubAssetType = '0106' ");
                    }
                    bufSql.append(" and FASSETGROUPCODE = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" ) x ");
                    bufSql.append(" join (select a.* ");
                    bufSql.append(" from Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio a ");
                    bufSql.append(" join (select DISTINCT FPortCode ");
                    bufSql.append(" from Tb_Sys_Userright ");
                    bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" and FUserCode = ").append(dbl.sqlString(pub.getUserCode()));
                    bufSql.append(" and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") d on a.FPortCode = ");
                    bufSql.append(" d.FPortCode ");
                    bufSql.append(" where a.fcheckstate = 1 ");
                    bufSql.append(" and a.FEnabled = 1) y on x.FPortCode = ");
                    bufSql.append(" y.FPortCode ");
                   
                    bufSql.append(" ) b ON a.fportcode = ");
                    bufSql.append(" b.FPortCode ");
                    bufSql.append(" WHERE (FPortType = 0 ) OR (FPortName IS NOT NULL)");
                    bufSql.append(" UNION ALL ");
                    bufSql.append(" select ' ' AS FOperPortCode, ' ' AS FOperPortName, y.FPortCode, ");
                    bufSql.append(" y.FPortName, 1 AS fporttype, 1 AS FType ");
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
              
                    bufSql.append(" from (select FPortCode ");
                    bufSql.append(" from Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio ");
                    bufSql.append(" where ");
                    
                    
                    
                    //end by lidaolong
                    bufSql.append("  FCheckState = 1 ");
                    bufSql.append(" and FEnabled = 1 ");
                    if(bETF){
                        bufSql.append(" AND FAssetType = '01' AND FSubAssetType = '0106' ");
                    }
                    bufSql.append(" and FASSETGROUPCODE = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" ) x ");
                    bufSql.append(" join (select a.* ");
                    bufSql.append(" from Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio a ");
                    bufSql.append(" join (select DISTINCT FPortCode ");
                    bufSql.append(" from Tb_Sys_Userright ");
                    bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" and (FUserCode = ").append(dbl.sqlString(pub.getUserCode()));
                    //20121128 added by liubo.海富通测试问题：调度方案执行、收益支付等界面的组合选择框没有考虑权限继承的问题
                    //============================================
                    bufSql.append(right.getInheritedRights(rs.getString("FAssetGroupCode"), "")).append(")");
                    //================end============================
                    bufSql.append(" and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") d on a.FPortCode = ");
                    bufSql.append(" d.FPortCode ");
                    bufSql.append(" where a.fcheckstate = 1 ");
                    bufSql.append(" and a.FEnabled = 1) y on x.FPortCode = y.FPortCode ");
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                /*    bufSql.append(" and x.FStartDate = y.FStartDate ");*/
                    
                    //end by lidaolong
                    bufSql.append(" WHERE y.FPortCode NOT IN ");
                    bufSql.append(" (SELECT FPortCode ");
                    bufSql.append(" FROM Tb_" + rs.getString("FTabPreFix") + "_Para_Operportfolio ");
                    bufSql.append(" WHERE FCheckState = 1)) atab ");
                    //========================== 增加组合的筛选 ===========================
                    //MS00003-QDV4.1赢时胜上海2009年2月1日03_A: 参数设置布局分散不便操作
                    //add by sunkey 20090411
                    //流程的相关信息是存放在pub里的，如果取得到流程的信息就要对组合进行筛选
                    if (pub.getFlow() != null) {
                        FlowBean flow = (FlowBean) pub.getFlow().get(pub.getUserCode());
                        if (flow != null) {
                            String tmpPorts = flow.getFPorts();
                            if (tmpPorts != null && !tmpPorts.trim().equals("")) {
                                //将组合作为筛选条件放入上述sql语句
                                bufSql.append(" Where atab.FPortCode in (" + operSql.sqlCodes(tmpPorts) + ")");
                            }
                        }
                    }
                    //=============================End MS00003===========================

                    bufSql.append(" ORDER BY FType, FOperPortCode, atab.FPortType, FType, FPortCOde ");

                    prs = dbl.openResultSet(bufSql.toString());
                    while (prs.next()) {
                        if (bFirstRow) {//如果该组合群下有操作组合，则在且仅在结果集的第一行将该组合群代码和名称添加到bufAll中返回至前台 panjunfang modify 20090825
                            NodeCode = rs.getString("FAssetGroupCode");
                            NodeName = rs.getString("FAssetGroupName");
                            NodeOrderCode = YssFun.formatNumber(GroupOrderCode++, "000");
                            parentCode = "[root]";
                            bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                            //没有组合代码就是空格
                            bufAll.append(" ").append(YssCons.YSS_LINESPLITMARK);
                            //取当前用户下可用组合
                            //2009.04.29 蒋锦 修改 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
                            //修改了 SQL 语句，获取可用组合的部分 TB_Sys_UserRight 中可用组合的储存方式已在 MS00010 中被修改
                            bFirstRow = false;//此处设为false避免在循环下个操作组合的时候重复加载该组合群。panjunfang modify 20090825
                        }
                        //拼接操作组合和操作组合中的明晰组合
                        if (prs.getInt("FType") == 0) {
                            //拼接操作组合
                            if (prs.getDouble("fporttype") == 0) {
                                NodeCode = rs.getString("FAssetGroupCode") + "-" +
                                    prs.getString("FOperPortCode");
                                NodeName = prs.getString("FOperPortName");
                                NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1, "000") + YssFun.formatNumber(PortinfoOrderCode++,
                                    "000");
                                OperPortCode = NodeCode;
                                parentCode = rs.getString("FAssetGroupCode");
                                sPortCode = " "; //没有组合代码就是空格
                                detailPortOrderCode = 0;
                                tmpInt = 0;
                                //将操作组合临时存放，如果有明细组合则在存放明细组合前先存放操作组合
                                bufTmp = new StringBuffer();//清空上一个操作组合的数据
                                bufTmp.append(NodeCode).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufTmp.append(NodeName).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufTmp.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufTmp.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufTmp.append(sPortCode).append(YssCons.YSS_LINESPLITMARK); //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                            } else { //拼接操作组合中的明晰组合
                                NodeCode = rs.getString("FAssetGroupCode") + "-" +
                                    prs.getString("FOperPortCode") + "-" +
                                    prs.getString("FPortCode");
                                NodeName = prs.getString("FPortName");
                                NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1,
                                    "000") + YssFun.formatNumber(PortinfoOrderCode - 1,
                                    "000") + YssFun.formatNumber(detailPortOrderCode++, "000");
                                parentCode = OperPortCode;
                                sPortCode = prs.getString("FPortCode");
                                tmpInt ++ ;

                                if(tmpInt == 1){//存放明细组合前先存放操作组合，保证前台显示正确
                                    bufAll.append(bufTmp.toString());
                                }
                                bufAll.append(NodeCode).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufAll.append(NodeName).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufAll.append(sPortCode).append(YssCons.YSS_LINESPLITMARK); //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                            }
                        } else { //拼接未包含在操作组合中的明晰组合
                            NodeCode = rs.getString("FAssetGroupCode") + "-" + prs.getString("FPortCode");
                            NodeName = prs.getString("FPortName");
                            NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1,
                                "000") + YssFun.formatNumber(PortinfoOrderCode++,
                                "000");
                            parentCode = rs.getString("FAssetGroupCode");
                            sPortCode = prs.getString("FPortCode");

                            bufAll.append(NodeCode).append(YssCons.
                                YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeName).append(YssCons.
                                YSS_ITEMSPLITMARK1);
                            bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(sPortCode).append(YssCons.YSS_LINESPLITMARK); //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                        }
                    } //end port while
                    bufSql.delete(0, bufSql.length());
                    dbl.closeResultSetFinal(prs); //close the cursor modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
                    bFirstRow = true;//当前组合群循环完毕后将bFirstRow设回为true再进行下个组合群的判断。panjunfang modify 20090825
                }
            } //end group while
            sResult = bufAll.toString();
            if (sResult.endsWith(YssCons.YSS_LINESPLITMARK)) {
                sResult = sResult.substring(0, sResult.length() - YssCons.YSS_LINESPLITMARK.length());
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取组合群下可用组合信息出错", ex);
        } finally {
            bufAll = null;
            dbl.closeResultSetFinal(prs, rs);
        }
    }
    
    /**
     * TreeView获取组合群信息 MS00036
     * 加载全部可用组合群下的当前用户下的可用组合
     * panjunfang modify 20090917 如果操作组合下没有对应的明细组合，则前台界面无需显示该操作组合
     * 20081203  王晓光
     * @return String
     * @throws YssException
     */
    public String getTreeViewDataNew() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        ResultSet prs = null;
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufSql = new StringBuffer();
        StringBuffer bufTmp = new StringBuffer();//panjunfang modify 20090917 用于临时存放操作组合，当操作组合下有明细组合时才将操作组合传至前台
        int tmpInt = 0;//判断操作组合下是否有明细组合,值大于0代表有 panjunfang add 20090916
        int GroupOrderCode = 0;
        int PortinfoOrderCode = 0;
        int detailPortOrderCode = 0; //操作组合中明晰组合的排序代码 2009.04.29 蒋锦 添加 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
        String parentCode = "";
        String NodeCode = "";
        String NodeName = "";
        String NodeOrderCode = "";
        String OperPortCode = ""; //操作组合的菜单代码 2009.04.29 蒋锦 添加 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
        String sPortCode = ""; //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 添加一个组合代码传到前台
        boolean bFirstRow = true;//用于判断是否将该组合群传送到前台界面显示，如果该组合群没有对应的操作组合，则前台界面不显示该组合群
        

        //20121128 added by liubo.海富通测试问题：调度方案执行、收益支付等界面的组合选择框没有考虑权限继承的问题
        //============================================
        RightBean right = new RightBean();
        try {
        	right.setYssPub(pub);
        	right.setUserCode(pub.getUserCode());
            //==============end==============================
        	// add by yeshenghong story3702  数据中心的操作接口  20130418
            strSql = " select a.*,b.fassetgroupname from ( select fassetgroupcode, fportcode from tb_sys_userright u where u.fusercode = " + dbl.sqlString(pub.getUserCode()) +  
            		 " and frightcode = 'datacenter' and fopertypes like '%execute%' " + 
            		 " union select fassetgroupcode, fportcode from tb_sys_userright u " +  
            		 " join (select * from tb_sys_roleright r where frightcode = 'datacenter' and fopertypes like '%execute%') r " + 
            		 " on u.frightcode = r.frolecode where  u.fusercode = " + dbl.sqlString(pub.getUserCode()) +  " and u.frightind = 'Role' and u.fportcode <> ' ' " + 
            		 " union " +
            		 " select u.fassetgroupcode, u.fportcode from tb_sys_userright u join  " + 
            		 " (select ftrustor,substr(fportcodelist,1,instr(fportcodelist,'>>')-1) as fassetgroupcode, " + 
            		 " substr(fportcodelist,instr(fportcodelist,'>>')+2) as fportcode " +
            		 " from tb_sys_perinheritance  where ftrustee = " + dbl.sqlString(pub.getUserCode()) +  
            		 " and fstartdate < sysdate and fenddate > sysdate) p  on " +
            		 " u.fusercode = p.ftrustor and u.fassetgroupcode = p.fassetgroupcode and u.fportcode = p.fportcode " +
            		 " where u.frightcode = 'datacenter' and u.fopertypes like '%execute%' " +
            		 " ) a join tb_sys_assetgroup b on a.fassetgroupcode = b.fassetgroupcode order by a.fassetgroupcode,a.fportcode ";
            rs = dbl.openResultSet(strSql); //取组合群
            while (rs.next()) {

                //判断操作组合表是否存在，如果不存在，提示用户要更新完所有组合群才可进行跨组合群操作 sunkey@Modify 20090818
                if(!dbl.yssTableExist("Tb_" + rs.getString("fassetgroupcode") + "_Para_Operportfolio")){
                    throw new YssException("对不起，请更新完所有组合群后再进行跨组合群处理!");
                }

                if (dbl.yssTableExist("Tb_" + rs.getString("fassetgroupcode") + "_Para_Portfolio")) {
                    bufSql.append(" SELECT * ");
                    bufSql.append(" FROM (SELECT a.FOperPortCode, a.FOperPortName, b.FPortCode, b.FPortName, ");
                    bufSql.append(" a.fporttype, 0 AS FType ");
                    bufSql.append(" FROM (SELECT * ");
                    bufSql.append(" FROM Tb_" + rs.getString("fassetgroupcode") + "_Para_Operportfolio ");
                    bufSql.append(" WHERE FCheckState = 1) a ");
                    bufSql.append(" LEFT JOIN (select y.FPortCode, y.FPortName ");
                 
                    bufSql.append(" from (select FPortCode ");
                    bufSql.append(" from Tb_" + rs.getString("fassetgroupcode") + "_Para_Portfolio ");
                    bufSql.append(" where  ");
                    
                    //end by lidaolong
                    bufSql.append("  FCheckState = 1 ");
                    bufSql.append(" and FEnabled = 1 ");
                    if(bETF){
                        bufSql.append(" AND FAssetType = '01' AND FSubAssetType = '0106' ");
                    }
                    bufSql.append(" and FASSETGROUPCODE = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" ) x ");
                    bufSql.append(" join (select a.* ");
                    bufSql.append(" from Tb_" + rs.getString("fassetgroupcode") + "_Para_Portfolio a ");
                    bufSql.append(" join (select DISTINCT FPortCode ");
                    bufSql.append(" from Tb_Sys_Userright ");
                    bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" and FUserCode = ").append(dbl.sqlString(pub.getUserCode()));
                    bufSql.append(" and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") d on a.FPortCode = ");
                    bufSql.append(" d.FPortCode ");
                    bufSql.append(" where a.fcheckstate = 1 ");
                    bufSql.append(" and a.FEnabled = 1) y on x.FPortCode = ");
                    bufSql.append(" y.FPortCode ");
                   
                    bufSql.append(" ) b ON a.fportcode = ");
                    bufSql.append(" b.FPortCode ");
                    bufSql.append(" WHERE (FPortType = 0 ) OR (FPortName IS NOT NULL)");
                    bufSql.append(" UNION ALL ");
                    bufSql.append(" select ' ' AS FOperPortCode, ' ' AS FOperPortName, y.FPortCode, ");
                    bufSql.append(" y.FPortName, 1 AS fporttype, 1 AS FType ");
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
              
                    bufSql.append(" from (select FPortCode ");
                    bufSql.append(" from Tb_" + rs.getString("fassetgroupcode") + "_Para_Portfolio ");
                    bufSql.append(" where ");
                    
                    
                    
                    //end by lidaolong
                    bufSql.append("  FCheckState = 1 ");
                    bufSql.append(" and FEnabled = 1 ");
                    if(bETF){
                        bufSql.append(" AND FAssetType = '01' AND FSubAssetType = '0106' ");
                    }
                    bufSql.append(" and FASSETGROUPCODE = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" ) x ");
                    bufSql.append(" join (select a.* ");
                    bufSql.append(" from Tb_" + rs.getString("fassetgroupcode") + "_Para_Portfolio a ");
                    bufSql.append(" join (select DISTINCT FPortCode ");
                    bufSql.append(" from Tb_Sys_Userright ");
                    bufSql.append(" where FAssetGroupCode = ").append(dbl.sqlString(rs.getString("FAssetGroupCode")));
                    bufSql.append(" and (FUserCode = ").append(dbl.sqlString(pub.getUserCode()));
                    //20121128 added by liubo.海富通测试问题：调度方案执行、收益支付等界面的组合选择框没有考虑权限继承的问题
                    //============================================
                    bufSql.append(right.getInheritedRights(rs.getString("FAssetGroupCode"), "")).append(")");
                    //================end============================
                    bufSql.append(" and FRightType = " + dbl.sqlString(YssCons.YSS_SYS_RIGHTTYPE_PORT) + ") d on a.FPortCode = ");
                    bufSql.append(" d.FPortCode ");
                    bufSql.append(" where a.fcheckstate = 1 ");
                    bufSql.append(" and a.FEnabled = 1) y on x.FPortCode = y.FPortCode ");
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                /*    bufSql.append(" and x.FStartDate = y.FStartDate ");*/
                    
                    //end by lidaolong
                    bufSql.append(" WHERE y.FPortCode NOT IN ");
                    bufSql.append(" (SELECT FPortCode ");
                    bufSql.append(" FROM Tb_" + rs.getString("fassetgroupcode") + "_Para_Operportfolio ");
                    bufSql.append(" WHERE FCheckState = 1)) atab ");
                    //========================== 增加组合的筛选 ===========================
                    //MS00003-QDV4.1赢时胜上海2009年2月1日03_A: 参数设置布局分散不便操作
                    //add by sunkey 20090411
                    //流程的相关信息是存放在pub里的，如果取得到流程的信息就要对组合进行筛选
                    if (pub.getFlow() != null) {
                        FlowBean flow = (FlowBean) pub.getFlow().get(pub.getUserCode());
                        if (flow != null) {
                            String tmpPorts = flow.getFPorts();
                            if (tmpPorts != null && !tmpPorts.trim().equals("")) {
                                //将组合作为筛选条件放入上述sql语句
                                bufSql.append(" Where atab.FPortCode in (" + operSql.sqlCodes(tmpPorts) + ")");
                            }
                        }
                    }
                    //=============================End MS00003===========================

                    bufSql.append(" ORDER BY FType, FOperPortCode, atab.FPortType, FType, FPortCOde ");

                    prs = dbl.openResultSet(bufSql.toString());
                    while (prs.next()) {
                        if (bFirstRow) {//如果该组合群下有操作组合，则在且仅在结果集的第一行将该组合群代码和名称添加到bufAll中返回至前台 panjunfang modify 20090825
                            NodeCode = rs.getString("FAssetGroupCode");
                            NodeName = rs.getString("FAssetGroupName");
                            NodeOrderCode = YssFun.formatNumber(GroupOrderCode++, "000");
                            parentCode = "[root]";
                            bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                            //没有组合代码就是空格
                            bufAll.append(" ").append(YssCons.YSS_LINESPLITMARK);
                            //取当前用户下可用组合
                            //2009.04.29 蒋锦 修改 MS00010 QDV4赢时胜（上海）2009年02月01日10_A
                            //修改了 SQL 语句，获取可用组合的部分 TB_Sys_UserRight 中可用组合的储存方式已在 MS00010 中被修改
                            bFirstRow = false;//此处设为false避免在循环下个操作组合的时候重复加载该组合群。panjunfang modify 20090825
                        }
                        //拼接操作组合和操作组合中的明晰组合
                        if (prs.getInt("FType") == 0) {
                            //拼接操作组合
                            if (prs.getDouble("fporttype") == 0) {
                                NodeCode = rs.getString("FAssetGroupCode") + "-" +
                                    prs.getString("FOperPortCode");
                                NodeName = prs.getString("FOperPortName");
                                NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1, "000") + YssFun.formatNumber(PortinfoOrderCode++,
                                    "000");
                                OperPortCode = NodeCode;
                                parentCode = rs.getString("FAssetGroupCode");
                                sPortCode = " "; //没有组合代码就是空格
                                detailPortOrderCode = 0;
                                tmpInt = 0;
                                //将操作组合临时存放，如果有明细组合则在存放明细组合前先存放操作组合
                                bufTmp = new StringBuffer();//清空上一个操作组合的数据
                                bufTmp.append(NodeCode).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufTmp.append(NodeName).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufTmp.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufTmp.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufTmp.append(sPortCode).append(YssCons.YSS_LINESPLITMARK); //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                            } else { //拼接操作组合中的明晰组合
                                NodeCode = rs.getString("FAssetGroupCode") + "-" +
                                    prs.getString("FOperPortCode") + "-" +
                                    prs.getString("FPortCode");
                                NodeName = prs.getString("FPortName");
                                NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1,
                                    "000") + YssFun.formatNumber(PortinfoOrderCode - 1,
                                    "000") + YssFun.formatNumber(detailPortOrderCode++, "000");
                                parentCode = OperPortCode;
                                sPortCode = prs.getString("FPortCode");
                                tmpInt ++ ;

                                if(tmpInt == 1){//存放明细组合前先存放操作组合，保证前台显示正确
                                    bufAll.append(bufTmp.toString());
                                }
                                bufAll.append(NodeCode).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufAll.append(NodeName).append(YssCons.
                                    YSS_ITEMSPLITMARK1);
                                bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                                bufAll.append(sPortCode).append(YssCons.YSS_LINESPLITMARK); //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                            }
                        } else { //拼接未包含在操作组合中的明晰组合
                            NodeCode = rs.getString("FAssetGroupCode") + "-" + prs.getString("FPortCode");
                            NodeName = prs.getString("FPortName");
                            NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1,
                                "000") + YssFun.formatNumber(PortinfoOrderCode++,
                                "000");
                            parentCode = rs.getString("FAssetGroupCode");
                            sPortCode = prs.getString("FPortCode");

                            bufAll.append(NodeCode).append(YssCons.
                                YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeName).append(YssCons.
                                YSS_ITEMSPLITMARK1);
                            bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                            bufAll.append(sPortCode).append(YssCons.YSS_LINESPLITMARK); //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                        }
                    } //end port while
                    bufSql.delete(0, bufSql.length());
                    dbl.closeResultSetFinal(prs); //close the cursor modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
                    bFirstRow = true;//当前组合群循环完毕后将bFirstRow设回为true再进行下个组合群的判断。panjunfang modify 20090825
                }
            } //end group while
            sResult = bufAll.toString();
            if (sResult.endsWith(YssCons.YSS_LINESPLITMARK)) {
                sResult = sResult.substring(0, sResult.length() - YssCons.YSS_LINESPLITMARK.length());
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取组合群下可用组合信息出错", ex);
        } finally {
            bufAll = null;
            dbl.closeResultSetFinal(prs, rs);
        }
    }

    /**
     * TreeView获取全部组合信息
     * 加载全部组合群的全部组合
     * MS00010 : QDV4赢时胜（上海）2009年02月01日10_A
     * 20090429  wangzuochun
     * @return String
     * @throws YssException
     */
    public String getTreeViewPort() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        ResultSet prs = null;
        StringBuffer bufAll = new StringBuffer();
        int GroupOrderCode = 0;
        int PortinfoOrderCode = 0;
        String parentCode = "";
        String NodeCode = "";
        String NodeName = "";
        String NodeOrderCode = "";
        try {
            strSql = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1 order by FAssetGroupCode ";
            rs = dbl.openResultSet(strSql); //取出全部组合群
            while (rs.next()) {

                NodeCode = rs.getString("FAssetGroupCode");
                NodeName = rs.getString("FAssetGroupName");
                NodeOrderCode = YssFun.formatNumber(GroupOrderCode++, "000");
                parentCode = "[root]";
                bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
                bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
                bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                bufAll.append(" ").append(YssCons.YSS_LINESPLITMARK);
                //取全部组合群下的全部组合
                //2009.04.29 wangzuochun

                if (dbl.yssTableExist("Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio")) {
                    strSql = "select y.FPortCode,y.FPortName from " +
                    // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                      /*  "(select FPortCode,max(FStartDate) as FStartDate from " +
                        "Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio" +
                        " " +
                        " where FStartDate <= " +
                        dbl.sqlDate(new java.util.Date()) +
                        */
                    
                    "(select FPortCode from " +
                    "Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio" +
                    " " +
                    " where " +            
                        //end  by lidaolong
                    
                        " FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " +
                        dbl.sqlString(rs.getString("FAssetGroupCode")) +
                        " ) x join" +
                        " (select a.*  from " +
                        "Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio" +
                        " a" +
                        " where a.fcheckstate = 1 and a.FEnabled = 1" +
                        // edit by lidaolong 20110318 清理系统界面无效启用日期，调整前后台代码
                       /* ") y on x.FPortCode = y.FPortCode and x.FStartDate = y.FStartDate " +*/
                        ") y on x.FPortCode = y.FPortCode  " +
                        //end by lidaolong
                        
                        " order by y.FPortCode, y.FCheckState, y.FCreateTime desc";
                    prs = dbl.openResultSet(strSql);
                    while (prs.next()) {
                        NodeCode = rs.getString("FAssetGroupCode") + "-" + prs.getString("FPortCode");
                        NodeName = prs.getString("FPortName");
                        NodeOrderCode = YssFun.formatNumber(GroupOrderCode - 1, "000") + YssFun.formatNumber(PortinfoOrderCode++,
                            "000");
                        parentCode = rs.getString("FAssetGroupCode");
                        bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
                        bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
                        bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                        bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                        //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码
                        bufAll.append(prs.getString("FPortCode")).append(YssCons.YSS_LINESPLITMARK);
                    } //end port while
                    dbl.closeResultSetFinal(prs); //close the cursor modify by sunkey 20090602 MS00472:QDV4上海2009年6月02日01_B
                }
            } //end group while
            sResult = bufAll.toString();
            if (sResult.endsWith(YssCons.YSS_LINESPLITMARK)) {
                sResult = sResult.substring(0, sResult.length() - YssCons.YSS_LINESPLITMARK.length());
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取全部组合群全部组合信息出错", ex);
        } finally {
            bufAll = null;
            dbl.closeResultSetFinal(prs, rs);
        }
    }

    /**
     * TreeViewGroup获取全部组合群信息
     * 加载全部组合群
     * 20090429  wangzuochun
     * MS00010 : QDV4赢时胜（上海）2009年02月01日10_A
     * @return String
     * @throws YssException
     */
    public String getTreeViewGroup() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        StringBuffer bufAll = new StringBuffer();
        int GroupOrderCode = 0;
        String parentCode = "";
        String NodeCode = "";
        String NodeName = "";
        String NodeOrderCode = "";
        try {
            strSql = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1 order by FAssetGroupCode ";
            rs = dbl.openResultSet(strSql); //取出全部组合群
            while (rs.next()) {

                NodeCode = rs.getString("FAssetGroupCode");
                NodeName = rs.getString("FAssetGroupName");
                NodeOrderCode = YssFun.formatNumber(GroupOrderCode++, "000");
                parentCode = "[root]";
                bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
                bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
                bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);
                //2009.05.25 蒋锦 添加 国内： MS00002 QDV4赢时胜（上海）2009年4月20日02_A 操作组合 组合代码的位置
                bufAll.append(" ").append(YssCons.YSS_LINESPLITMARK);
            } //end group while
            sResult = bufAll.toString();
            if (sResult.endsWith(YssCons.YSS_LINESPLITMARK)) {
                sResult = sResult.substring(0, sResult.length() - YssCons.YSS_LINESPLITMARK.length());
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取全部组合群信息出错", ex);
        } finally {
            bufAll = null;
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * TreeView 加载全部组合群的全部组合信息   add by guolongchao 20111104 STORY 1572 权限复制功能扩展
     */
    public String getTreeViewPortCodes() throws YssException {
        String strSql = "";
        String sResult = "";
        ResultSet rs = null;
        ResultSet prs = null;
        StringBuffer bufAll = new StringBuffer();
        int GroupOrderCode = 0;//组合群排序代码
        int PortOrderCode = 0;//投资组合排序代码
        String parentCode = "";
        String NodeCode = "";
        String NodeName = "";
        String NodeOrderCode = "";
        try 
        {
            strSql = "select FAssetGroupCode,FAssetGroupName,FTabPreFix  from tb_sys_assetgroup where FLocked=0 and FTabInd=1 order by FAssetGroupCode ";
            rs = dbl.openResultSet(strSql); //取出全部组合群
            while (rs.next()) 
            {
            	GroupOrderCode++;
            	if (dbl.yssTableExist("Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio"))
            	{
            		 strSql = "select a.FPortCode,a.FPortName from " +
                              " Tb_" + rs.getString("FTabPreFix") + "_Para_Portfolio a" +
                              " where  FCheckState = 1 and FEnabled = 1 and FASSETGROUPCODE = " +
                                 dbl.sqlString(rs.getString("FAssetGroupCode")) +
                               " order by a.FPortCode, a.FCheckState, a.FCreateTime desc";
            		 prs = dbl.openResultSet(strSql);
            		 while(prs.next())
            		 {
            			  NodeCode = rs.getString("FAssetGroupCode") + "-"+ prs.getString("FPortCode");
                          NodeName = prs.getString("FPortName");
                          NodeOrderCode =  YssFun.formatNumber(GroupOrderCode-1, "000") +YssFun.formatNumber(PortOrderCode++, "000");
                          parentCode = "[root]";
                          bufAll.append(NodeCode).append(YssCons.YSS_ITEMSPLITMARK1);
                          bufAll.append(NodeName).append(YssCons.YSS_ITEMSPLITMARK1);
                          bufAll.append(parentCode).append(YssCons.YSS_ITEMSPLITMARK1);
                          bufAll.append(NodeOrderCode).append(YssCons.YSS_ITEMSPLITMARK1);               
                          bufAll.append(prs.getString("FPortCode")).append(YssCons.YSS_LINESPLITMARK);
            		 }
            		 dbl.closeResultSetFinal(prs);
            	}              
            } 
            sResult = bufAll.toString();
            if (sResult.endsWith(YssCons.YSS_LINESPLITMARK)) 
            {
                sResult = sResult.substring(0, sResult.length() - YssCons.YSS_LINESPLITMARK.length());
            }
            return sResult;
        } catch (Exception ex) {
            throw new YssException("获取全部组合群信息出错", ex);
        } finally {
            bufAll = null;
            dbl.closeResultSetFinal(rs);
        }
    }
    
    /**
     * 查询组合群记录
     * @param groupCode String ：组合群代码，如果不为空，就查询具体的组合群的记录
     * @throws YssException
     * @return String
     */
    public String getData_group(String sGroupCode) throws YssException {
        StringBuffer buffer = new StringBuffer();
        ResultSet rs = null;
        String strSql = "", strReturn = "";
        try {
            if (pub.getPrefixTB().length() == 0) { //表前缀为空：登陆系统前创建公司组时
                strSql = "select a.*, '' as fcuryName  from TB_SYS_AssetGroup a";
            } else {
                strSql =
                    "select a.*,b.fcuryName,c.FExRateSrcName,d.FRateName as FRateSrcName " +
                    " ,d.FExRateSrcCode as FExRateSrcCode from TB_SYS_AssetGroup a left join (select fcuryName,fcuryCode from " +
                    pub.yssGetTableName("Tb_Para_Currency") +
                    ") b on a.FBaseCury = b.fcuryCode";
                strSql = strSql +
                    " left join (select FExRateSrcCode,FExRateSrcName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    ") c on a.FBaseRateSrcCode = c.FExRateSrcCode";
                strSql = strSql +
                    " left join (select FExRateSrcCode,FExRateSrcName as FRateName from " +
                    pub.yssGetTableName("Tb_Para_ExRateSource") +
                    ") d on a.FPortRateSrcCode = d.FExRateSrcCode";
            }
            if (sGroupCode != null) {
                strSql = strSql + " where a.FAssetGroupCode='" + sGroupCode + "'"; // wdy modify 20070830
            }

            strSql = strSql + " order by a.FAssetGroupCode"; // wdy modify 20070830
            rs = dbl.openResultSet(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE);
            while (rs.next()) {
                buffer.append(rs.getString("FAssetGroupCode")).append("\t");
                buffer.append(rs.getString("FAssetGroupName")).append("\t");
                if (sGroupCode != null) {
                    buffer.append(rs.getString("FMaxNum")).append("\t"); //
                    /**shashijie 2012-07-25 BUG 5023 */
                    buffer.append(YssFun.formatDate(rs.getDate("FStartDate"))).append("\t");
                    /**end*/
                    buffer.append(rs.getString("FBaseCury")).append("\t");
                    buffer.append(rs.getString("FLocked")).append("\t");
                    //edit by songjie 2012.01.30 BUG 3714 QDV4赢时胜(上海)2012年01月19日04_B 若为 null 则显示为 ""
                    buffer.append((null == rs.getString("FDesc")) ? "" : rs.getString("FDesc")).append("\t");
                    buffer.append(rs.getString("FTabInd")).append("\t");
                    buffer.append(rs.getString("FTabPrefix")).append("\t");
                    buffer.append(rs.getString("FCuryName")).append("\t");
                    buffer.append(rs.getInt("FSysCheck")).append("\t");
                    buffer.append(rs.getString("FBaseRateSrcCode")).append("\t");
                    //edit by songjie 2012.01.30 BUG 3714 QDV4赢时胜(上海)2012年01月19日04_B 若为 null 则显示为 ""
                    buffer.append((null == rs.getString("FExRateSrcName")) ? "" : rs.getString("FExRateSrcName")).append("\t");
                    buffer.append(rs.getString("FBaseRateCode")).append("\t");

                    buffer.append(rs.getString("FPortRateSrcCode")).append("\t");
                    //edit by songjie 2012.01.30 BUG 3714 QDV4赢时胜(上海)2012年01月19日04_B 若为 null 则显示为 ""
                    buffer.append((null == rs.getString("FRateSrcName")) ? "" : rs.getString("FRateSrcName")).append("\t");
                    buffer.append(rs.getString("FPortRateCode")).append("\t");

                    /**
                     *  date   : 2008-11-11
                     *  author : sunkey
                     *  BugID  : MS00010
                     *  desc   :
                     *  获取审核参数【用户可以审核自己录入的数据】状态(FCheckSelf),
                     *  以便前台根据此参数来确定对应的checkbox状态,该参数只有两个状态"yes" or "no"
                     */
                    buffer.append(rs.getString("FCheckSelf")).append("\t");
                    if (dbl.isFieldExist(rs, "FAuditOwn")) 
                        buffer.append(rs.getString("FAuditOwn")).append("\t");//add by guolongchao 20120426 添加自审字段FAuditOwn  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
                }
                buffer.append(YssCons.YSS_LINESPLITMARK); //行间用crlf间隔
            }
            if (buffer.toString().length() > 2) {
                strReturn = buffer.toString().substring(0,
                    buffer.toString().length() - 2);
            }
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            String sVocStr = vocabulary.getVoc(YssCons.YSS_MTV_EXCHANGERATE);

            strReturn = strReturn + "\r\n" + "voc" + sVocStr;
            return strReturn;
        } catch (SQLException se) {
            throw new YssException("获取组合群信息出错！", se); //注意这里抛出异常的方式
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }
    
    //add by huangqirong 2011-09-22 story #1286
    public int getIsExsistMsg(String [][]frmValue) throws Exception{
    	int rows=0;
    	try {
			rows = dbl.executeSqlwithReturnRows("select * from TB_SYS_AssetGroup where FAssetGroupCode =" + dbl.sqlString(frmValue[0][0]));
		} catch (SQLException e) {
			throw new YssException("查询组合群数据出错！", e);
		}
		return rows;
    }
    

    /**
     * 删除或保存组合群记录
     * @param frmValue String[][]：传入二维数组，里面为需要保存的记录数据
     * @param blDelete boolean：如果为true，则表示为删除操作，否则为保存操作
     * @throws YssException
     */
    public void saveData_group(String[][] frmValue, boolean blDelete, String status) throws
        YssException {
        String errorInfo = "保存组合群信息设定时出错!"; //定义错误提示信息
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务

        //add by xuqiji 20090415：QDV4赢时胜（上海）2009年4月7日01_A MS00352  新建组合群时能够自动创建对应的一套表  ----//
        StringBuffer bufSql = new StringBuffer(); //用户拼接sql语句
        int rowCount = 0; //用于记录每次删除的组合群数
        byte statusId = 0;//add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B
        //======================End MS00352 Declared=============================
        try {
        	//----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
        	if(status != null){
        		statusId = status.equals("0")? YssCons.OP_ADD : YssCons.OP_EDIT;
        	}
        	//----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < frmValue.length; i++) {
            	//----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
            	if(status != null && statusId == YssCons.OP_ADD){
            		strSql = " select * from TB_SYS_AssetGroup where FAssetGroupCode =" + dbl.sqlString(frmValue[i][0]);
            		rowCount = dbl.executeSqlwithReturnRows(strSql);
            		if(rowCount != 0){
            			throw new YssException("【" + frmValue[i][0] + "】信息已经存在，请重新输入");
            		}
            	}
            	
            	//add by guolongchao 20111128 STORY 1900  增加对“删除”组合群功能的约束
            	if (blDelete==true) 
            	{
            		strSql = " select * from user_tab_comments a where a.table_name like 'TB_" + frmValue[i][0]+"%'";
            		rowCount = dbl.executeSqlwithReturnRows(strSql);
            		if(rowCount != 0){
            			throw new YssException("所选择组合群【" + frmValue[i][0] + "】已有数据，不能删除！");
            		}
            	}           
                //----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
                strSql = "delete from TB_SYS_AssetGroup where FAssetGroupCode='" +frmValue[i][0] + "'";
                //用户获取删除的行数，如果等于0，则表示是新增 add by xuqiji MS00352
                rowCount = dbl.executeSqlwithReturnRows(strSql);
                
                //add by huangqirong 2011-09-22 story #1286
                //edit by songjie 2012.01.10 BUG 3570 QDV4赢时胜(测试)2012年01月04日02_B blDelete==false 改为 blDelete
                if((status != null && status.equals("3")) || blDelete){//update by guolongchao 20111128 STORY 1900  增加对“删除”组合群状态的判断
                	dbl.executeSqlwithReturnRows("delete from Tb_Fun_Version where FASSETGROUPCODE='" +
                        frmValue[i][0] + "'");
            	}
                //---end---
                
                if (!blDelete) {
                    /**
                     *  date   : 2008-11-11
                     *  author : sunkey
                     *  BugID  : MS00010
                     *  desc   : 添加保存审核参数【用户可以审核自己录入的数据】状态 frmValue[i][14]
                     */
                    strSql = "insert into TB_SYS_AssetGroup(FAssetGroupCode,FAssetGroupName,FMaxNum,FBaseCury,FLocked,FDesc,FTabInd,FTabPrefix,FStartDate" +
                        ", FSysCheck,FBaseRateSrcCode,FBaseRateCode," +
                        " FPortRateSrcCode,FPortRateCode" +
                      //modidy huangqirong 2011-09-09 story #1286
                        ",FCheckSelf,FAuditOwn";//add by guolongchao 20120426 添加自审字段FAuditOwn  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
                    	if(status.equals("3")&&rowCount==0)
                    		strSql+=",FCOPYSOURCE,FCOPYSTATE";
                    	strSql+=") values('" +
                        //---end---
                        frmValue[i][0] + "','" +
                        frmValue[i][1] + "'," + frmValue[i][2] +//modify by baopingping BUG2222 20110816 在添加组合群设置时 最大组合数的位数超过10位会报错
                        ",'" + frmValue[i][4] + "'," + frmValue[i][5] + ",'" +
                        frmValue[i][6].trim() + "'," +//modify by baopingping BUG2222 20110816  在添加组合群设置时  备注字段末尾出现空格 导致插入数据出错
                        frmValue[i][7] + ",'" + frmValue[i][8] + "'," +
                        dbl.sqlDate(frmValue[i][3]) + "," + frmValue[i][9] + "," +
                        (frmValue[i][10].equalsIgnoreCase("") ? "' '" :
                         dbl.sqlString(frmValue[i][10])) + "," +
                        dbl.sqlString(frmValue[i][11])
                        //edit by songjie 2012.01.30 BUG 3714 QDV4赢时胜(上海)2012年01月19日04_B 若组合汇率来源为空字符串，则插入空格
                        + "," + (frmValue[i][12].equalsIgnoreCase("") ? "' '" : dbl.sqlString(frmValue[i][12]))  + "," +
                        dbl.sqlString(frmValue[i][13])
                        //modify by huangqirong 2011-09-08 story #1286
                        + ",'" + frmValue[i][14]+"'"
                    	+ "," + dbl.sqlString(frmValue[i][15]);//add by guolongchao 20120426 添加自审字段FAuditOwn  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
                        if((status.equals("3")&&rowCount==0) || (status.equals("4")&&rowCount==0)){
                        	strSql+=","+dbl.sqlString(frmValue[i][16])+","+dbl.sqlString(frmValue[i][17]);
                        }
                        strSql+=")";
                        //---end---
                    dbl.executeSql(strSql);
                    
                    //add by guolongchao 20120426  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----start
                    //若启用了自审功能,更新tb_fun_menubar表,对包含审核功能的菜单自动添加自审操作类型
                    if(frmValue[i][15].equals("yes")&& frmValue[i][14].equals("yes"))
                    {
                    	dbl.executeSql(" delete from  Tb_Sys_OperationType  where FType = 'system' and fopertypecode='auditOwn' ");  
                    	dbl.executeSql(" insert into Tb_Sys_OperationType(fopertypecode,fopertypename,ftype) values('auditOwn','自审','system') ");  
                        dbl.executeSql(" update tb_fun_menubar set fopertypecode=fopertypecode||',auditOwn' where instr(fopertypecode,'audit')>0 and instr(fopertypecode,'auditOwn')=0 ");
                        dbl.executeSql(" update tb_sys_roleright set fopertypes=fopertypes||',auditOwn' where instr(fopertypes,'audit')>0 and instr(fopertypes,'auditOwn')=0 ");                        
                        dbl.executeSql(" update tb_sys_userright set fopertypes=fopertypes||',auditOwn' where instr(fopertypes,'audit')>0 and instr(fopertypes, 'auditOwn') = 0 " +
    	                		" and (fassetgroupcode='"+frmValue[i][0]+
    	                		"' or (fusercode in (select distinct fusercode from  tb_sys_userright where fassetgroupcode='"+frmValue[i][0]+"') and frighttype in ('public','system')))");
                    }
                    //add by guolongchao 20120426  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----end
                    
                    //------add by xuqiji 20090415：QDV4赢时胜（上海）2009年4月7日01_A MS00352  新建组合群时能够自动创建对应的一套表  描述：在创建组合群时自动创建版本--------------//
                    //如果是新增，要插入版本号，新增的判断标识是删除了0行，也就是数据库中之前不存在相关信息的数据
                    if (rowCount == 0) {
                        bufSql.append(" INSERT INTO TB_FUN_VERSION ");
                        bufSql.append(" (FAssetGroupCode, ");
                        bufSql.append(" FVerNum, ");
                        bufSql.append(" FIssueDate, ");
                        bufSql.append(" FFinish, ");
                        bufSql.append(" FUSERCODE,");
                        bufSql.append(" FCreateDate, ");
                        bufSql.append(" FCreateTime, ");
                        bufSql.append(" FUPDATETABLES, ");
                        bufSql.append(" FERRORINFO, ");
                        bufSql.append(" FSQLSTR) ");
                        bufSql.append(" VALUES ( ");
                        bufSql.append(dbl.sqlString(frmValue[i][0])).append(", ");
                        bufSql.append(dbl.sqlString(YssCons.YSS_VERSION_STANDARD)).append(", "); //插入基准版本号而不是最大版本号 sunkey@Modify
                        bufSql.append(dbl.sqlDate(new java.util.Date())).append(", ");
                        bufSql.append(dbl.sqlString("Success")).append(", ");
                        bufSql.append(dbl.sqlString("admin")).append(", ");
                        bufSql.append(dbl.sqlDate(new java.util.Date())).append(", ");
                        bufSql.append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))).append(", ");
                        bufSql.append(dbl.sqlString(" ")).append(", ");
                        bufSql.append(dbl.sqlString(" ")).append(", ");
                        if (dbl.dbType == YssCons.DB_ORA) {
                            bufSql.append("EMPTY_CLOB()").append(")");
                        } else {
                            bufSql.append(dbl.sqlString("").replaceAll("'", "''")).append(")");
                        }
                        dbl.executeSql(bufSql.toString());
                    } //===End if
                    rowCount = 0; //每个循环结束，将删除的行数还原成0
                    //================================End MS00352====================
                } //==End for
            }

        	//20130122 added by liubo.Story #3213
        	//每次更新组合群信息后，都需要更新一次组合群列表视图
            //=================================
            createViewOfAssetGroupList();
            //==============end===================
   
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            throw new YssException(errorInfo, se);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    
    /**
     * 删除或保存组合群记录
     * @param frmValue String[][]：传入二维数组，里面为需要保存的记录数据
     * @param blDelete boolean：如果为true，则表示为删除操作，否则为保存操作
     * @throws YssException
     * @throws IOException 
     */
    public void saveData_creategroup(String[][] frmValue, boolean blDelete, String status) throws
        YssException{
        String errorInfo = "保存组合群信息设定时出错!"; //定义错误提示信息
        String strSql = "";
        Connection conn = dbl.loadConnection();
        boolean bTrans = false; //代表是否开始了事务
        
        /**add by liuxiaojun story 4150*/
        ResultSet rs = null;
        /**end story 4150*/
        
        //add by xuqiji 20090415：QDV4赢时胜（上海）2009年4月7日01_A MS00352  新建组合群时能够自动创建对应的一套表  ----//
        StringBuffer bufSql = new StringBuffer(); //用户拼接sql语句
        int rowCount = 0; //用于记录每次删除的组合群数
        byte statusId = 0;//add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B
        //======================End MS00352 Declared=============================
        try {
        	//----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
        	if(status != null){
        		statusId = status.equals("0")? YssCons.OP_ADD : YssCons.OP_EDIT;
        	}
        	//----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
            conn.setAutoCommit(false);
            bTrans = true;
            for (int i = 0; i < frmValue.length; i++) {
            	//----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
            	if(status != null && statusId == YssCons.OP_ADD){
            		strSql = " select * from TB_SYS_AssetGroup where FAssetGroupCode =" + dbl.sqlString(frmValue[i][0]);
            		rowCount = dbl.executeSqlwithReturnRows(strSql);
            		if(rowCount != 0){
            			throw new YssException("【" + frmValue[i][0] + "】信息已经存在，请重新输入");
            		}
            	}
            	
            	//add by guolongchao 20111128 STORY 1900  增加对“删除”组合群功能的约束
            	if (blDelete==true) 
            	{
            		strSql = " select * from user_tab_comments a where a.table_name like 'TB_" + frmValue[i][0]+"%'";
            		rowCount = dbl.executeSqlwithReturnRows(strSql);
            		if(rowCount != 0){
            			throw new YssException("所选择组合群【" + frmValue[i][0] + "】已有数据，不能删除！");
            		}
            	}           
                //----add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B----//
                strSql = "delete from TB_SYS_AssetGroup where FAssetGroupCode='" +frmValue[i][0] + "'";
                //用户获取删除的行数，如果等于0，则表示是新增 add by xuqiji MS00352
                rowCount = dbl.executeSqlwithReturnRows(strSql);
                
                //add by huangqirong 2011-09-22 story #1286
                //edit by songjie 2012.01.10 BUG 3570 QDV4赢时胜(测试)2012年01月04日02_B blDelete==false 改为 blDelete
                if((status != null && status.equals("3")) || blDelete){//update by guolongchao 20111128 STORY 1900  增加对“删除”组合群状态的判断
                	dbl.executeSqlwithReturnRows("delete from Tb_Fun_Version where FASSETGROUPCODE='" +
                        frmValue[i][0] + "'");
            	}
                //---end---
                if (!blDelete) {              	
                    /**
                     *  date   : 2008-11-11
                     *  author : sunkey
                     *  BugID  : MS00010
                     *  desc   : 添加保存审核参数【用户可以审核自己录入的数据】状态 frmValue[i][14]
                     */
                    strSql = "insert into TB_SYS_AssetGroup(FAssetGroupCode,FAssetGroupName,FMaxNum,FBaseCury,FLocked,FDesc,FTabInd,FTabPrefix,FStartDate" +
                        ", FSysCheck,FBaseRateSrcCode,FBaseRateCode," +
                        " FPortRateSrcCode,FPortRateCode" +
                      //modidy huangqirong 2011-09-09 story #1286
                        ",FCheckSelf,FAuditOwn";//add by guolongchao 20120426 添加自审字段FAuditOwn  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
                    	if(status.equals("3")&&rowCount==0)
                    		strSql+=",FCOPYSOURCE,FCOPYSTATE";
                    	strSql+=") values('" +
                        //---end---
                        frmValue[i][0] + "','" +
                        frmValue[i][1] + "'," + frmValue[i][2] +//modify by baopingping BUG2222 20110816 在添加组合群设置时 最大组合数的位数超过10位会报错
                        ",'" + frmValue[i][4] + "'," + frmValue[i][5] + ",'" +
                        frmValue[i][6].trim() + "'," +//modify by baopingping BUG2222 20110816  在添加组合群设置时  备注字段末尾出现空格 导致插入数据出错
                        frmValue[i][7] + ",'" + frmValue[i][8] + "'," +
                        dbl.sqlDate(frmValue[i][3]) + "," + frmValue[i][9] + "," +
                        (frmValue[i][10].equalsIgnoreCase("") ? "' '" :
                         dbl.sqlString(frmValue[i][10])) + "," +
                        dbl.sqlString(frmValue[i][11])
                        //edit by songjie 2012.01.30 BUG 3714 QDV4赢时胜(上海)2012年01月19日04_B 若组合汇率来源为空字符串，则插入空格
                        + "," + (frmValue[i][12].equalsIgnoreCase("") ? "' '" : dbl.sqlString(frmValue[i][12]))  + "," +
                        dbl.sqlString(frmValue[i][13])
                        //modify by huangqirong 2011-09-08 story #1286
                        + ",'" + frmValue[i][14]+"'"
                    	+ "," + dbl.sqlString(frmValue[i][15]);//add by guolongchao 20120426 添加自审字段FAuditOwn  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc
                        if(status.equals("3")&&rowCount==0){
                        	strSql+=","+dbl.sqlString(frmValue[i][16])+","+dbl.sqlString(frmValue[i][17]);
                        }
                        strSql+=")";
                        //---end---
                    dbl.executeSql(strSql);
                    
                    
                    //add by guolongchao 20120426  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----start
                    //若启用了自审功能,更新tb_fun_menubar表,对包含审核功能的菜单自动添加自审操作类型
                    if(frmValue[i][15].equals("yes")&& frmValue[i][14].equals("yes"))
                    {
                    	dbl.executeSql(" delete from  Tb_Sys_OperationType  where FType = 'system' and fopertypecode='auditOwn' ");  
                    	dbl.executeSql(" insert into Tb_Sys_OperationType(fopertypecode,fopertypename,ftype) values('auditOwn','自审','system') ");  
                        dbl.executeSql(" update tb_fun_menubar set fopertypecode=fopertypecode||',auditOwn' where instr(fopertypecode,'audit')>0 and instr(fopertypecode,'auditOwn')=0 ");
                        dbl.executeSql(" update tb_sys_roleright set fopertypes=fopertypes||',auditOwn' where instr(fopertypes,'audit')>0 and instr(fopertypes,'auditOwn')=0 ");                        
                        dbl.executeSql(" update tb_sys_userright set fopertypes=fopertypes||',auditOwn' where instr(fopertypes,'audit')>0 and instr(fopertypes, 'auditOwn') = 0 " +
    	                		" and (fassetgroupcode='"+frmValue[i][0]+
    	                		"' or (fusercode in (select distinct fusercode from  tb_sys_userright where fassetgroupcode='"+frmValue[i][0]+"') and frighttype in ('public','system')))");
                    }
                    //add by guolongchao 20120426  QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----end
                    
                    //------add by xuqiji 20090415：QDV4赢时胜（上海）2009年4月7日01_A MS00352  新建组合群时能够自动创建对应的一套表  描述：在创建组合群时自动创建版本--------------//
                    //如果是新增，要插入版本号，新增的判断标识是删除了0行，也就是数据库中之前不存在相关信息的数据
                    if (rowCount == 0) {
                        bufSql.append(" INSERT INTO TB_FUN_VERSION ");
                        bufSql.append(" (FAssetGroupCode, ");
                        bufSql.append(" FVerNum, ");
                        bufSql.append(" FIssueDate, ");
                        bufSql.append(" FFinish, ");
                        bufSql.append(" FUSERCODE,");
                        bufSql.append(" FCreateDate, ");
                        bufSql.append(" FCreateTime, ");
                        bufSql.append(" FUPDATETABLES, ");
                        bufSql.append(" FERRORINFO, ");
                        bufSql.append(" FSQLSTR) ");
                        bufSql.append(" VALUES ( ");
                        bufSql.append(dbl.sqlString(frmValue[i][0])).append(", ");
                        bufSql.append(dbl.sqlString(YssCons.YSS_VERSION_STANDARD)).append(", "); //插入基准版本号而不是最大版本号 sunkey@Modify
                        bufSql.append(dbl.sqlDate(new java.util.Date())).append(", ");
                        bufSql.append(dbl.sqlString("Success")).append(", ");
                        bufSql.append(dbl.sqlString("admin")).append(", ");
                        bufSql.append(dbl.sqlDate(new java.util.Date())).append(", ");
                        bufSql.append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date()))).append(", ");
                        bufSql.append(dbl.sqlString(" ")).append(", ");
                        bufSql.append(dbl.sqlString(" ")).append(", ");
                        if (dbl.dbType == YssCons.DB_ORA) {
                            bufSql.append("EMPTY_CLOB()").append(")");
                        } else {
                            bufSql.append(dbl.sqlString("").replaceAll("'", "''")).append(")");
                        }
                        dbl.executeSql(bufSql.toString());
                    } //===End if
                    rowCount = 0; //每个循环结束，将删除的行数还原成0
                    //================================End MS00352====================
                    
                    
                } //==End for
            }

        	//20130122 added by liubo.Story #3213
        	//每次更新组合群信息后，都需要更新一次组合群列表视图
            //=================================
            createViewOfAssetGroupList();
            //==============end===================

            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            throw new YssException(errorInfo, se);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
    }
    

    /**
     * 保存导入的菜单记录
     * @param frmValue String[][]：传入二维数组，里面为需要保存菜单记录
     * @throws YssException
     */
    public void updateMenu(String[][] frmValue, String type) throws YssException {
        String errorInfo = "保存菜单信息时出错!"; //定义错误提示信息
        String strSql = "";
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务

        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + type;
            dbl.executeSql(strSql);
            for (int i = 0; i < frmValue.length; i++) {
                strSql = "select * from " + type;
                rs = dbl.openResultSet(strSql);
                rs.moveToInsertRow();
                if (type.equalsIgnoreCase("Tb_Fun_Menu")) {
                    rs.updateString("FMenuCode", frmValue[i][0]);
                    rs.updateString("FMenuName", frmValue[i][1]);
                    rs.updateString("FParentCode", frmValue[i][2]);
                    rs.updateString("FMenuOrder", frmValue[i][3]);
                    rs.updateString("FMenuLevel", frmValue[i][4]);
                    rs.updateString("FCheck", frmValue[i][5]);
                    rs.updateString("FIcon", frmValue[i][6]);
                    rs.updateString("FShortCut",
                                    (frmValue[i][7].equalsIgnoreCase("") ? " " :
                                     frmValue[i][7]));
                    rs.updateString("FEnabled", frmValue[i][8]);
                    rs.updateInt("FRefInvokeCode", Integer.parseInt(frmValue[i][9]));
                } else {
                    rs.updateString("FBarCode", frmValue[i][0]);
                    rs.updateString("FBarName", frmValue[i][1]);
                    rs.updateString("FCroupCode", frmValue[i][2]);
                    rs.updateString("FIcon", frmValue[i][3]);
                    rs.updateString("FEnabled", frmValue[i][4]);
                    rs.updateString("FBarOrder", frmValue[i][5]);
                    rs.updateInt("FRefInvokeCode", Integer.parseInt(frmValue[i][6]));
                }
                //rs.insertRow();
                rs.insertRow();
                rs.getStatement().close();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            throw new YssException(errorInfo, se);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }

    /**
     * 保存导入的权限记录
     * @param frmValue String[][]：传入二维数组里面为需要保存记录
     * @throws YssException
     */
    public void updateRithgType(String[][] frmValue, String type) throws
        YssException {
        String errorInfo = "保存权限设置时出错!"; //定义错误提示信息
        String strSql = "";
        Connection conn = dbl.loadConnection();
        ResultSet rs = null;
        boolean bTrans = false; //代表是否开始了事务
        try {
            conn.setAutoCommit(false);
            bTrans = true;
            strSql = "delete from " + type;
            dbl.executeSql(strSql);
            for (int i = 0; i < frmValue.length; i++) {
                strSql = "select * from " + type;
                rs = dbl.openResultSet(strSql);
                rs.moveToInsertRow();
                rs.updateString("Fcode", frmValue[i][0]);
                rs.updateString("Fname", frmValue[i][1]);
                rs.updateString("FdescCode", frmValue[i][2]);
                rs.updateString("Ftype", frmValue[i][3]);
                rs.insertRow();
                rs.getStatement().close();
            }
            conn.commit();
            bTrans = false;
            conn.setAutoCommit(true);
        } catch (SQLException se) {
            throw new YssException(errorInfo, se);
        } finally {
            dbl.closeResultSetFinal(rs);
            dbl.endTransFinal(conn, bTrans);
        }
    }
    
    //add by huangqirong 2011-09-08 story #1286
    public void updateSourceState(String assetGroupCode) throws Exception{
        try {
            dbl.executeSql("update TB_SYS_ASSETGROUP set FCOPYSTATE='1' where FASSETGROUPCODE="+dbl.sqlString(assetGroupCode));            
        } catch (SQLException se) {
            throw new YssException("更改组合群复制源状态出错！", se);
        } finally {
        }
    }
    
  //add by huangqirong 2011-09-08 story #1286
    public String getCopySource(String assetGroupCode) throws Exception{
    	String result="";
        ResultSet rs = null;       
        try {
            rs=dbl.openResultSet("select FCOPYSOURCE,FCOPYSTATE from TB_SYS_ASSETGROUP where FASSETGROUPCODE="+dbl.sqlString(assetGroupCode));
            if(rs.next()){
            	result=rs.getString("FCOPYSOURCE")!=null?rs.getString("FCOPYSOURCE").trim()+"\t":"";
            	result+=rs.getString("FCOPYSTATE")!=null?rs.getString("FCOPYSTATE").trim():"";
            }
        } catch (SQLException se) {
            throw new YssException("读取组合群复制源状态出错！", se);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return result;
    }
    
    
    public void setGrouNodeCode(String grouNodeCode) {
        this.grouNodeCode = grouNodeCode;
    }

    public void setGrouNodeName(String grouNodeName) {
        this.grouNodeName = grouNodeName;
    }

    public void setGrouParentCode(String grouParentCode) {
        this.grouParentCode = grouParentCode;
    }

    public void setOldorderCode(int oldorderCode) {
        this.oldorderCode = oldorderCode;
    }

    public void setGrouSectorCode(String grouSectorCode) {
        this.grouSectorCode = grouSectorCode;
    }

    public void setGrouDesc(String grouDesc) {
        this.grouDesc = grouDesc;
    }

    public void setGrouCheckTime(String grouCheckTime) {
        this.grouCheckTime = grouCheckTime;
    }

    public void setGrouCheckUserName(String grouCheckUserName) {
        this.grouCheckUserName = grouCheckUserName;
    }

    public void setGrouCheckUserCode(String grouCheckUserCode) {
        this.grouCheckUserCode = grouCheckUserCode;
    }

    public void setGrouCreatorTime(String grouCreatorTime) {
        this.grouCreatorTime = grouCreatorTime;
    }

    public void setGrouCreatorName(String grouCreatorName) {
        this.grouCreatorName = grouCreatorName;
    }

    public void setGrouCreatorCode(String grouCreatorCode) {
        this.grouCreatorCode = grouCreatorCode;
    }

    public void setGrouCheckStateName(String grouCheckStateName) {
        this.grouCheckStateName = grouCheckStateName;
    }

    public void setGrouCheckStateId(int grouCheckStateId) {
        this.grouCheckStateId = grouCheckStateId;
    }

    public void setBETF(boolean bETF) {
        this.bETF = bETF;
    }

    public String getGrouNodeCode() {
        return grouNodeCode;
    }

    public String getGrouNodeName() {
        return grouNodeName;
    }

    public String getGrouParentCode() {
        return grouParentCode;
    }

    public int getOldorderCode() {
        return oldorderCode;
    }

    public String getGrouSectorCode() {
        return grouSectorCode;
    }

    public String getGrouDesc() {
        return grouDesc;
    }

    public String getGrouCheckTime() {
        return grouCheckTime;
    }

    public String getGrouCheckUserName() {
        return grouCheckUserName;
    }

    public String getGrouCheckUserCode() {
        return grouCheckUserCode;
    }

    public String getGrouCreatorTime() {
        return grouCreatorTime;
    }

    public String getGrouCreatorName() {
        return grouCreatorName;
    }

    public String getGrouCreatorCode() {
        return grouCreatorCode;
    }

    public String getGrouCheckStateName() {
        return grouCheckStateName;
    }

    public int getGrouCheckStateId() {
        return grouCheckStateId;
    }

    public boolean isBETF() {
        return bETF;
    }
    //------ add by wangzuochun 2010.10.23 BUG #177::删除组合群信息的时候报错----QDV4赢时胜(测试)2010年10月21日03_B.xls
	public String buildRowStr() throws YssException {
		
        return "";
	}

	public String getOperValue(String type) throws YssException {
		// TODO Auto-generated method stub
		return "";
	}

	public void parseRowStr(String rowStr) throws YssException {
		//add by guyichuan 20110608 STORY #897
		if("".equals(rowStr)|| "[false]".equalsIgnoreCase(rowStr)){
        	return;
        }else if("[true]".equalsIgnoreCase(rowStr)){
        	this.isMultiGroup=true;
        	return;
        }//--end-STORY #897---
		
	}
	
	/**
	 * add by songjie 2011.03.19
	 * BUG:1466 
	 * QDV4赢时胜(测试)2011年03月15日01_B
	 * 解析数据
	 * @param rowStr
	 * @throws YssException
	 */
	public void parseRowStr(String[][] rowStr) throws YssException {
		try{
			
		}catch(Exception e){
			throw new YssException("解析组合群设置请求信息出错", e);
		}
		
	}
	
	//--------------------------BUG #177--------------------------//
	
	// add by fangjiang 2011.08.18 STORY #1288
	public String isNeedCheck() throws YssException {
		String result = "";
		String sql = " select FSysCheck from TB_SYS_AssetGroup where fassetgroupcode = "
			         + dbl.sqlString(pub.getAssetGroupCode());
		ResultSet rs = null;
		try{
			rs = dbl.openResultSet(sql);
			if(rs.next()){
				if(rs.getDouble("FSysCheck") == 1)
					result = "yes";
				else
					result = "no";
			}
		}catch(Exception e){
			throw new YssException("获取组合群信息出错", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return result;
	}
	//----------------------
	
	//add by huangqirong 2011-09-20 story #1284
	public String getListViewGroups() throws YssException {  
		String result="";
        String strSql = "";
        StringBuffer bufShow = new StringBuffer();
        ResultSet rs = null;
        try {
            
            strSql = "select FAssetGroupCode,FAssetGroupName from tb_sys_assetgroup where FLocked=0 and FSyscheck = 1 order by FAssetGroupCode";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                bufShow.append(rs.getString("FAssetGroupCode")).append("\t");
                bufShow.append(rs.getString("FAssetGroupName"));
                bufShow.append("\f\f");                
            }
            if(bufShow.length()>0)
            	result=bufShow.toString().substring(0, bufShow.toString().length()-3);
        }catch (Exception e) {
        	throw new YssException("获取组合群信息出错", e);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
        return result;
      } 
	

	//20130122 added by liubo.Story #3213
	//每次更新组合群信息后，都需要更新一次组合群列表视图
	private void createViewOfAssetGroupList() throws YssException
	{
		String strSql = "";
		String sqlView = "";
		ResultSet rs = null;
		
		try
		{
			strSql = "select * from tb_sys_assetgroup order by FAssetGroupCode";
			rs = dbl.queryByPreparedStatement(strSql);
			while(rs.next())
			{
				/**add---shashijie 2013-2-4 BUG 7030组合群复制报错的问题*/
				//循环组合群中若有以下任意一张表不存在则不拼接SQL,否则会报错
				if (!dbl.yssTableExist("Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp")//关联公司信息
						|| !dbl.yssTableExist("Tb_" + rs.getString("FAssetGroupCode") + "_para_portfolio")//组合设置
						|| !dbl.yssTableExist("Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_Relaship")//组合设置关联
						){
					continue;
				}
				/**end---shashijie 2013-2-4 BUG 7030 */
				if(sqlView.trim().length() == 0)
				{
					sqlView = " select " + dbl.sqlString(rs.getString("FAssetGroupCode")) + " as FAssetGroupCode,a.Fportcode,a.fportname,a.fassetcode,a.finceptiondate, " +
							  " Nvl(b.faffcorpcode,' ') as managerCode,Nvl(b.FAffCorpName,' ') as managerName, " +
							  " Nvl(c.faffcorpcode, ' ') as trusteeCode,Nvl(c.FAffCorpName,' ') as trusteeName " +
							  " from tb_" + rs.getString("FAssetGroupCode") + "_para_portfolio a " +
							  " left join  " +
							  "( " +
							  " select a.FportCode,b.faffcorpcode,b.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
							  " left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp b " +
							  " on a.FSubCode = b.FAffCorpCode and a.FRelaType = 'Manager' " +
							  " where a.FRelaType = 'Manager' " +
							  " ) b on a.Fportcode = b.FPortCode " +
							  " left join  " +
							  "( " +
							  " select a.FportCode,c.faffcorpcode,c.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
							  " left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp c " +
							  " on a.FSubCode = c.FAffCorpCode and a.FRelaType = 'Trustee' and a.FRelaGrade = 'primary' " +
							  " where  (a.FRelaType = 'Trustee' and FRelaGrade = 'primary') " +
							  " ) c on a.Fportcode = c.FPortCode ";
				}
				else
				{
					  sqlView += " union all " +
					  			" select " + dbl.sqlString(rs.getString("FAssetGroupCode")) + " as FAssetGroupCode,a.Fportcode,a.fportname,a.fassetcode,a.finceptiondate, " +
								" Nvl(b.faffcorpcode,' ') as managerCode,Nvl(b.FAffCorpName,' ') as managerName, " +
								" Nvl(c.faffcorpcode, ' ') as trusteeCode,Nvl(c.FAffCorpName,' ') as trusteeName " +
								" from tb_" + rs.getString("FAssetGroupCode") + "_para_portfolio a " +
								" left join  " +
								"( " +
								" select a.FportCode,b.faffcorpcode,b.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
								" left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp b " +
								" on a.FSubCode = b.FAffCorpCode and a.FRelaType = 'Manager' " +
								" where a.FRelaType = 'Manager' " +
								" ) b on a.Fportcode = b.FPortCode " +
								" left join  " +
								"( " +
								" select a.FportCode,c.faffcorpcode,c.FAffCorpName from Tb_" + rs.getString("FAssetGroupCode") + "_Para_Portfolio_RelaShip a  " +
								" left join Tb_" + rs.getString("FAssetGroupCode") + "_Para_AffiliatedCorp c " +
								" on a.FSubCode = c.FAffCorpCode and a.FRelaType = 'Trustee' and a.FRelaGrade = 'primary' " +
								" where  (a.FRelaType = 'Trustee' and FRelaGrade = 'primary') " +
								" ) c on a.Fportcode = c.FPortCode ";
				}
			}
			if (sqlView.trim().length() > 0)
			{
				sqlView = "create or replace view VQ_AssetGroupList as " + sqlView;
				dbl.executeSql(sqlView);
			}
		}
		catch(Exception ye)
		{
			throw new YssException("生成组合群列表视图出错：" + ye.getMessage());
		}
		finally
		{
			dbl.closeResultSetFinal(rs);
		}
	}
}
