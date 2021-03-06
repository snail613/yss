package com.yss.main.funsetting;

import java.io.*;
import java.sql.*;
import java.util.*;

import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.dao.*;
import com.yss.main.parasetting.*;
import com.yss.util.*;

/// <summary>
/// 后台流程设置的一个POJO类
/// author : fanghaoln
/// date   : 20090224
/// BugNO  : MS00003 QDV4.1-参数布局散乱不便操作
/// </summary>
public class FlowBean
    extends BaseDataSettingBean implements IDataSetting,
    Serializable {
    //=======================主流程相关的属性=====================================
    private String FFlowCode = ""; //流程代码
    private String FFlowName = ""; //流程名称
    private String FFlowPointID = ""; //流程点序号
    private String FFlowPointName = ""; //流程点名称
    private int FFlowType = 99; //流程类型 99代表全部类型
    private java.util.Date FDate = new java.util.Date(); //执行日期

    //=========================流程点相关属性=====================================
    private String FMenuCode = ""; //菜单代码
    private int FIsMust; //是否必须执行
  //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A  
    private String daoGroup="";//接口群
  //--------------------------------------------------------------------  
    private String sFIsMust; //是否必须执行汉字形式
    private String FPorts = " "; //适用组合
    private String FRelate = " "; //关联项
    private String FDependence = " "; //依赖项
    private int FState; //流程点状态
    private String FPortCodes = ""; //已执行组合 2009.04.17 蒋锦 添加

    private FlowBean filterType = null;
    private String oldFFlowCode = " "; //用来放已前的流程代码，主要用来当查询条件用
    private String sRecycled = ""; //用来保存更新状态传来的字符串，审核，反审核，回收站
    private String sFlowPoint = ""; //用来保存前台传来流程节点的的字符串
    private String FMenuName = ""; //菜单编号
    private String FFunCode = ""; //功能模块代码

    private String SFIsMust;
    public FlowBean() {
    }

    //这一个公共的方法，用来验证要保存的设置信息
    public void checkInput(byte btOper) throws YssException {
        dbFun.checkInputCommon(btOper, pub.yssGetTableName("Tb_Fun_Flow"),
                               "FFlowCode", this.FFlowCode, this.oldFFlowCode);

    }

    /**修改时间：090224
     * 修改人：fanghaoln
     * BugNO  : MS00003 QDV4.1-参数布局散乱不便操作
     * 方法功能：流程设置界面新增一个流程调用此方法
     * @throws YssException
     */
    public String addSetting() throws YssException {
        Connection conn = null;
        boolean bTrans = true; //用来设置数据库的事物提交的
        StringBuffer bufSql = new StringBuffer();
        try {
            conn = dbl.loadConnection(); //得到一个数据库联接
            conn.setAutoCommit(false); //设置事物提交为手动提交，打开一个事物

            if (this.FFlowCode != null && !FFlowCode.trim().equals("")) {
                //===================插入一条主流程信息===================================
                //增加之前先删除这个流程的主流程信息,防止出错
                bufSql.append("delete from ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                    .append(" where FFlowCode=").append(dbl.sqlString(FFlowCode))
                    .append(" and FFlowPointID=-1");
                dbl.executeSql(bufSql.toString());

                //清空bufSql,重新实例化的话会浪费资源
                bufSql.delete(0, bufSql.length());

                //将主流程信息保存到数据库
                bufSql.append("insert into ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                    .append(" (FFlowCode,FFlowName,FFlowPointID,FFlowPointName,FFlowType,FMenuCode,")
                    //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A
                    .append("FDaoGroup,")//接口群
                    //--------------------------------------------------------------------
                    .append(" FIsMust,FPorts,FRelate,FDependence,FCheckState,FCreator,FCreateTime) values(")
                    .append(dbl.sqlString(this.FFlowCode)).append(",") //流程代码
                    .append(dbl.sqlString(this.FFlowName)).append(",") //流程名称
                    .append("-1,") //增加首个流程就是流程根节点它的流程节点序号为了-1
                    .append(dbl.sqlString(this.FFlowPointName)).append(",") //流程节点序号
                    .append(this.FFlowType).append(",") //流程类型
                    .append(dbl.sqlString(this.FMenuCode)).append(",") //菜单条
                    //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A
                    .append(dbl.sqlString(this.daoGroup)).append(",")//接口群
                    //--------------------------------------------------------------------
                    .append(this.FIsMust).append(",") //是否必须执行
                    .append(dbl.sqlString(this.FPorts)).append(",") //可用组合
                    .append(dbl.sqlString(this.FRelate)).append(",") //关联项
                    .append(dbl.sqlString(this.FDependence)).append(",") //依赖项
                    .append( (pub.getSysCheckState() ? "0" : "1")).append(",") //审核状态
                    .append(dbl.sqlString(this.creatorName)).append(",") //创建者
                    .append(dbl.sqlString(this.creatorTime)).append(")"); //创建时间
                dbl.executeSql(bufSql.toString());
                //===============================End Insert============================
            }
            //判断可有前台流程设置里listview里可传来流程节
            if (this.sFlowPoint != null && !this.sFlowPoint.trim().equals("")) {
                this.saveMutliSetting(sFlowPoint); //把流程节点保存到数据库中
            }
            conn.commit(); //提交事物
            conn.setAutoCommit(true); //关闭事物
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("添加流程设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**修改时间：090224
     * 修改人：fanghaoln
     * BugNO  : MS00003 QDV4.1-参数布局散乱不便操作
     * 方法功能：流程设置界面点修改事件时调用此方法
     * @throws YssException
     */
    public String editSetting() throws YssException {
        Connection conn = dbl.loadConnection(); //得到一个数据库联接
        StringBuffer bufSql = new StringBuffer(); //存放sql语句
        boolean bTrans = true; //用来设置数据库的事物提交的
        try {
            conn.setAutoCommit(false); //设置事物提交为手动提交，从而控制事物

            //=============================更新主流程信息===============================
            bufSql.append(" update ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                .append(" set FFlowCode=").append(dbl.sqlString(this.FFlowCode)) //更新流程代码
                .append(",FFlowName=").append(dbl.sqlString(this.FFlowName)) //更新流程名称
                .append(",FFlowType =").append(this.FFlowType) //更新流程类型
                .append(" where FFlowCode=").append(dbl.sqlString(this.oldFFlowCode))
                .append(" and FFlowPointID=-1"); //判断条件为修改前的流程代码，并且流程点编号为-1，即流程主信息
            dbl.executeSql(bufSql.toString());
            //==============================End Update================================

            //判断是否更新流程点
            if (sFlowPoint != null && !sFlowPoint.trim().equals("")) {
                this.saveMutliSetting(sFlowPoint); //把修改后的流程节点保存到数据库中
            }
            conn.commit(); //提交事物
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("修改流程设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    /**修改时间：090224
     * 修改人：fanghaoln
     * BugNO  : MS00003 QDV4.1-参数布局散乱不便操作
     * 方法功能：反审核里的删除事件，其实是对数据库的一个更新操作，是把数据状态更析到回收站里
     * @throws YssException
     */
    public void delSetting() throws YssException {
        Connection conn = null; //数据库连接
        Statement st = null; //游标变量，用来批处理
        StringBuffer bufSql = null; //存放sql语句
        String[] arrData = null; //当进行多条删除时候保存多条删除对象的字符数组
        boolean bTrans = true; //用来设置数据库的事物提交的
        try {
            //创建对象实例
            conn = dbl.loadConnection();
            conn.setAutoCommit(false); //设置事物提交为手动提交，控制事物

            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && !sRecycled.trim().equals("")) {
                st = conn.createStatement();
                bufSql = new StringBuffer();

                arrData = sRecycled.split("\r\n"); //解析传来字符串把它放到字符数组中
                for (int i = 0; i < arrData.length; i++) { //从字符数组中得到单条数据
                    if (arrData[i].trim().equals("")) { //判断单条数据是否为""，为""直接执行下一个
                        continue;
                    }
                    this.parseRowStr(arrData[i]); //把字符串解析成FlowBean

                    //更新主流程信息到回收站
                    bufSql.append("update ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                        .append("  set FCheckState = ").append(this.checkStateId)
                        .append(", FCheckUser = ").append(dbl.sqlString(pub.getUserCode()))
                        .append(", FCheckTime =").append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date())))
                        .append("  where FFlowCode = ").append(dbl.sqlString(this.FFlowCode))
                        .append("  and FFlowPointID=-1");
                    st.addBatch(bufSql.toString());
                    //清空bufSql,重新实例化的话会浪费资源
                    bufSql.delete(0, bufSql.length());
                }
            }
            if (st != null)
            {
            	st.executeBatch();
            }
            conn.commit(); //提交事物
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("删除流程设置出错", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**修改时间：090224
     * 修改人：fanghaoln
     * 方法功能：审核，反审核，反审核删除到回收站里,把信息从回收站还原到数据库，并可以同时处理多条信息
     * @throws YssException
     */
    public void checkSetting() throws YssException {
        Connection conn = null; //数据库联接
        Statement st = null; //用来进行批量处理的游标
        StringBuffer bufSql = null; //存放sql语句
        String[] arrData = null; //当进行多条数据保存多条数据的字符数组
        boolean bTrans = true; //用来设置数据库的事物提交的
        try {
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && !sRecycled.trim().equals("")) {
                //获取和创建对象
                conn = dbl.loadConnection();
                st = conn.createStatement();
                bufSql = new StringBuffer();

                conn.setAutoCommit(false); //设置事物提交为手动提交，打开一个事物
                arrData = sRecycled.split("\r\n"); //解析传来字符串把它放到字符数组中
                for (int i = 0; i < arrData.length; i++) { //从字符数组中得到单条数据
                    if (arrData[i].trim().equals("")) { //如果要解析的数据是""，直接跳到下一条
                        continue;
                    }
                    this.parseRowStr(arrData[i]); //把字符串解析成FlowBean

                    //更新主流程的审核状态
                    bufSql.append("update ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                        .append(" set FCheckState=").append(this.checkStateId)
                        .append(", FCheckUser = ").append(dbl.sqlString(pub.getUserCode()))
                        .append(", FCheckTime = ").append(dbl.sqlString(YssFun.formatDatetime(new java.util.Date())))
                        .append(" where FFlowCode=").append(dbl.sqlString(this.FFlowCode))
                        .append(" and FFlowPointID = -1");
                    st.addBatch(bufSql.toString());
                    //清空bufSql,重新实例化的话会浪费资源
                    bufSql.delete(0, bufSql.length());
                }
            }
            if (conn != null)
            {
	            conn.commit(); //提交事物
	            conn.setAutoCommit(true); //关闭事物
            }
            if (st != null)
            {
            	st.executeBatch();
            }
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("审核流程设置出错", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
        }

    }

    public IDataSetting getSetting() throws YssException {
        return null;
    }

    public String getAllSetting() throws YssException {
        return "";
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

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：这个方法得到的是筛选用的筛选条件
     * @throws YssException
     */
    private String filterSql() {
        String strSql = "";
        //作为主流程信息的筛选条件，一定要添加流程点=-1的条件
        //因为这个条件才能匹配到主流程的信息，否则会将流程点的信息也加载出来
        //Modify By sunkey 20090301
        strSql = " where FFlowPointID=-1 ";
        if (this.filterType != null) {
            if (this.filterType.FFlowCode != null &&
                filterType.FFlowCode.trim().length() != 0) { //判断是否筛选事件且有筛选代码
                strSql += " and FFlowCode like '" +
                    filterType.FFlowCode.replaceAll("'", "''") + "%'"; //通过流程代码进行的筛选
            }
            if (filterType.FFlowName != null &&
                filterType.FFlowName.trim().length() != 0) { //判断是否筛选事件且筛选名称不为空
                strSql += " and FFlowName like '" +
                    filterType.FFlowName.replaceAll("'", "''") + "%'"; //通过流程名称进行的筛选
            }
            if (filterType.FFlowType != 99) { //根据流程类型筛选
                strSql += " and FFlowType=" + filterType.FFlowType; //通过流程名称进行的筛选
            }
        }
        return strSql;
    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：首次加载数据调用此方法，得到前台需要的listview显示内容，和listviewItem里要保存的内容
     * @throws YssException
     */
    public String getListViewData1() throws YssException {
        ResultSet rs = null; //存放结果集
        String sVocStr = ""; //用来实现前台必须执行写活
        String sHeader = ""; //用来存放表头
        String sAllDataStr = "";
        String sShowDataStr = ""; //保存表头，listview显示的内容，listview里的内容
        StringBuffer bufShow = new StringBuffer(); //组合listview显示的内容
        StringBuffer bufAll = new StringBuffer(); //组合存放listview里的内容
        StringBuffer bufSql = new StringBuffer(); //存放sql语句
        try {
            sHeader = this.getListView1Headers(); //listview里面显示的最上面的表头

            //使用流程表左连接词汇信息进行流程信息的查询
            bufSql.append("select a.*,b.FVocName as sFlowType ")
                .append(" from ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                .append(" a left join Tb_Fun_Vocabulary b on ")
                .append(dbl.sqlToChar("a.FFlowType")).append(" = b.FVocCode")
                .append(this.filterSql())
                .append(" and b.FVocTypeCode = ")
                .append(dbl.sqlString(YssCons.YSS_FUN_FLOWTYPE))
                .append(" order by FFlowCode");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                bufShow.append(rs.getString("FFlowCode")).append("\t"); //流程代码
                bufShow.append(rs.getString("FFlowName")).append("\t"); //流程名称
                bufShow.append(rs.getString("sFlowType")).append("\t"); //流程类型
                bufShow.append(rs.getString("FCreator")).append("\t"); //创建人
                bufShow.append(rs.getString("FCreateTime")).append("\t"); //创建时间
                bufShow.append(rs.getString("FCheckUser")).append("\t"); //复核人
                bufShow.append(rs.getString("FCheckTime")).append("\t"); //复核时间
                bufShow.append(YssCons.YSS_LINESPLITMARK); //解析符号

                //这是第一次加载时放在listview里的内容，当点修改，复制，审核，反审核等在出现的流程设置界面出现下面这些内容。
                this.FFlowCode = rs.getString("FFlowCode"); //流程代码，后面加载的流程节点就是通过它加载出来的
                this.FFlowName = rs.getString("FFlowName"); //流程名称
                this.FFlowType = rs.getInt("FFlowType"); //流程类型
                super.checkStateId = rs.getInt("Fcheckstate"); //这是设置YUN类的状态使审核未审核，回收站分开。

                bufAll.append(this.buildRowStr()).append(YssCons.YSS_LINESPLITMARK); //加个协议用于前台解析
            }
            //如果长度大于2，则删除结尾的\f\f分隔符
            if (bufShow.toString().length() > 2) {
                sShowDataStr = bufShow.toString().substring(0,
                    bufShow.toString().length() - 2);
            }
            //如果长度大于2，则删除结尾的\f\f分隔符
            if (bufAll.toString().length() > 2) {
                sAllDataStr = bufAll.toString().substring(0,
                    bufAll.toString().length() - 2);
                this.getListView1ShowCols(); //把表头+listview里显示的内容+listview里存放的内容组合起来
            }
            //====================获取词汇必须执行和流程类型对应的词汇名===================
            VocabularyBean vocabulary = new VocabularyBean();
            vocabulary.setYssPub(pub);
            sVocStr = vocabulary.getVoc(YssCons.YSS_FUN_FISMUST + "," +
                                        YssCons.YSS_FUN_FLOWTYPE); //加入必须执行这个词汇

            return sHeader + "\r\f" + sShowDataStr + "\r\f" +
                sAllDataStr + "\r\f" +
                this.getListView1ShowCols() + "\r\f" + "voc" + sVocStr;

        } catch (Exception e) {
            throw new YssException("获取流程设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：流程设置界面调用此方法进行加载,加载的是流程设置界面里显示流程节点项的内容
     * @throws YssException
     */
    public String getListViewData2() throws YssException {
        ResultSet rs = null; //结果集
        String sHeader = ""; //表头
        String sAllDataStr = ""; //ListView中的数据
        String sShowDataStr = ""; //ListView中显示的数据
        StringBuffer bufShow = new StringBuffer(); //组合listview显示的内容
        StringBuffer bufAll = new StringBuffer(); //组合存放listview里的内容
        StringBuffer bufSql = new StringBuffer(); //存放sql语句
        try {
            sHeader = "流程点序号\t流程点名称\t必须执行"; //listview里面显示的最上面的表头

            //通过流程信息表关联菜单条、词汇查询流程点信息
            bufSql.append("select a.*,b.fbarname,c.FVocName")
                .append(" from ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                .append(" a left join ").append(pub.yssGetTableName("TB_FUN_MENUBAR"))
                .append(" b on a.FMenuCode=b.fbarcode ")
                .append(" left join Tb_Fun_Vocabulary c on ")
                .append(dbl.sqlToChar("a.FIsMust")).append(" = c.FVocCode")
                .append(" where c.FVocTypeCode = ").append(dbl.sqlString(YssCons.YSS_FUN_FISMUST))
                .append(" and a.FFlowCode = ").append(dbl.sqlString(this.FFlowCode))
                .append(" and FFlowPointID<>-1 ").append(" order by FFlowPointID ");

            rs = dbl.openResultSet(bufSql.toString()); //得到结果集
            while (rs.next()) {
                bufShow.append(rs.getInt("FFlowPointID")).append("\t"); //listview里显示流程节点序号
                bufShow.append(rs.getString("FFlowPointName")).append("\t"); //listview里显示流程节点名称
                bufShow.append(rs.getString("FVocName")).append("\t"); //listview里显示是否必须执行
                bufShow.append(YssCons.YSS_LINESPLITMARK); //加个协议方便前台解析

                //下面这是listview里面保存的内容
                this.FFlowCode = rs.getString("FFlowCode"); //流程代码
                this.FFlowName = rs.getString("FFlowName"); //流程名称
                this.FFlowType = rs.getInt("FFlowType"); //流程类型
                super.checkStateId = rs.getInt("Fcheckstate"); //审核状态
                this.FFlowPointID = rs.getString("FFlowPointID"); //流程节点序号
                this.FFlowPointName = rs.getString("FFlowPointName"); //流程节点名称
                this.FMenuCode = rs.getString("FMenuCode"); //菜单条
              //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A
                this.daoGroup=rs.getString("FDaoGroup");//接口群
              //--------------------------------------------------------------------  
                this.sFIsMust = rs.getString("FVocName"); //是否必须执行得到的汉字形式
                this.FPorts = rs.getString("FPorts") == null ? "" :
                    rs.getString("FPorts"); //组合项
                this.FRelate = rs.getString("FRelate") == null ? "" :
                    rs.getString("FRelate"); //关联项
                this.FDependence = rs.getString("FDependence") == null ? "" :
                    rs.getString("FDependence"); //依赖项
                this.FMenuName = rs.getString("fbarname"); //菜单条

                //把listview里的内容组合起来加协议\f\f
                bufAll.append(this.buildPointRowStr()).
                    append(YssCons.YSS_LINESPLITMARK);
            }
            //===========删除字符buff结尾的\f\f
            if (bufShow.toString().length() > 2) {
                bufShow.delete(bufShow.length() - 2, bufShow.length());
            }
            if (bufAll.toString().length() > 2) {
                bufAll.delete(bufAll.length() - 2, bufAll.length());
            }
        } catch (Exception e) {
            throw new YssException("获取流程设置出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        //把表头+listview里显示的内容+listview里存放的内容组合起来返回
        return sHeader + "\r\f" + bufShow.toString() + "\r\f" +
            bufAll.toString() + "\r\f" + this.getListView1ShowCols();

    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：加载出流程节点界面组合项，关联项，依赖项的内容
     * @throws YssException
     */
    public String getListViewData3() throws YssException {
        ResultSet rs = null; //结果集

        String[] sPorts = this.FPorts.split(","); //解析前台传来的组合项
        String sRelate = this.FRelate; //解析前台传来的关联项
        String sDependence = this.FDependence; //解析前台传来的依赖项

        String sPortsBean = ""; //存放所有组合代码的内容
        String sShowPorts = ""; //所有显示的组合内容
        String sShowRelate = ""; //所有显示的依赖项
        String sShowDependence = ""; //所有显示的关联项
        StringBuffer bufSql = new StringBuffer(); //拼装Sql语句
        StringBuffer bufPorts = new StringBuffer(); //拼装组合代码
        StringBuffer bufRelate = new StringBuffer(); //拼装关联项
        StringBuffer bufDependence = new StringBuffer(); //拼装依赖项

        //创建组合对象，并传递需要的Pub
        PortfolioBean portfolioBean = new PortfolioBean();
        portfolioBean.setYssPub(pub);

        //设置关联、依赖项，如果不是以，结尾，添加，号，在下面会根据，号解析
        if (!sRelate.endsWith(",")) {
            sRelate += ",";
        }
        if (!sRelate.startsWith(",")) {
            sRelate = "," + sRelate;
        }
        if (!sDependence.endsWith(",")) {
            sDependence += ",";
        }
        if (!sDependence.startsWith(",")) {
            sDependence = "," + sDependence;
        }

        try {
            //========把组合项要的内容给查出来==========
            sPortsBean = portfolioBean.getListViewData2(); //存放所有加载出的组合
            String[] ports = sPortsBean.split("\r\f"); //解析得到的组合代码
            String bufPort = ports[2]; //得到我们想要的组合代码
            for (int i = 0; i < sPorts.length; i++) { //把到组合码和前台的组合代码进行对比，得到我前台相等的项
                String[] sbufports = bufPort.split("\f\f"); //解析后台加载出来的组合代码转变成单个组合项
                for (int j = 0; j < sbufports.length; j++) { //循环遍历这些组合代码
                    String[] strports = sbufports[j].split("\t"); //分解单个组合代码
                    if (strports[0].equalsIgnoreCase(sPorts[i])) { //合前台传来的组合代码进行匹配
                        bufPorts.append(sbufports[j]); //匹配相等就是我们要的组合代码保存起来
                        bufPorts.append(YssCons.YSS_LINESPLITMARK); //在后面加个字符帮住前台解析
                    }
                }
            }

            //=通过流程信息表关联菜单条、词汇，然后匹配流程代码和流程点信息进行查询
            bufSql.append("select a.*,b.fbarname,c.FVocName from ")
                .append(pub.yssGetTableName("Tb_Fun_Flow"))
                .append(" a left join ").append(pub.yssGetTableName("TB_FUN_MENUBAR"))
                .append(" b on a.FMenuCode=b.fbarcode left join ")
                .append(pub.yssGetTableName("Tb_Fun_Vocabulary"))
                .append(" c on ")
                .append(dbl.sqlToChar("a.FIsMust")).append(" = c.FVocCode")
                .append(" where c.FVocTypeCode = ")
                .append(dbl.sqlString(YssCons.YSS_FUN_FISMUST))
                .append(" and a.FMenuCode=b.fbarcode ")
                .append(" and a.FFlowCode = ").append(dbl.sqlString(this.FFlowCode));

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                //下面这是listview里面保存的内容
                this.FFlowCode = rs.getString("FFlowCode"); //流程代码
                this.FFlowName = rs.getString("FFlowName"); //流程名称
                this.FFlowType = rs.getInt("FFlowType"); //流程类型
                super.checkStateId = rs.getInt("Fcheckstate"); //审核状态
                this.FFlowPointID = rs.getString("FFlowPointID"); //流程节点序号
                this.FFlowPointName = rs.getString("FFlowPointName"); //流程节点名称
                this.FMenuCode = rs.getString("FMenuCode"); //菜单条
                //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A
                this.daoGroup=rs.getString("FDaoGroup");//接口群
                //--------------------------------------------------------------------  
                this.sFIsMust = rs.getString("FVocName"); //是否必须执行得到的汉字形式
                this.FPorts = rs.getString("FPorts"); //组合项
                this.FRelate = rs.getString("FRelate"); //关联项
                this.FDependence = rs.getString("FDependence"); //依赖项
                this.FMenuName = rs.getString("fbarname"); //菜单条

                //如果关联项字符串中包含此关联项，则添加到关联项StringBuffer中
                if (sRelate.indexOf("," + FFlowPointID + ",") > -1) {
                    bufRelate.append(this.buildPointRowStr()).append(YssCons.
                        YSS_LINESPLITMARK); //组合关联节点的内容
                }
                //如果依赖项字符串中包含此依赖项，则添加到依赖项StringBuffer中
                if (sDependence.indexOf("," + FFlowPointID + ",") > -1) {
                    bufDependence.append(this.buildPointRowStr()).append(
                        YssCons.YSS_LINESPLITMARK); //组合依赖节点的内容
                }
            }

            //========================删除多加的\f\f==================================
            if (bufPorts.toString().length() > 2) {
                bufPorts.delete(bufPorts.length() - 2, bufPorts.length());
            }
            if (bufRelate.toString().length() > 2) {
                bufRelate.delete(bufRelate.length() - 2, bufRelate.length());
            }
            if (bufDependence.toString().length() > 2) {
                bufDependence.delete(bufDependence.length() - 2, bufDependence.length());
            }
            //=========================end delete===================================
        } catch (Exception e) {
            throw new YssException("获取流程设置点出错", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        //把查到的组合项，依赖项，关联项组合起来作为返回值返回
        return bufPorts.toString() + "\r\f" + bufRelate.toString() + "\r\f" +
            bufDependence.toString() + "\r\f";
    }

    public String getListViewData4() throws YssException {
        return "";
    }

    public String getBeforeEditData() throws YssException {
        return "";
    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：对前台传过来的字符串进行解析
     * @throws YssException
     */
    public void parseRowStr(String sRowStr) throws YssException {
        String[] reqAry = null; //解析的字符串数组
        String sTmpStr = ""; //解析的字符串
        try {
            if (sRowStr.trim().length() == 0) { //看是否传来内容，没有的话直接反回
                return;
            }
            if (sRowStr.indexOf("\r\t") >= 0) { //解析前台传来的内容
                sTmpStr = sRowStr.split("\r\t")[0]; //提取它的第一项，并把它解析出来
                if (sRowStr.split("\r\t").length == 3) { //这里是判断传来的内容里是否有流程节点的内容
                    sFlowPoint = sRowStr.split("\r\t")[2]; //提取流程节点的内容
                }

            } else {
                sTmpStr = sRowStr;
            }
            sRecycled = sRowStr; //用来更新操作保存的数据
            reqAry = sTmpStr.split("\t"); //解析前台传来的字符串
            this.FFlowCode = reqAry[0]; //流程代码
            this.FFlowName = reqAry[1]; //流程名称
            this.FFlowPointID = reqAry[2]; //流程节点序号
            this.FFlowPointName = reqAry[3]; //流程节点名称
            this.FFlowType = Integer.parseInt(reqAry[4]); //流程类型
            this.FMenuCode = reqAry[5]; //菜单条
            String sFIsMust = reqAry[6]; //是否必须执行
            if (sFIsMust.equalsIgnoreCase("否")) { //把前台传来的字符串转化为数字
                this.FIsMust = 0; //否对应的是0
            } else {
                this.FIsMust = 1; //是对应的是1
            }
            this.FPorts = reqAry[7]; //组项
            this.FRelate = reqAry[8]; //关联项
            this.FDependence = reqAry[9]; //依赖项
            this.FDate = YssFun.parseDate(reqAry[10]); //执行日期
            this.checkStateId = Integer.parseInt(reqAry[11]); //审核状态
            this.oldFFlowCode = reqAry[12]; //存放未改变前的流程代码
            this.FMenuName = reqAry[13]; //菜单名称
            //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A  
            if(reqAry[14]==null||reqAry[14].length()==0||reqAry[14].equalsIgnoreCase("null")){
            	this.daoGroup=" ";
            }else{
            	this.daoGroup=reqAry[14];//接口群
            }
            
           //--------------------------------------------------------------------- 
            super.parseRecLog(); //设置一些公共属性
            if (sRowStr.indexOf("\r\t") >= 0) { //这是解析筛选时传来的内容
                if (this.filterType == null) {
                    this.filterType = new FlowBean();
                    this.filterType.setYssPub(pub);
                }
                if (!sRowStr.split("\r\t")[1].equalsIgnoreCase("[null]")) { //得到筛选传来是否为空
                    this.filterType.parseRowStr(sRowStr.split("\r\t")[1]); //解析筛选传来的内容
                }
            }
        } catch (Exception e) {
            throw new YssException("解析流程设置信息出错!", e);
        }

    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：组合后台查到的数据传送给前台
     * @throws YssException
     */
    public String buildRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.FFlowCode).append("\t"); //流程代码
        buf.append(this.FFlowName).append("\t"); //流程名称
        buf.append(this.FFlowPointID).append("\t"); //流程节点序号
        buf.append(this.FFlowPointName).append("\t"); //流程节点名称
        buf.append(this.FFlowType).append("\t"); //流程类型
        buf.append(this.FMenuCode).append("\t"); //菜单条
        buf.append(this.FIsMust).append("\t"); //是否必须执行
        buf.append(this.FPorts).append("\t"); //组合项
        buf.append(this.FRelate).append("\t"); //关联项
        buf.append(this.FDependence).append("\t"); //依赖项
        buf.append(YssFun.formatDate(this.FDate, "yyyy-MM-dd")).append("\t"); //执行日期
        buf.append(this.FPortCodes).append("\t"); //已执行组合
        buf.append(this.FState).append("\t"); //执行状态
        buf.append(this.FMenuName).append("\t"); //菜单名称
        buf.append(this.FFunCode).append("\t"); //功能模块代码
        //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A  
        buf.append(this.daoGroup).append("\t");//接口群
        //--------------------------------------------------------------------
        buf.append(super.buildRecLog());
        return buf.toString();
    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：组合流程点的方法，在viewData2方法时用到放入前台的viewflowpoint
     * @throws YssException
     */
    public String buildPointRowStr() throws YssException {
        StringBuffer buf = new StringBuffer();
        buf.append(this.FFlowPointID).append("\t"); //流程节点序号
        buf.append(this.FFlowPointName).append("\t"); //流程节点名称
        buf.append(this.FMenuCode).append("\t"); //菜单条
        buf.append(this.sFIsMust).append("\t"); //是否必须执行
        buf.append(this.FPorts).append("\t"); //组合项
        buf.append(this.FRelate).append("\t"); //关联项
        buf.append(this.FDependence).append("\t"); //依赖项
        buf.append(this.FMenuName).append("\t"); //菜单条
        //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A  
        buf.append(this.daoGroup).append("\t");//接口群
        //--------------------------------------------------------------------
        return buf.toString();
    }

    /**
     * 2009-02-27 蒋锦 添加
     * @return String：结果字符串
     * @throws YssException
     */
    private String getSingleFlow() throws YssException {
        ResultSet rs = null;
        StringBuffer bufAll = new StringBuffer();
        StringBuffer bufSql = new StringBuffer();
        try {
            //事实上这里是想获取流程点的信息，而非流程+流程点的信息
            //通过sql语句查询流程点的状态和对应的菜单条对象（主要是为了调用窗体时用）
            //Modify by sunkey 20090302 BugNO:QDV4.1-MS00003 参数布局分散不便操作
            //普通流程的状态可以直接查询，不必跟时间
            bufSql.append("SELECT * FROM TB_FUN_FLOW C LEFT JOIN ")
                .append("(select * from Tb_Fun_Menubar a JOIN Tb_Fun_RefInvoke b ")
                .append("on a.FRefInvokeCode = b.FRefInvokeCode) D ")
                .append("ON C.FMENUCODE = D.FBARCODE ");
            //每日业务的流程状态和时间绑定，采用时间查询
            if (this.FFlowType == 1) {
                bufSql.append("LEFT JOIN ")
                    .append(pub.yssGetTableName("TB_PARA_FLOW"))
                    .append(" E ON ")
                    .append(
                        "(C.FFLOWCODE = E.FFLOWCODE AND C.FFLOWPOINTID = E.FEXECUTEID")
                    .append(" and E.FDate=").append(dbl.sqlDate(this.FDate))
                    .append(")");
            }
            bufSql.append(" WHERE C.FFLOWCODE=").append(dbl.sqlString(this.FFlowCode));
            bufSql.append(" order by C.FFlowPointID");

            rs = dbl.openResultSet(bufSql.toString());
            while (rs.next()) {
                //判断流程是否被反审核了
                if (rs.getInt("FFlowPointID") == -1 && rs.getInt("FCheckState") != 1) {
                    throw new YssException("抱歉，您点击的流程已被反审核，请稍后再试！");
                }
                //请不要误会，这里获取的并不是流程的信息，而是流程点的信息
                //事实上每个FlowBean记载下来的数据是流程点
                FlowBean flow = new FlowBean();
                flow.FFlowCode = rs.getString("FFlowCode");
                flow.FFlowName = rs.getString("FFlowName");
                flow.FFlowPointID = rs.getString("FFlowPointID");
                flow.FFlowPointName = rs.getString("FFlowPointName");
                flow.FFlowType = rs.getInt("FFlowType");
                //结束流程是无菜单代码的，设置为""，否则将为null，会报异常 sunkey 20090320
                flow.FMenuCode = rs.getString("FMenuCode") == null ? "" :
                    rs.getString("FMenuCode");
                //MS01272  add by zhangfa  2010.07.09      QDV4招商基金2010年6月8日01_A
                flow.daoGroup=rs.getString("FDaoGroup");
                //--------------------------------------------------------------------
                flow.FIsMust = rs.getInt("FIsMust");
                flow.FRelate = rs.getString("FRelate") == null ? "" :
                    rs.getString("FRelate");
                flow.FDependence = rs.getString("FDependence") == null ? "" :
                    rs.getString("FDependence");
                flow.FPorts = rs.getString("FPorts") == null ? "" :
                    rs.getString("FPorts");
                //如果是每日流程，则从状态表中取执行的组合
                if (this.FFlowType == 1) {
                    flow.FPortCodes = rs.getString("FPortCodes") == null ? "" :
                        rs.getString("FPortCodes"); //2009.04.17 蒋锦 添加
                }
                //每日流程，添加状态和
                if (this.FFlowType == 1) {
                    flow.FState = rs.getInt("FState");
                    flow.FFunCode = rs.getString("FRemark") == null ? "" :
                        rs.getString("FRemark");
                }
                //创建菜单条对象
                MenubarBean menu = new MenubarBean();
                //如果FBarCode==null，不执行下面，否则会有异常
                if (rs.getString("FBARCODE") != null) {
                    menu.setMenubarCode(rs.getString("FBARCODE"));
                    menu.setMenubarName(rs.getString("FBARNAME"));
                    menu.setdllName(rs.getString("FDLLNAME"));
                    menu.setclassName(rs.getString("FCLASSNAME"));
                    menu.setmethodName(rs.getString("FMETHODNAME"));
                    menu.setparams(rs.getString("FPARAMS"));
                    menu.setTabMainCode(rs.getString("ftabmaincode"));//add by yeshenghog BUG3728 20120222
                    menu.setTabMainName(rs.getString("ftabmainname"));
                }
                
                bufAll.append(flow.buildRowStr()).append("\r\f");
                //将菜单信息添加到记录
                if(menu.getTabMainCode()==null)
                {
                	menu.setTabMainCode("");
                }
                if(menu.getTabMainName()==null)
                {
                	menu.setTabMainName("");
                }
                bufAll.append(menu.buildRowStr()).append(YssCons.YSS_LINESPLITMARK);
            }
            if (bufAll.length() > 0) {
                bufAll.delete(bufAll.length() - 2, bufAll.length());
            }
        } catch (Exception e) {
            throw new YssException("加载流程【" + this.FFlowName + "】子项过程出现异常！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bufAll.toString();
    }

    public String getOperValue(String sType) throws YssException {
        String sResult = "";
        try {
            if (sType.equalsIgnoreCase("getsingleflow")) {
                sResult = getSingleFlow();
            }
        } catch (Exception e) {
            throw new YssException("操作流程出错！", e);
        }
        return sResult;
    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：从回收站删除数据，即从数据库彻底删除数据，并可以同时处理多条信息
     * @throws YssException
     */
    public void deleteRecycleData() throws YssException {
        StringBuffer bufSql = new StringBuffer();
        String[] arrData = null;
        boolean bTrans = true; //代表是否开始了事务
        Connection conn = dbl.loadConnection(); ;
        Statement st = null;
        try {
            conn.setAutoCommit(false);
            //如果sRecycled不为空，就按解析sRecycled中的字符串，然后一个一个来执行sql语句
            if (sRecycled != null && !sRecycled.trim().equals("")) {
                st = conn.createStatement();
                //根据规定的符号，把多个sql语句分别放入数组
                arrData = sRecycled.split("\r\n");
                //循环执行这些删除语句
                for (int i = 0; i < arrData.length; i++) {
                    if (arrData[i].length() == 0) {
                        continue;
                    }
                    this.parseRowStr(arrData[i]);
                    bufSql.append("delete from ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                        .append(" where FFlowCode = ").append(dbl.sqlString(this.FFlowCode));
                    st.addBatch(bufSql.toString());
                    //清空StringBuffer
                    bufSql.delete(0, bufSql.length());
                }
                //执行批量删除
                st.executeBatch();
            }
            //sRecycled如果sRecycled为空,而oldFFlowCode不为空,则按照oldFFlowCode来执行sql语句
            else if (oldFFlowCode != null && !oldFFlowCode.trim().equals("")) {
                bufSql.append("delete from ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                    .append(" where FFlowCode = ").append(dbl.sqlString(this.oldFFlowCode));
                //执行删除
                dbl.executeSql(bufSql.toString());
            }
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("清除数据出错", e);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }

    }

    /**创建时间：090224
     * 创建人:fanghaoln
     * 方法功能：往数据库里插入数据，增加和修改都会调用些方法，增加插入数据，修改删除以前的数据加入新的数据
     * @throws YssException
     */
    public String saveMutliSetting(String sMutilRowStr) throws YssException {
        Connection conn = null;
        StringBuffer bufSql = new StringBuffer();
        Statement st = null;
        boolean bTrans = true; //默认自动回滚事物

        try {
            conn = dbl.loadConnection();
            st = conn.createStatement();
            //设置事物不自动提交
            conn.setAutoCommit(false);
            String[] sData = sMutilRowStr.split("\f\f");
            //将之前非流程主信息的数据删除，避免出现主键重复问题
            bufSql.append("delete from ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                .append(" where FFlowCode = ")
                .append(dbl.sqlString("".equals(oldFFlowCode) ? FFlowCode : oldFFlowCode))
                .append(" and FFlowPointID <> -1");
            dbl.executeSql(bufSql.toString());
            //清空StringBuffer
            bufSql.delete(0, bufSql.length());

            //因为流程和流程点保存在同一张表中，因此一个流程实际上在表中存在多条记录，故而使用循环插入
            //如果插入多条记录的话，使用批量插入比较好
            for (int i = 0; i < sData.length; i++) {
                this.parseRowStr(sData[i]);
                //插入数据的SQL语句
                bufSql.append("insert into ").append(pub.yssGetTableName("Tb_Fun_Flow"))
                    .append(" (FFlowCode,FFlowName,FFlowPointID,FFlowPointName,FFlowType,FMenuCode,")
                     //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A
                    .append("FDaoGroup,")//接口群
                    //--------------------------------------------------------------------
                    .append(" FIsMust,FPorts,FRelate,FDependence,FCheckState,FCreator,FCreateTime) values(")
                    .append(dbl.sqlString(this.FFlowCode)).append(",") //流程代码
                    .append(dbl.sqlString(this.FFlowName)).append(",") //流程名称
                    .append(this.FFlowPointID).append(",") //流程节点序号
                    .append(dbl.sqlString(this.FFlowPointName)).append(",") //流程节点名称
                    .append(this.FFlowType).append(",") //流程类型
                    .append(dbl.sqlString(this.FMenuCode)).append(",") //菜单条
                      //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A
                    .append(dbl.sqlString(this.daoGroup)).append(",")//接口群
                    //--------------------------------------------------------------------
                    .append(this.FIsMust).append(",") //是否必须执行
                    .append(dbl.sqlString(this.FPorts)).append(",") //组合项
                    .append(dbl.sqlString(this.FRelate)).append(",") //关联项
                    .append(dbl.sqlString(this.FDependence)).append(",") //依赖项
                    .append("0").append(",") //审核状态
                    .append(dbl.sqlString(this.creatorName)).append(",") //创建者
                    .append(dbl.sqlString(this.creatorTime)).append(")"); //创建时间
                st.addBatch(bufSql.toString());
                //清空StringBuffer
                bufSql.delete(0, bufSql.length());
            }
            //执行批量插入
            st.executeBatch();
            //执行成功，恢复自动提交功能，并交回滚事物修改为false
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception ex) {
            throw new YssException("保存流程过程中出现异常，请联系赢时胜工作人员!", ex);
        } finally {
            dbl.closeStatementFinal(st);
            dbl.endTransFinal(conn, bTrans);
        }
        return "";
    }

    //--------------------------------------------------------------------------------------------
    /**
     * 插入流程状态
     * @param flowState int
     * @throws YssException
     */
    public void addFlowState(int flowState) throws YssException {
        StringBuffer bufSql = new StringBuffer();
        Connection conn = dbl.loadConnection();
        //已被执行的组合 2009.04.17 蒋锦 添加
        String exdPortCodes = "";
        ResultSet rs = null;
        String sRemark = "";
        boolean bTrans = true;
        try {
            conn.setAutoCommit(false);

            //使用流程信息表连接每日流程信息查询路程状态信息
            bufSql.append("SELECT a.FMenuCode, b.FState, b.FRemark, b.FPortCodes, a.FPorts FROM (")
                .append("SELECT FMenuCode, FFlowPointID, FFlowCode, FPorts FROM TB_Fun_Flow) a")
                .append(" RIGHT JOIN (SELECT * FROM ")
                .append(pub.yssGetTableName("Tb_Para_Flow"))
                .append(" WHERE FFlowCode = ")
                .append(dbl.sqlString(this.FFlowCode))
                .append(" AND FExecuteID = ").append(this.FFlowPointID)
                .append(" AND FDate = ").append(dbl.sqlDate(this.FDate))
                .append(") b")
                .append(" ON a.FFlowCode = b.FFlowCode AND a.FFlowPointID = b.FExecuteID");

            rs = dbl.openResultSet(bufSql.toString());
            if (rs.next()) {
                if ("incomecalculate".equalsIgnoreCase(rs.getString("FMenuCode")) ||
                    "incomepaid".equalsIgnoreCase(rs.getString("FMenuCode")) ||
                    "interfacedeal".equalsIgnoreCase(rs.getString("FMenuCode"))) {
                    if (YssCons.YSS_FLOW_POINTSTATE_EXECUTION == rs.getInt("FState") &&
                        flowState == YssCons.YSS_FLOW_POINTSTATE_SUCCESS) {
                        sRemark = (rs.getString("FRemark") + "").replaceAll("null", "");
                        int iIndex = sRemark.indexOf(this.FFunCode);
                        if (iIndex != -1) {
                            sRemark = sRemark.replaceAll(FFunCode + ",|" +
                                FFunCode, "");
                            if (sRemark.endsWith(",")) {
                                sRemark = sRemark.substring(0, sRemark.length() - 1);
                            }
                        }
                        if (sRemark.trim().length() != 0) {
                            flowState = YssCons.YSS_FLOW_POINTSTATE_FALSE;
                        }
                    } else if (YssCons.YSS_FLOW_POINTSTATE_EXECUTION ==
                               rs.getInt("FState") &&
                               flowState == YssCons.YSS_FLOW_POINTSTATE_FALSE) {
                        sRemark = rs.getString("FRemark") + "";
                        sRemark = (sRemark + "," + FFunCode).replaceAll("null,", "");
                    } else if (YssCons.YSS_FLOW_POINTSTATE_EXECUTION == flowState) {
                        sRemark = rs.getString("FRemark") + "";
                    }
                }
                //---------2009.04.17 蒋锦 添加 判断适用组合是否已经执行完毕---------//
                //已执行的组合
                exdPortCodes = rs.getString("FPortCodes") == null ? "" : rs.getString("FPortCodes");
                if (rs.getString("FPorts") != null && flowState != YssCons.YSS_FLOW_POINTSTATE_EXECUTION) {
                    //适用组合
                    String ports = rs.getString("FPorts");
                    //被执行的组合
                    String[] portCodes = this.FPortCodes.split(",");
                    for (int i = 0; i < portCodes.length; i++) {
                        if (ports.indexOf(portCodes[i]) != -1) {
                            if (exdPortCodes.indexOf(portCodes[i]) != -1) {
                                continue;
                            } else {
                                exdPortCodes += ("," + portCodes[i]);
                            }
                        }
                    }
                    if (exdPortCodes.startsWith(",")) {
                        exdPortCodes = exdPortCodes.substring(1);
                    }
                    String[] arrPorts = ports.split(",");
                    for (int i = 0; i < arrPorts.length; i++) {
                        if (exdPortCodes.indexOf(arrPorts[i]) == -1) {
                            //设置执行状态为部分完成
                            if (flowState != YssCons.YSS_FLOW_POINTSTATE_FALSE) {
                                //部分执行成功
                                flowState = YssCons.YSS_FLOW_POINTSTATE_PARTOF;
                            }
                        }
                    }
                }
                //------------------------------------------------------------//
            }

            //======删除流程状态信息==================================================
            bufSql.delete(0, bufSql.length());
            bufSql.append("delete from ").append(pub.yssGetTableName("Tb_Para_Flow"))
                .append(" where FFlowCode =  ").append(dbl.sqlString(this.FFlowCode))
                .append(" and FExecuteID = ").append(this.FFlowPointID)
                .append(" and FDate = ").append(dbl.sqlDate(this.FDate));
            dbl.executeSql(bufSql.toString());
            //============================end delete ===============================

            //============================插入流程状态信息============================
            bufSql.delete(0, bufSql.length());
            bufSql.append("insert into ").append(pub.yssGetTableName("Tb_Para_Flow"))
                .append(" (FFlowCode,FExecuteID,FState,FDate, FRemark, FPortCodes) values(")
                .append(dbl.sqlString(this.FFlowCode)).append(",")
                .append(this.FFlowPointID).append(",")
                .append(flowState).append(",")
                .append(dbl.sqlDate(this.FDate)).append(",")
                .append(dbl.sqlString(sRemark.replaceAll("null", ""))).append(",")
                .append(dbl.sqlString(exdPortCodes)).append(")");
            dbl.executeSql(bufSql.toString());
            //-----------2009.03.11 蒋锦 添加 同时更新关联项的状态---------------//
            if (this.FRelate.trim().length() > 0 &&
                flowState == YssCons.YSS_FLOW_POINTSTATE_SUCCESS) {
                String sRelateCode = FRelate;
                if (sRelateCode.endsWith(",")) {
                    sRelateCode = sRelateCode.substring(0, FRelate.length() - 1);
                }
                bufSql.delete(0, bufSql.length());
                bufSql.append("UPDATE ").append(pub.yssGetTableName("Tb_Para_Flow"))
                    .append(" SET FState = ").append(YssCons.YSS_FLOW_POINTSTATE_UNFINISHED)
                    .append(" WHERE FFlowCode = ").append(dbl.sqlString(this.FFlowCode))
                    .append(" AND FExecuteID IN (").append(operSql.sqlCodes(sRelateCode)).append(")");
                dbl.executeSql(bufSql.toString());
            }
            //---------------------------------------------------------------//
            //========================End insert====================================
            conn.commit();
            conn.setAutoCommit(true);
            bTrans = false;
        } catch (Exception e) {
            throw new YssException("插入流程状态信息出现异常！", e);
        } finally {
            dbl.endTransFinal(conn, bTrans);
            dbl.closeResultSetFinal(rs);
        }
    }

    /**
     * 插入流程状态和日志信息
     * @param state Boolean
     * @throws YssException
     */
    public void ctlFlowStateAndLog(Boolean state) throws YssException {
    	//----add by yangheng  MS01573 QDV4赢时胜(上海开发部)2010年08月09日01_A 2010.09.03
        /*FlowBean flow = null;
        if (null != pub.getUserCode() && pub.getUserCode().length() > 0) {
            if (null != pub.getFlow() &&
                null != pub.getFlow().get(pub.getUserCode())) {
                flow = (FlowBean) pub.getFlow().get(pub.getUserCode());
                ctlFlowState(flow, state);
                insertFlowLogInServlet(flow, state);
            }
        }*/
    	//------------------------------------
    }

    /**
     * 在处理业务流程过程中插入状态
     * @param state int
     * @throws YssException
     */
    public void ctlFlowStateAndLogInFun(int state) throws YssException {
    	//---------add by yangheng  MS01573 QDV4赢时胜(上海开发部)2010年08月09日01_A 2010.09.03
        /*FlowBean flow = null;
        if (null != pub.getUserCode() && pub.getUserCode().length() > 0) {
            if (null != pub.getFlow() &&
                null != pub.getFlow().get(pub.getUserCode())) {
                flow = (FlowBean) pub.getFlow().get(pub.getUserCode());
                cltFlowInFun(flow, state);
            }
        }*/
    	//----------------------------------
    }

    /**
     * 插入流程执行状态
     * @param flow FlowBean
     * @param state int
     * @throws YssException
     */
    public void cltFlowInFun(FlowBean flow, int state) throws YssException {
        try {
            if (flow.getFFlowType() == 1) {
                flow.addFlowState(state);
            }
        } catch (YssException ex) {
            throw new YssException(ex);
        }
    }

    /**
     * 插入成功,失败的流程状态
     * @param flow FlowBean
     * @param state Boolean
     * @throws YssException
     */
    public void ctlFlowState(FlowBean flow, Boolean state) throws YssException {
        Hashtable flowtable = null;
        try {
            if (flow.getFFlowType() == 1) {
                if (null == state) {
                    return;
                }
                flow.addFlowState(state.booleanValue() ? 1 : 2);
                flow.setFState(state.booleanValue() ? 1 : 2);
            }
        } catch (YssException ex) {
            throw new YssException(ex);
        }
    }

    /**
     * 插入日志
     * @param flow FlowBean
     * @param state Boolean
     * @throws YssException
     */
    public void insertFlowLogInServlet(FlowBean flow, Boolean state) throws
        YssException {
        FlowLogBean flowLog = null;
//       if (null == state){
//          state = new Boolean(false);
//       }
        if (null == state) {
            return;
        }
        flowLog = new FlowLogBean();
        flowLog.setYssPub(pub);
        flowLog.setFlowCode(flow.getFFlowCode());
        flowLog.setFlowPointCode(flow.getFFlowPointID());
        flowLog.setOperUser(pub.getUserCode());
        flowLog.setOperContent(flow.getFFlowName());
        flowLog.setOperResult(state.booleanValue() ? "操作成功" : "操作失败");
        flowLog.addSetting();
    }

//----------------------------------------------------------------------------------------------
    //下面是FlowBean类的属性的get ,set方法
    public String getFDependence() {
        return FDependence;
    }

    //流程代码的get 方法
    public String getFFlowCode() {
        return FFlowCode;
    }

    //依赖项的set方法
    public void setFDependence(String FDependence) {
        this.FDependence = FDependence;
    }

    //流程代码的set 方法
    public void setFFlowCode(String FFlowCode) {
        this.FFlowCode = FFlowCode;
    }

    //流程名称
    public String getFFlowName() {
        return FFlowName;
    }

    //流程名称
    public void setFFlowName(String FFlowName) {
        this.FFlowName = FFlowName;
    }

    //流程节点序号
    public String getFFlowPointID() {
        return FFlowPointID;
    }

    //流程节点序号
    public void setFFlowPointID(String FFlowPointID) {
        this.FFlowPointID = FFlowPointID;
    }

    //流程节点名称
    public String getFFlowPointName() {
        return FFlowPointName;
    }

    //流程节点名称
    public void setFFlowPointName(String FFlowPointName) {
        this.FFlowPointName = FFlowPointName;
    }

    //流程类型
    public int getFFlowType() {
        return FFlowType;
    }

    //流程类型
    public void setFFlowType(int FFlowType) {
        this.FFlowType = FFlowType;
    }

    //用来保存流程筛选对象
    public FlowBean getFilterType() {
        return filterType;
    }

    public String getSFIsMust() {
        return SFIsMust;
    }

    public String getFPortCodes() {
        return FPortCodes;
    }

    public String getFFunCode() {
        return FFunCode;
    }

    public int getFState() {
        return FState;
    }

    public String getFMenuName() {
        return FMenuName;
    }

    //关联项
    public String getFRelate() {
        return FRelate;
    }

    //组合项
    public String getFPorts() {
        return FPorts;
    }

//    //记录状态
//    public int getFNotState() {
//        return FNotState;
//    }
    //流程节点菜单条
    public String getFMenuCode() {
        return FMenuCode;
    }

    //流程节点必须执行
    public int getFIsMust() {
        return FIsMust;
    }

    //用来保存流程筛选对象
    public void setFilterType(FlowBean filterType) {
        this.filterType = filterType;
    }

    public void setSFIsMust(String SFIsMust) {
        this.SFIsMust = SFIsMust;
    }

    public void setFPortCodes(String FPortCodes) {
        this.FPortCodes = FPortCodes;
    }

    public void setFFunCode(String FFunCode) {
        this.FFunCode = FFunCode;
    }

    public void setFState(int FState) {
        this.FState = FState;
    }

    public void setFMenuName(String FMenuName) {
        this.FMenuName = FMenuName;
    }

    //关联项
    public void setFRelate(String FRelate) {
        this.FRelate = FRelate;
    }

    //组合项
    public void setFPorts(String FPorts) {
        this.FPorts = FPorts;
    }

//    //记录状态
//    public void setFNotState(int FNotState) {
//        this.FNotState = FNotState;
//    }
    //流程节点菜单条
    public void setFMenuCode(String FMenuCode) {
        this.FMenuCode = FMenuCode;
    }

    //流程节点必须执行
    public void setFIsMust(int FIsMust) {
        this.FIsMust = FIsMust;
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
  //MS01272  add by zhangfa  2010.07.08      QDV4招商基金2010年6月8日01_A  
	public String getDaoGroup() {
		return daoGroup;
	}

	public void setDaoGroup(String daoGroup) {
		this.daoGroup = daoGroup;
	}
//----------------------------------------------------------------------
}
