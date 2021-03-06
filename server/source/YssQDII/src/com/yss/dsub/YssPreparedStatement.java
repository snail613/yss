package com.yss.dsub;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.yss.util.YssException;

/********************************************************************
 * <p>Title: </p>
 * <p>Description: 对PrePareStatement 进行重新封装。
 *  1.添加日志模块，便于调试
 *  2.对历史SQL语句进行解析处理，避免经常执行的语句硬编译
 *  </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Ysstech</p>
 * @author benson
 * @version 1.0
 */
public class YssPreparedStatement implements PreparedStatement {

	private PreparedStatement pst = null;
	private DbBase dbl = null;
	private Logger log =null;
	private String sql = "";
	private StringBuffer buff = null;
	private String[] sqlArrs = null;
	
	public void setDbBase(DbBase dbl){
		this.dbl = dbl;
	}
	
	public YssPreparedStatement(DbBase dbl,String sql) throws SQLException, YssException{
		if(sql.indexOf("?")>0){
			sqlArrs = sql.split("[?]");//split方法的参数，既是代表分隔字符串，也可以代表正则表达式字符串，当分割字符串中有.或者+或者|等的时候，会与正则表达式冲突。
		}
		this.sql = sql;
		this.dbl = dbl;
		buff = new StringBuffer();
		pst = dbl.loadConnection().prepareStatement(sql);
		log = Logger.getLogger("stdout");
	}
	
	public YssPreparedStatement(DbBase dbl,String sql,boolean bParserFlag)throws SQLException, YssException{
		
		
			this.sql = sql;
			this.dbl = dbl;
			buff = new StringBuffer();
			
			SqlParser sqlparser= new SqlParser();
			sqlparser.setDbType(this.dbl.getDBType());
	    	pst = sqlparser.getPstmRs(this.sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY,dbl.loadConnection());
			log = Logger.getLogger("stdout");
		
		
	}
	
	
	public YssPreparedStatement(DbBase dbl,String sql,int resultSetType,int resultSetConcurrency) throws SQLException, YssException{
		if(sql.indexOf("?")>0){
			/**shashijie 2012-7-2 STORY 2475 */
			sqlArrs = sql.split("\\?");
			/**end*/
		}
		this.sql = sql;
		this.dbl = dbl;
		buff = new StringBuffer();
		pst =  dbl.loadConnection().prepareStatement(sql, resultSetType, resultSetConcurrency);
		log = Logger.getLogger("stdout");
	}
	
	
	public ResultSetMetaData getMetaData() throws SQLException {
		
		return pst.getMetaData();
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getParameterMetaData();
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~ Setter Methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public void setArray(int i, Array x) throws SQLException {
		
		pst.setArray(i, x);
		
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		pst.setAsciiStream(parameterIndex, x, length);
		
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		pst.setBigDecimal(parameterIndex, x);
		
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		
		pst.setBinaryStream(parameterIndex, x, length);
		
	}

	public void setBlob(int i, Blob x) throws SQLException {
		
		pst.setBlob(i, x);
		
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		buildSql(parameterIndex,x+"");
		pst.setBoolean(parameterIndex, x);
		
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		
		pst.setByte(parameterIndex, x);
		
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		
		pst.setBytes(parameterIndex, x);
		
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		
		pst.setCharacterStream(parameterIndex, reader, length);
		
	}

	public void setClob(int i, Clob x) throws SQLException {
		buildSql(i,x.toString());
		pst.setClob(i, x);
		
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		
		buildSql(parameterIndex,dbl.sqlDate(x));
		pst.setDate(parameterIndex, x);
		
	}

	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		pst.setDate(parameterIndex, x, cal);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		// TODO Auto-generated method stub
		buildSql(parameterIndex,x+"");
		pst.setDouble(parameterIndex, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		// TODO Auto-generated method stub
		buildSql(parameterIndex,x+"");
		pst.setFloat(parameterIndex, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		// TODO Auto-generated method stub
		buildSql(parameterIndex,x+"");
		pst.setInt(parameterIndex, x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		// TODO Auto-generated method stub
		buildSql(parameterIndex,x+"");
		pst.setLong(parameterIndex, x);
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		// TODO Auto-generated method stub
		pst.setNull(parameterIndex, sqlType);
	}

	public void setNull(int paramIndex, int sqlType, String typeName)
			throws SQLException {
		// TODO Auto-generated method stub
		pst.setNull(paramIndex, sqlType, typeName);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		// TODO Auto-generated method stub
		pst.setObject(parameterIndex, x);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		// TODO Auto-generated method stub
		pst.setObject(parameterIndex, x, targetSqlType);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scale) throws SQLException {
		// TODO Auto-generated method stub
		pst.setObject(parameterIndex, x, targetSqlType, scale);
	}

	public void setRef(int i, Ref x) throws SQLException {
		// TODO Auto-generated method stub
		pst.setRef(i, x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		// TODO Auto-generated method stub
		buildSql(parameterIndex,x+"");
		pst.setShort(parameterIndex, x);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		// TODO Auto-generated method stub
		buildSql(parameterIndex,x);
		pst.setString(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		// TODO Auto-generated method stub
		buildSql(parameterIndex,x.toString());
		pst.setTime(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		pst.setTime(parameterIndex, x, cal);
	}

	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		// TODO Auto-generated method stub
		pst.setTimestamp(parameterIndex, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		// TODO Auto-generated method stub
		pst.setTimestamp(parameterIndex, x, cal);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		// TODO Auto-generated method stub
		pst.setURL(parameterIndex, x);
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		// TODO Auto-generated method stub
		pst.setUnicodeStream(parameterIndex, x, length);
	}
   //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Setter Methods end ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	public ResultSet executeQuery() throws SQLException {
		// TODO Auto-generated method stub
		ResultSet rs = null;
		if (sqlArrs ==null){
			
				//log.debug(sql);
				log.info(sql); //modified by ysh
				int rsType = pst.getResultSetType();
				int rsConcur = pst.getResultSetConcurrency();
				rs = dealQuery(rsType,rsConcur);
			
		}else{
			printLog();
			 rs = pst.executeQuery();
		}
		return rs;
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		// TODO Auto-generated method stub
		ResultSet rs = null;
		if (sqlArrs ==null){
			try {
				//log.debug(sql);
				log.info(sql);
				int rsType = pst.getResultSetType();
				int rsConcur = pst.getResultSetConcurrency();
				rs = dealQuery(rsType,rsConcur);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}else{
			printLog();
			 rs = pst.executeQuery(sql);
		}
		return rs;
		
	}
	
	

	public boolean execute() throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			printLog();
			if(buff.length()>0){
				buff.setLength(0);
			}
			return pst.execute();
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" +this.sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public void addBatch() throws SQLException {
		// TODO Auto-generated method stub
		this.printLog();
		if(buff.length()>0){
			buff.setLength(0);
		}
		sqlArrs = sql.split("[?]");
		pst.addBatch();
	}
	
	public int executeUpdate() throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(this.sql);
			return pst.executeUpdate();
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" +this.sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public boolean execute(String sql) throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.execute(sql);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.execute(sql, autoGeneratedKeys);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.execute(sql, columnIndexes);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.execute(sql, columnNames);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(this.sql);
			return pst.executeBatch();	
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public int executeUpdate(String sql) throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.executeUpdate(sql);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.executeUpdate(sql, autoGeneratedKeys);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.executeUpdate(sql, columnIndexes);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	}

	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		// TODO Auto-generated method stub
		try
		{
			log.info(sql);
			return pst.executeUpdate(sql, columnNames);
		}
		catch(Exception ye)
		{
			String sValues = "";
			for(int i = 0; i < sqlArrs.length; i++)
			{
				sValues += sqlArrs[i];
			}
			
			log.error(ye.getMessage());
			log.error("ERROR SQL:" + sql);
			log.error("Values:" + sValues);
			throw new SQLException(ye.getMessage());
		}
	} 
	
	public void addBatch(String sql) throws SQLException {
		// TODO Auto-generated method stub
		pst.addBatch();
	}

	
	public void clearParameters() throws SQLException {
		pst.clearParameters();
	}
	
	public void cancel() throws SQLException {
		// TODO Auto-generated method stub
		pst.cancel();
	}

	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub
		pst.clearBatch();
	}

	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		pst.clearWarnings();
	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub
		pst.close();
	}

	
	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getConnection();
	}

	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getFetchDirection();
	}

	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getFetchSize();
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getGeneratedKeys();
	}

	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getMaxFieldSize();
	}

	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getMaxRows();
	}

	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getMoreResults();
	}

	public boolean getMoreResults(int current) throws SQLException {
		// TODO Auto-generated method stub
		return pst.getMoreResults(current);
	}

	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getQueryTimeout();
	}

	public ResultSet getResultSet() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getResultSet();
	}

	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getResultSetConcurrency();
	}

	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getResultSetHoldability();
	}

	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getResultSetType();
	}

	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getUpdateCount();
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return pst.getWarnings();
	}

	public void setCursorName(String name) throws SQLException {
		// TODO Auto-generated method stub
		pst.setCursorName(name);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		// TODO Auto-generated method stub
		pst.setEscapeProcessing(enable);
	}

	public void setFetchDirection(int direction) throws SQLException {
		
		pst.setFetchDirection(direction);
	}

	public void setFetchSize(int rows) throws SQLException {
		
		pst.setFetchSize(rows);
	}

	public void setMaxFieldSize(int max) throws SQLException {
		
		pst.setMaxFieldSize(max);
	}

	public void setMaxRows(int max) throws SQLException {
		
		pst.setMaxRows(max);
		
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		
		pst.setQueryTimeout(seconds);
		
	}

	/********************************************************
	 * 对原始SQL 语句进行解析处理。
	 * 
	 */
	private ResultSet dealQuery(int rsType,int rsConcur) throws SQLException{
		ResultSet rs = null;
		PreparedStatement pStmt =  null;
		SqlParser sqlparser= new SqlParser();
		sqlparser.setDbType(this.dbl.getDBType());
    	try {
			pStmt = sqlparser.getPstmRs(this.sql,rsType,rsConcur,dbl.loadConnection());
			rs = pStmt.executeQuery();
		} catch (YssException e) {
			log.error("访问数据库出错:" + this.sql);//add by yeshenghong
			throw new SQLException(e.getMessage());
			
		}
       rs = pStmt.executeQuery();
		return rs;
	}
	
	
	private void buildSql(int parameterIndex,String value){
		try{
			if(sqlArrs == null){
				return;
			}
			if(parameterIndex-1>sqlArrs.length){
				throw new YssException("");
			}
			sqlArrs[parameterIndex-1]= buff.append(sqlArrs[parameterIndex-1]).append(" ").append(dbl.sqlString(value.toString())).toString();
			buff.setLength(0);
		}catch(Exception e){
			e.getMessage();
		}	
	}
	
	private void printLog(){
		if(sqlArrs==null)return;
		for(int i=0;i<sqlArrs.length;i++){
			buff.append(sqlArrs[i]).append(" ");
		}
		log.info(buff.toString());
	}

	@Override
	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}





	
	
}
