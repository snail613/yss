package com.yss.main.etfoperation.etfaccbook.timeandaverage; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

import com.yss.dsub.DbBase;
import com.yss.dsub.YssPub;
import com.yss.pojo.sys.YssStatus;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.vsub.YssDbFun;
import com.yss.vsub.YssDbOperSql;
import com.yss.vsub.YssOperFun;



/**临时台账表Bean
 * @author shashijie ,2011-11-28 下午07:43:09 : STORY 1789
 */
public class TempBook {
	private String FNum = "";//申请编号	VARCHAR(15)
	private Date FBuyDate = null;//申购日期	Date
	private String FBs = "";//交易类型	VARCHAR(1)
	private String FPortCode = "";//组合代码	VARCHAR(20)
	private String FSecurityCode = "";//证券代码	VARCHAR(20)
	private String FStockHolderCode = "";//股东代码	VARCHAR(20)
	private String FBrokerCode = "";//参与券商	VARCHAR(20)
	private String FSeatCode = "";//席位号	VARCHAR(20)
	private double FMakeUpAmount = 0;//补票数量	NUMBER(18,0)
	private double FUnitCost = 0;//单位成本	NUMBER(30,15)
	private double FReplaceCash = 0;//替代金额	NUMBER(18,4)
	private double FCanReplaceCash = 0;//可退替代款	NUMBER(18,4)
	private double FRemaindAmount = 0;//剩余数量	NUMBER(18,0)
	private double FSumReturn = 0;//应退合计	NUMBER(18,4)
	private Date FRefundDate = null;//退款日期	Date
	private double FExchangeRate = 0;//申述数据对应的汇率	NUMBER(18,15)
	private String FOrderCode = "";//排序编号	VARCHAR(200)
	private String FGradeType1 = "";//分级类型1	VARCHAR(40)
	private String FGradeType2 = "";//分级类型2	VARCHAR(40)
	private String FGradeType3 = "";//分级类型3	VARCHAR(40)
	
	
	private Date FMakeUpDate1 = null;//第一次补票的日期
	private double FMakeUpAmount1 = 0;//第一次补票的数量
	private double FMakeUpUnitCost1 = 0;//第一次补票的单位成本
	private double FOMakeUpCost1 = 0;//第一次补票的总成本（原币）
	private double FHMakeUpCost1 = 0;//第一次补票的总成本（本币）
	private double FMakeUpRepCash1 = 0;//第一次补票的应付替代款
	private double FCanMkUpRepCash1 = 0;//第一次补票的可退替代款
	private double FExRate1 = 0;//第一次补票汇率
	private double FTradeUnitCost1 = 0;//第一次补票的成交单价
	private double FFeeUnitCost1 = 0;//第一次补票的费用单价
	
	protected YssPub pub = null; //全局变量
    protected DbBase dbl = null; //数据连接已经处理
    protected YssDbFun dbFun = null;
    protected YssDbOperSql operSql = null;
    protected YssOperFun operFun = null;
    protected YssStatus runStatus = null;
	
	public void setYssPub(YssPub ysspub) {
        pub = ysspub;
        dbl = ysspub.getDbLink();
        dbFun = new YssDbFun(ysspub);
        operSql = new YssDbOperSql(ysspub);
        operFun = new YssOperFun(ysspub);
    }
	
	
	public String getFNum() {
		return FNum;
	}
	public void setFNum(String fNum) {
		FNum = fNum;
	}
	public String getFBs() {
		return FBs;
	}
	public void setFBs(String fBs) {
		FBs = fBs;
	}
	public String getFPortCode() {
		return FPortCode;
	}
	public void setFPortCode(String fPortCode) {
		FPortCode = fPortCode;
	}
	public String getFSecurityCode() {
		return FSecurityCode;
	}
	public void setFSecurityCode(String fSecurityCode) {
		FSecurityCode = fSecurityCode;
	}
	public String getFStockHolderCode() {
		return FStockHolderCode;
	}
	public void setFStockHolderCode(String fStockHolderCode) {
		FStockHolderCode = fStockHolderCode;
	}
	public String getFBrokerCode() {
		return FBrokerCode;
	}
	public void setFBrokerCode(String fBrokerCode) {
		FBrokerCode = fBrokerCode;
	}
	public String getFSeatCode() {
		return FSeatCode;
	}
	public void setFSeatCode(String fSeatCode) {
		FSeatCode = fSeatCode;
	}
	public double getFMakeUpAmount() {
		return FMakeUpAmount;
	}
	public void setFMakeUpAmount(double fMakeUpAmount) {
		FMakeUpAmount = fMakeUpAmount;
	}
	public double getFUnitCost() {
		return FUnitCost;
	}
	public void setFUnitCost(double fUnitCost) {
		FUnitCost = fUnitCost;
	}
	public double getFReplaceCash() {
		return FReplaceCash;
	}
	public void setFReplaceCash(double fReplaceCash) {
		FReplaceCash = fReplaceCash;
	}
	public double getFCanReplaceCash() {
		return FCanReplaceCash;
	}
	public void setFCanReplaceCash(double fCanReplaceCash) {
		FCanReplaceCash = fCanReplaceCash;
	}
	public double getFRemaindAmount() {
		return FRemaindAmount;
	}
	public void setFRemaindAmount(double fRemaindAmount) {
		FRemaindAmount = fRemaindAmount;
	}
	public double getFSumReturn() {
		return FSumReturn;
	}
	public void setFSumReturn(double fSumReturn) {
		FSumReturn = fSumReturn;
	}
	public double getFExchangeRate() {
		return FExchangeRate;
	}
	public void setFExchangeRate(double fExchangeRate) {
		FExchangeRate = fExchangeRate;
	}
	public String getFOrderCode() {
		return FOrderCode;
	}
	public void setFOrderCode(String fOrderCode) {
		FOrderCode = fOrderCode;
	}
	public String getFGradeType1() {
		return FGradeType1;
	}
	public void setFGradeType1(String fGradeType1) {
		FGradeType1 = fGradeType1;
	}
	public String getFGradeType2() {
		return FGradeType2;
	}
	public void setFGradeType2(String fGradeType2) {
		FGradeType2 = fGradeType2;
	}
	public String getFGradeType3() {
		return FGradeType3;
	}
	public void setFGradeType3(String fGradeType3) {
		FGradeType3 = fGradeType3;
	}
	public Date getFBuyDate() {
		return FBuyDate;
	}
	public void setFBuyDate(Date fBuyDate) {
		FBuyDate = fBuyDate;
	}
	public Date getFRefundDate() {
		return FRefundDate;
	}
	public void setFRefundDate(Date fRefundDate) {
		FRefundDate = fRefundDate;
	}
	public Date getFMakeUpDate1() {
		return FMakeUpDate1;
	}
	public void setFMakeUpDate1(Date fMakeUpDate1) {
		FMakeUpDate1 = fMakeUpDate1;
	}
	public double getFMakeUpAmount1() {
		return FMakeUpAmount1;
	}
	public void setFMakeUpAmount1(double fMakeUpAmount1) {
		FMakeUpAmount1 = fMakeUpAmount1;
	}
	public double getFMakeUpUnitCost1() {
		return FMakeUpUnitCost1;
	}
	public void setFMakeUpUnitCost1(double fMakeUpUnitCost1) {
		FMakeUpUnitCost1 = fMakeUpUnitCost1;
	}
	public double getFOMakeUpCost1() {
		return FOMakeUpCost1;
	}
	public void setFOMakeUpCost1(double fOMakeUpCost1) {
		FOMakeUpCost1 = fOMakeUpCost1;
	}
	public double getFHMakeUpCost1() {
		return FHMakeUpCost1;
	}
	public void setFHMakeUpCost1(double fHMakeUpCost1) {
		FHMakeUpCost1 = fHMakeUpCost1;
	}
	public double getFMakeUpRepCash1() {
		return FMakeUpRepCash1;
	}
	public void setFMakeUpRepCash1(double fMakeUpRepCash1) {
		FMakeUpRepCash1 = fMakeUpRepCash1;
	}
	public double getFCanMkUpRepCash1() {
		return FCanMkUpRepCash1;
	}
	public void setFCanMkUpRepCash1(double fCanMkUpRepCash1) {
		FCanMkUpRepCash1 = fCanMkUpRepCash1;
	}
	public double getFExRate1() {
		return FExRate1;
	}
	public void setFExRate1(double fExRate1) {
		FExRate1 = fExRate1;
	}
	public double getFTradeUnitCost1() {
		return FTradeUnitCost1;
	}
	public void setFTradeUnitCost1(double fTradeUnitCost1) {
		FTradeUnitCost1 = fTradeUnitCost1;
	}
	public double getFFeeUnitCost1() {
		return FFeeUnitCost1;
	}
	public void setFFeeUnitCost1(double fFeeUnitCost1) {
		FFeeUnitCost1 = fFeeUnitCost1;
	}


	/**添加临时台长表
	 * @param dDate 操作日
	 * @param portCodes 组合
	 * @param tempBookList 集合
	 * @param deleteSqlWhere 删除条件语句
	 * @author shashijie ,2011-11-29 , STORY 1789
	 * @modified 
	 */
	public void Insert(Date dDate, String portCodes, List tempBookList,
			String deleteSqlWhere) throws YssException {
		//先删除旧数据
		DeleteFromTempBooklist(dDate,deleteSqlWhere);
		//添加记录
		PreparedStatement ps = null;//预处理
		Connection conn =null;//数据库连接
		boolean bTrans = true;//事物控制标识
		TempBook book = null;
		String sql = getInserSql();//32
		final int batchSize = 1000;
		
		try {
			conn = dbl.loadConnection();//打开数据库连接
			conn.setAutoCommit(false);//设置事物手动提交
			
			ps = dbl.openPreparedStatement(sql);
			for (int i = 0; i < tempBookList.size(); i++) {
				book = (TempBook) tempBookList.get(i);
				//设置对象预处理
				setTempBook(book,ps);		
				if(i % batchSize == 0) {//每1000条执行一次插入操作，提高性能
					ps.executeBatch();
				}				
			}
			ps.executeBatch();
			conn.commit();//提交事物
			conn.setAutoCommit(true);//设置为自动提交事物
			bTrans = false;
			
		}catch (Exception e) {
			throw new YssException("添加临时台账数据出错！",e);
		}finally{
			dbl.endTransFinal(conn,bTrans);
			dbl.closeStatementFinal(ps);
			//dbl.closeResultSetFinal(rs);
		}
	}

	/**设置对象预处理
	 * @param book
	 * @param ps
	 * @author shashijie ,2011-11-29 , STORY 1789
	 * @modified 
	 */
	private void setTempBook(TempBook book, PreparedStatement ps) throws Exception {
		if (book==null) {
			return;
		}
		ps.setString(1, book.getFNum());//申请编号
		ps.setDate(2, YssFun.toSqlDate(book.getFBuyDate()));//申购日期
		ps.setString(3, book.getFBs());//交易类型
		ps.setString(4, book.getFPortCode());//组合代码	VARCHAR(20)
		ps.setString(5, book.getFSecurityCode());//证券代码	VARCHAR(20)
		ps.setString(6, book.getFStockHolderCode());//股东代码	VARCHAR(20)
		ps.setString(7, book.getFBrokerCode());//参与券商	VARCHAR(20)
		ps.setString(8, book.getFSeatCode());//席位号	VARCHAR(20)
		ps.setDouble(9, book.getFMakeUpAmount());//补票数量	NUMBER(18,0)
		ps.setDouble(10, book.getFUnitCost());//单位成本	NUMBER(30,15)
		ps.setDouble(11, book.getFReplaceCash());//替代金额	NUMBER(18,4)
		ps.setDouble(12, book.getFCanReplaceCash());//可退替代款	NUMBER(18,4)
		ps.setDouble(13, book.getFRemaindAmount());//剩余数量	NUMBER(18,0)
		ps.setDouble(14, book.getFSumReturn());//应退合计	NUMBER(18,4)
		ps.setDate(15, YssFun.toSqlDate(book.getFRefundDate()));//退款日期	Date
		ps.setDouble(16, book.getFExchangeRate());//申述数据对应的汇率	NUMBER(18,15)
		ps.setString(17, book.getFOrderCode());//排序编号	VARCHAR(200)
		ps.setString(18, book.getFGradeType1());//分级类型1	VARCHAR(40)
		ps.setString(19, book.getFGradeType2());//分级类型2	VARCHAR(40)
		ps.setString(20, book.getFGradeType3());//分级类型3	VARCHAR(40)
		ps.setString(21, pub.getUserCode());//创建人	VARCHAR(20)
		ps.setString(22, YssFun.formatDate(new Date()));//创建时间	VARCHAR(20)
		ps.setDate(23, YssFun.toSqlDate(book.getFMakeUpDate1()));//第一次补票的日期	Date
		ps.setDouble(24, book.getFMakeUpAmount1());//第一次补票的数量	Number(18,0)
		ps.setDouble(25, book.getFMakeUpUnitCost1());//第一次补票的单位成本	Number(30,15)
		ps.setDouble(26, book.getFOMakeUpCost1());//第一次补票的总成本（原币）	Number(18,4)
		ps.setDouble(27, book.getFHMakeUpCost1());//第一次补票的总成本（本币）	Number(18,4)
		ps.setDouble(28, book.getFMakeUpRepCash1());//第一次补票的应付替代款	Number(18,4)
		ps.setDouble(29, book.getFCanMkUpRepCash1());//第一次补票的可退替代款	Number(18,4)
		ps.setDouble(30, book.getFExRate1());//第一次补票汇率	Number(18,15)
		ps.setDouble(31, book.getFTradeUnitCost1());//第一次补票的成交单价	Number(30,15)
		ps.setDouble(32, book.getFFeeUnitCost1());//第一次补票的费用单价	Number(30,15)
		ps.addBatch();
	}


	/**获取添加SQL
	 * @return
	 * @author shashijie ,2011-11-29 , STORY 1789
	 * @modified 
	 */
	private String getInserSql() {
		String sqlString = "Insert into "+pub.yssGetTableName("Tb_ETF_TempStandingBook")+" ( " +
		" FNum ,"+
		" FBuyDate ,"+
		" FBs ,"+
		" FPortCode ,"+
		" FSecurityCode ,"+
		" FStockHolderCode ,"+
		" FBrokerCode ,"+
		" FSeatCode ,"+
		" FMakeUpAmount ,"+
		" FUnitCost ,"+
		" FReplaceCash ,"+
		" FCanReplaceCash ,"+
		" FRemaindAmount ,"+
		" FSumReturn ,"+
		" FRefundDate ,"+
		" FExchangeRate ,"+
		" FOrderCode ,"+
		" FGradeType1 ,"+
		" FGradeType2 ,"+
		" FGradeType3 ,"+
		" FCreator ,"+
		" FCreateTime ,"+
		" FMakeUpDate1 ,"+
		" FMakeUpAmount1 ,"+
		" FMakeUpUnitCost1 ,"+
		" FOMakeUpCost1 ,"+
		" FHMakeUpCost1 ,"+
		" FMakeUpRepCash1 ,"+
		" FCanMkUpRepCash1 ,"+
		" FExRate1 ,"+
		" FTradeUnitCost1 ,"+
		" FFeeUnitCost1 "+
		" )values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		return sqlString;
	}


	/**删除数据
	 * @param dDate
	 * @author shashijie ,2011-11-29 , STORY 1789
	 * @modified 
	 */
	private void DeleteFromTempBooklist(Date dDate,String deleteSqlWhere) throws YssException {
		String sql = " delete from "+pub.yssGetTableName("Tb_ETF_TempStandingBook")+
			" where FBuyDate = "+dbl.sqlDate(dDate)+" "+deleteSqlWhere;
		try {
			dbl.executeSql(sql);
		} catch (Exception e) {
			throw new YssException("先删除临时台长表数据出错！",e);
		}
	}

	
	
	

}

