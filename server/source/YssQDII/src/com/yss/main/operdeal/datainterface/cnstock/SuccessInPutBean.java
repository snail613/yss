package com.yss.main.operdeal.datainterface.cnstock;

import com.yss.main.operdeal.datainterface.pretfun.DataBase;
import com.yss.util.YssException;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import com.yss.util.YssFun;
import java.sql.ResultSet;
import java.sql.Connection;

/**
 * QDII国内：QDV4.1赢时胜（上海）2009年4月20日04_A MS00004 国内接口处理
 * add by songjie
 * 2009-05-15
 * 用于处理国内接口数据从临时表到债券信息表的数据转换
 */
public class SuccessInPutBean extends DataBase {
    public static ArrayList alInterfaceCode = new ArrayList();//用于储存已导入的自定义接口代码
    /**
     * 构造函数
     */
    public SuccessInPutBean() {

    }

    public ArrayList getAlInterfaceCode() {
        return alInterfaceCode;
    }

    /**
     *用于处理国内接口的数据
     * 若自定义接口对应的数据已经导入到临时表，
     * 那么就将自定义接口代码储存到列表中
     */
    public void inertData() throws YssException {
        if (this.cusCfgCode.equals("SH_zqbd_imp")) { //上海证券变动库
            insertIntoSHZQBD();//将临时表的数据插入到SHZQBD

            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SH_tmpgh_IMP")) { //上海过户库
            insertIntoSHGH("PT");//将临时表的数据插入到SHGH

            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }
        
        if(this.cusCfgCode.equals("SH_tmpdgh_IMP")){//上海B股大宗交易过户库      panjunfang add 20100426
        	this.insertIntoSHGH("DZ");
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SZ_sjsfx_IMP")) { //深圳发行库
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SZ_sjsgf_IMP")) { //深圳股份库
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }

        if (this.cusCfgCode.equals("SZ_sjshb_IMP")) { //深圳回报库
        	//add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
        	fromTmpToSZHB("PT");
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }
        
        if (this.cusCfgCode.equals("SZ_sjsbthb_IMP")) { //深圳大宗交易回报库
        	//add by songjie 2012.11.12 STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
        	fromTmpToSZHB("DZ");
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }
        
        //---add by songjie 2012.05.09 STORY #2599 QDV4赢时胜(上海开发部)2012年05月07日01_A start---//
        if (this.cusCfgCode.equals("JSMX_IMP")) { //上海结算明细库
        	insertIntoShJsMx();
            if (!alInterfaceCode.contains(this.cusCfgCode)) {
                alInterfaceCode.add(this.cusCfgCode);
            }
        }
        //---add by songjie 2012.05.09 STORY #2599 QDV4赢时胜(上海开发部)2012年05月07日01_A end---//
    }
    
    /**
     * add by songjie 2012.11.13
     * STORY #3230 需求深圳-[南方]QDIIV4[低]20121102001
     * 将深交所大宗交易数据 或 普通交易数据插入到SZHB表中
     * JYFS 交易方式：DZ大宗交易 ，PT 普通交易
     * @throws YssException
     */
    private void fromTmpToSZHB(String jyfs) throws YssException {
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        String strSql = null; //储存sql语句
        PreparedStatement pstmt = null;
        try {
            con.setAutoCommit(false); //开启事务
            bTrans = true;
            
            //根据交易方式删除数据
            strSql = " delete from SZHB where JYFS = " + dbl.sqlString(jyfs);
            dbl.executeSql(strSql); //清空SZHB表的数据

            strSql = " insert into SZHB(FDate, HBCJHM, HBZQDM, HBHTXH, HBGDDM, " +
            	//添加 JYFS字段 用于区分 大宗交易 和 普通交易
                " HBCJSL, HBCJJG, HBCJRQ, HBYWLB, HBMARK, HBBYBZ, JYFS)values(?,?,?,?,?,?,?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql); //将临时表的数据插入到SZHB表中

            strSql = " select * from tmp_sjshb where HBCJRQ = " + dbl.sqlDate(this.sDate) + " and idDel = 'False' " ;
            
            //若果是大宗交易的数据，则只读入HBYWLB 为 1B、1S 的数据
            if(jyfs.equals("DZ")){
            	strSql += "and HBYWLB in('1B','1S')";
            }
            
            rs = dbl.openResultSet(strSql); //查询临时表中的所有数据
            while (rs.next()) {
                pstmt.setDate(1, YssFun.toSqlDate(this.sDate)); //系统读数日期
                pstmt.setString(2, rs.getString("HBCJHM"));
                pstmt.setString(3, rs.getString("HBZQDM"));
                pstmt.setString(4, rs.getString("HBHTXH"));
                pstmt.setString(5, rs.getString("HBGDDM"));
                pstmt.setDouble(6, rs.getDouble("HBCJSL"));
                pstmt.setDouble(7, rs.getDouble("HBCJJG"));
                pstmt.setDate(8, rs.getDate("HBCJRQ"));
                pstmt.setString(9, rs.getString("HBYWLB"));
                pstmt.setString(10, rs.getString("HBMARK"));
                pstmt.setString(11, rs.getString("HBBYBZ"));
                pstmt.setString(12, jyfs);//保存交易方式
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        } catch (Exception e) {
            throw new YssException("将深圳回报库临时表的数据插入到SZHB表出错！", e);
        } finally {
            dbl.endTransFinal(con, bTrans); //关闭连接
            dbl.closeResultSetFinal(rs); //关闭结果集
            dbl.closeStatementFinal(pstmt); //关闭pstmt
        }
    }
    
    /**
     * JYFS 交易方式：DZ大宗交易 ，PT 普通交易
     * 将tmpSH_gh表中的数据插入到SHGH表中
     * @throws YssException
     */
    private void insertIntoSHGH(String strJYFS)throws YssException{
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        ResultSet rs1 = null; //声明结果集
        String strSql = null; //储存sql语句
        PreparedStatement pstmt = null;
        String fDate = null;
        String gddm = "";//股东代码
        String gsdm = "";//席位代码
        try{
            con.setAutoCommit(false); //开启事务
            bTrans = true;

            strSql = " select distinct GDDM, GSDM from tmpSH_gh a where a.isdel = 'False'";//edit by xuxuming,20091110.只用获取没有删除标记的数据
            rs1 = dbl.openResultSet(strSql); //查询临时表中的所有数据

            //edit by songjie 2010.03.18 MS00917 QDV4赢时胜（测试）2010年03月18日03_B
            while(rs1.next()){
                gddm += rs1.getString("GDDM") + ",";
                gsdm += rs1.getString("GSDM") + ",";
            }
            //edit by songjie 2010.03.18 MS00917 QDV4赢时胜（测试）2010年03月18日03_B
           
            //add by songjie 2010.03.18 MS00917 QDV4赢时胜（测试）2010年03月18日03_B
            if(gddm != null && gddm.length() >= 2){
            	gddm = gddm.substring(0, gddm.length() - 1);
            }
            if(gsdm != null && gsdm.length() >= 2){
            	gsdm = gsdm.substring(0, gsdm.length() - 1);
            }
            //add by songjie 2010.03.18 MS00917 QDV4赢时胜（测试）2010年03月18日03_B

            strSql = " delete from SHGH where FDate = " + dbl.sqlDate(this.sDate) +
            //edit by songjie 2010.03.18 MS00917 QDV4赢时胜（测试）2010年03月18日03_B   
            " and GDDM in (" + operSql.sqlCodes(gddm) + ") and GSDM in (" + operSql.sqlCodes(gsdm) + ")" + " and JYFS = " + dbl.sqlString(strJYFS);//交易方式也作为删除条件 panjunfang add 20100426
            dbl.executeSql(strSql); //清空SHGH表的数据

            strSql = " insert into SHGH(FDate,GDDM,GDXM,CJBH,GSDM,CJSL,ZQDM,CJJG,CJJE,SQBH,BS,JYFS)values(?,?,?,?,?,?,?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql); //将临时表的数据插入到SHGH表中

//            strSql = " select * from tmpSH_gh ";//edit by xuxuming,20091110.临时表中有删除标记，此处应该只导入没有删除标记的数据
            strSql = " select * from tmpSH_gh a where a.isdel = 'False'";//add by xuxuming,20091110.False表明是没有删除标记的数据
            rs = dbl.openResultSet(strSql); //查询临时表中的所有数据
            while (rs.next()) {
                fDate = rs.getString("BCRQ"); //交易日期
                fDate = YssFun.left(fDate, 4) + "-" + YssFun.mid(fDate, 4, 2) + "-" + YssFun.right(fDate, 2);
                pstmt.setDate(1, YssFun.toSqlDate(fDate));
                pstmt.setString(2, rs.getString("GDDM")); //股东代码
                pstmt.setString(3, rs.getString("GDXM")); //股东姓名
                pstmt.setString(4, rs.getString("CJBH")); //成交编号
                pstmt.setString(5, rs.getString("GSDM")); //席位代码
                pstmt.setDouble(6, rs.getDouble("CJSL")); //成交数量
                pstmt.setString(7, rs.getString("ZQDM")); //证券代码
                pstmt.setDouble(8, rs.getDouble("CJJG")); //成交价格
                pstmt.setDouble(9, rs.getDouble("CJJE")); //成交金额
                pstmt.setString(10, rs.getString("SQBH")); //申请编号
                pstmt.setString(11, rs.getString("BS")); //买卖标志
                pstmt.setString(12, strJYFS); //买卖标志
                pstmt.addBatch();
            }
            pstmt.executeBatch();

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }
        catch(Exception e){
            throw new YssException("将临时表的数据插入到上海过户表出错！", e);
        }
        finally{
            dbl.endTransFinal(con, bTrans); //关闭连接
            dbl.closeResultSetFinal(rs, rs1); //关闭结果集
            dbl.closeStatementFinal(pstmt); //关闭pstmt
        }
    }

	/**
	 * add by songjie 2012.05.09 
	 * STORY #2599 QDV4赢时胜(上海开发部)2012年05月07日01_A
	 * 将临时表的数据处理到ShJsMx表中
	 * @throws YssException
	 */
	private void insertIntoShJsMx()throws YssException{
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        String strSql = null; //储存sql语句
        PreparedStatement pstmt = null;
        String fDate = null;
        String gddm = "";//股东代码
        String gsdm = "";//席位代码
        try{
            con.setAutoCommit(false); //开启事务
            bTrans = true;
            
            strSql = " select distinct ZQZH, XWH1 from tmp_shjsmx a where a.isdel = 'False'";//edit by xuxuming,20091110.只用获取没有删除标记的数据
            rs = dbl.openResultSet(strSql); //查询临时表中的所有数据

            while(rs.next()){
                gddm += rs.getString("ZQZH") + ",";
                gsdm += rs.getString("XWH1") + ",";
            }
            if(gddm != null && gddm.length() >= 2){
            	gddm = gddm.substring(0, gddm.length() - 1);
            }
            if(gsdm != null && gsdm.length() >= 2){
            	gsdm = gsdm.substring(0, gsdm.length() - 1);
            }

            dbl.closeResultSetFinal(rs);
            rs = null;
            
            strSql = " delete from SHJSMX where FDate = " + dbl.sqlDate(this.sDate) +
            " and GDDM in (" + operSql.sqlCodes(gddm) + ") and XWDM in (" + operSql.sqlCodes(gsdm) + ")";
            dbl.executeSql(strSql); //清空SHJSMX表的数据
            
            strSql = " insert into SHJSMX(ZQDM,GDDM,XWDM,ZQLB,FDATE,BS,CJBH,SQBH,CJSL,CJJG,GHF,YHS,ZGF,JSF)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
            pstmt = dbl.openPreparedStatement(strSql); //将临时表的数据插入到SHJSMX表中

            strSql = " select * from tmp_shjsmx where JYRQ = " + dbl.sqlString(YssFun.formatDate(this.sDate, "yyyyMMdd"));
            rs = dbl.openResultSet(strSql);
            if(!rs.next()){
            	throw new YssException("界面所选日期与接口文件日期不一致，请重新选择！");
            }
            
            dbl.closeResultSetFinal(rs);
            rs = null;
            
            strSql = " select * from tmp_shjsmx a where a.isdel = 'False' and SCDM = '01' and JLLX = '001' and JYFS = '003' " +
            " and YWLX = '011' and GHLX = '00Y' and JYRQ = " + dbl.sqlString(YssFun.formatDate(this.sDate, "yyyyMMdd"));
            rs = dbl.openResultSet(strSql); //查询临时表中的所有数据
            
            while (rs.next()) {
            	pstmt.setString(1, rs.getString("ZQDM1")); //证券代码
            	pstmt.setString(2, rs.getString("ZQZH")); //股东代码
            	pstmt.setString(3, rs.getString("XWH1")); //席位代码
            	pstmt.setString(4, rs.getString("ZQLB")); //证券类别
            	
                fDate = rs.getString("JYRQ"); //交易日期
                fDate = YssFun.left(fDate, 4) + "-" + YssFun.mid(fDate, 4, 2) + "-" + YssFun.right(fDate, 2);
                pstmt.setDate(5, YssFun.toSqlDate(fDate));
                
                pstmt.setString(6, rs.getString("MMBZ")); //买卖标志
                pstmt.setString(7, rs.getString("CJBH")); //成交编号
                pstmt.setString(8, rs.getString("SQBH")); //申请编号
                pstmt.setDouble(9, Double.parseDouble(rs.getString("SL"))); //成交数量
                pstmt.setDouble(10, Double.parseDouble(rs.getString("JG1"))); //成交价格
                pstmt.setDouble(11, Double.parseDouble(rs.getString("GHF"))); //过户费
                pstmt.setDouble(12, Double.parseDouble(rs.getString("YHS"))); //印花税
                pstmt.setDouble(13, Double.parseDouble(rs.getString("ZGF"))); //证管费
                pstmt.setDouble(14, Double.parseDouble(rs.getString("JSF"))); //经手费

                pstmt.addBatch();
            }
            pstmt.executeBatch();
            
            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }catch(Exception e){
        	throw new YssException("将临时表的数据插入到上海结算明细表出错！", e);
        }finally{
            dbl.endTransFinal(con, bTrans); //关闭连接
            dbl.closeResultSetFinal(rs); //关闭结果集
            dbl.closeStatementFinal(pstmt); //关闭pstmt
        }
	}
    
    /**
     * 将上海证券变动库对应的临时表的数据插入到SHZQBD
     * @throws YssException
     */
    private void insertIntoSHZQBD()throws YssException{
        String sqlStr = "";
        Connection con = dbl.loadConnection(); //新建连接
        boolean bTrans = false;
        ResultSet rs = null; //声明结果集
        String qsbh = null;
        try{
            sqlStr = " select distinct QSBH from TMP_ZQBD ";
            rs = dbl.openResultSet(sqlStr);

            while(rs.next()){
                qsbh = rs.getString("QSBH");
            }

            con.setAutoCommit(false); //开启事务
            bTrans = true;

            //1:删除掉之前的数据
            sqlStr="delete from SHZQBD where FDate = "+dbl.sqlDate(this.sDate) + " and QSBH = " + dbl.sqlString(qsbh);
            dbl.executeSql(sqlStr);
            //2:插入新的数据
            sqlStr = "insert into SHZQBD (FDate,SCDM,QSBH,ZQZH,XWH,ZQDM,ZQLB,LTLX,QYLB,GPNF,BDSL,BDLX,BDRQ,SL,BH,FBY) select " + dbl.sqlDate(sDate) +
                ",SCDM,QSBH,ZQZH,XWH,ZQDM,ZQLB,LTLX,QYLB,GPNF,BDSL,BDLX,BDRQ,SL,BH,BYON from TMP_ZQBD "+
                " where isDel ='False'";
            dbl.executeSql(sqlStr);

            con.commit(); //提交事务
            bTrans = false;
            con.setAutoCommit(true); //设置可以自动提交
        }catch(Exception ex){
            throw new YssException("将临时表数据添加到上海证券变动表出错!",ex);
        }finally{
            dbl.endTransFinal(con, bTrans); //关闭连接
            dbl.closeResultSetFinal(rs); //关闭结果集
        }
    }
}
