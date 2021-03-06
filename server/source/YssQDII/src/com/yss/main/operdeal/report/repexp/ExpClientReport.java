package com.yss.main.operdeal.report.repexp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.yss.base.BaseAPOperValue;
import com.yss.main.report.GuessValue;
import com.yss.util.YssException;
import com.yss.util.YssFun;
/**
 * 自动导报表的实现类
 * @author liyu
 *
 */
public class ExpClientReport extends BaseAPOperValue {
	private final String COL_SPLIT="\t";
	private final String ROW_SPLIT="\r\n";
	//private BaseAPOperValue baseAPOper=null;//基类
	private java.util.Date startDate=null;//开始日期
	private java.util.Date endDate=null;//截止日期
	private String portCode="";//组合代码
	private String DpCode="";//报表代码
	private String DpName="";//报表名称	
	HashMap hmFieldFormat=null;//字段格式
	public ExpClientReport() {
	}

	/**
	 * getOperDoubleValue
	 * 
	 * @return double
	 */
	public double getOperDoubleValue() throws YssException {
		return 0.0;
	}

	/**
	 * 获取报表的数据
	 * 
	 * @return String
	 */
	public String getOperStrValue() throws YssException {
		String sReportRes="";
		try{
			//sReportRes = buildReport1Str();
		}
		catch(Exception ex)
		{
			throw new YssException(ex.getMessage(),ex);
		}
		return sReportRes;
	}

	/**
	 * 初始化
	 * 
	 * @param bean
	 *            Object
	 */
	public void init(Object bean) throws YssException {
	}

	/**
	 * invokeOperMothed
	 * 
	 * @return Object
	 */
	public Object invokeOperMothed() throws YssException {
		return "";
	}

	/**
	 * getTypeValue
	 * 
	 * @param sType
	 *            String
	 * @return Object
	 */
	public Object getTypeValue(String sType) throws YssException {
		String sReportRes="";
		try {
			/**
			 * 根据不同报表的情况，需逐个将报表的列标题、正文数据加载到一起，若为汇总报表还要
			 */
			sReportRes = buildReportData(sType);
		} catch (Exception ex) {
			throw new YssException(ex.getMessage(),ex);
		}		
		return sReportRes;
	}
	
	/**
	 * 用于生成一个报表数据源数据的方法
	 * 此方法根据SQL语句的字段类型来拼数据，可用来拼数据源的临时表数据，也可以用来拼报表列标题
	 * @param sqlStr sql语句
	 * @return 数据源的数据
	 * @throws YssException
	 */
	private String buildReportDetailData(String sqlStr,String sField) throws YssException{
		ResultSet rs =null;
		HashMap hmField =null;
		StringBuffer buf =null;
		String[] arrField=null;
		String[] format=null;
		try{
			buf = new StringBuffer();			
			rs =dbl.openResultSet(sqlStr);
			hmField = dbFun.getFieldsType(rs);
			arrField = sField.split(",");
			while(rs.next()){
				//Iterator it = set.iterator();
				for(int i=0;i<arrField.length;i++){
					String fieldType= "";//这里只考虑字符型、日期型、与数值型数据
					String field =arrField[i].trim().toUpperCase();
					if(hmField.get(field)!=null){
						format = String.valueOf(hmFieldFormat.get(field)).split("\t");
						fieldType=String.valueOf(hmField.get(field));
						buf.append(format[0].replaceAll("null", ""));
						if(fieldType.indexOf("CHAR")>-1){
							buf.append(rs.getString(String.valueOf(field))==null?"":rs.getString(String.valueOf(field)));
						}else if(fieldType.indexOf("DATE")>-1){
							//buf.append(YssFun.formatDate(rs.getDate(String.valueOf(field)),"yyyy-MM-dd"));
							//edit by qiuxufeng 20101208 值为null返回空
							buf.append(rs.getDate(String.valueOf(field)) == null ? "" : YssFun.formatDate(rs.getDate(String.valueOf(field)),"yyyy-MM-dd"));
						}else{
							//buf.append(YssFun.formatNumber(rs.getDouble(String.valueOf(field)),buildFormStr(format)));
							//edit by qiuxufeng 20101208 值为null返回空
							buf.append(rs.getString(String.valueOf(field)) == null ? "" : YssFun.formatNumber(rs.getDouble(String.valueOf(field)),buildFormStr(format)));
						}
						if(format.length>1)
							buf.append(format[1]);
						buf.append(COL_SPLIT);
					}					
				}
				buf.append(ROW_SPLIT);
			}
			return buf.toString();
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}		
	}
	
	/**
	 * 根据报表数据源代码查询报表格式，取出报表格式的字段来
	 * @param dpDsCode
	 * @return
	 * @throws YssException
	 */
	private String buildDsField(String dpDsCode) throws YssException{
		String strSql="";
		ResultSet rs=null;
		String sFields="";
		try{
			strSql = "select fcontent,fformat from " +
            pub.yssGetTableName("TB_REP_CELL") + " where frelacode='" +
            dpDsCode + "' and frelatype='DSF' and frow=-1 order by fcol";
			rs = dbl.openResultSet(strSql);
			while(rs.next())
			{
				sFields+=(rs.getString("FContent")+",");
				hmFieldFormat.put(rs.getString("FContent").trim().toUpperCase(), rs.getString("FFormat")==null?"":rs.getString("FFormat").replaceAll("null", ""));
			}
			if(sFields.endsWith(",")){
				sFields = sFields.substring(0,sFields.length()-1);
			}
		}catch(Exception ex){
			throw new YssException(ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return sFields;
	}
	/**
	 * 根据报表代码查询出该报表的数据来
	 * @param reportCode
	 * @return
	 * @throws YssException
	 */
	public String buildReportData(String reportCode) throws YssException{
		ResultSet rs =null;
		String strSql="";
		String sResult ="";
		try{
			strSql = "select FRepType,FSubRepCodes from " + pub.yssGetTableName("Tb_Rep_Custom") +
            " where FCusRepCode = " + dbl.sqlString(reportCode);
			rs = dbl.openResultSet(strSql);
			while(rs.next()){
				if (rs.getString("FRepType").equalsIgnoreCase("0")) { //明细报表
                    sResult += buildAllDataSource(reportCode);
                 }
                 else if (rs.getString("FRepType").equalsIgnoreCase("1")) { //汇总报表
                    sResult += buildCusSumRep(rs.getString("FSubRepCodes"));
                 }
			}
			return sResult;
		}catch(Exception ex){
			throw new YssException(ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 处理多重汇总报表的问题
	 * @param sSubRepCodes
	 * @return
	 * @throws YssException
	 */
	private String buildCusSumRep(String sSubRepCodes) throws YssException {
		String[] sSubRepAry = null;
		String strSql = "";
		ResultSet rs = null;
		String sResult = "";
		try {
			sSubRepAry = sSubRepCodes.split(",");
			for (int i = 0; i < sSubRepAry.length; i++) {
				strSql = "select FRepType from "
						+ pub.yssGetTableName("Tb_Rep_Custom")
						+ " where FCusRepCode = "
						+ dbl.sqlString(sSubRepAry[i]);
				rs = dbl.openResultSet(strSql);
				if (rs.next()) {
					if (rs.getString("FRepType").equalsIgnoreCase("1")) { // 如果又是汇总的话再做一回
						sResult += buildReportData(sSubRepAry[i]);
					} else if (rs.getString("FRepType").equalsIgnoreCase("0")){	// 明细报表
						sResult += buildAllDataSource(sSubRepAry[i]);
					}
				}
				//add by songjie 2011.04.27 资产估值报游标超出最大数错误
				dbl.closeResultSetFinal(rs);
			}
			return sResult;
		} catch (Exception e) {
			throw new YssException(e.getMessage());
		} finally {
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 20110519 Added by liubo.Story 850 
	 * 工银财务估值表取数据，其中getGuessValueReport方法为直接调用获取财务估值表数据
	 * @param sSubDsCodes，sGuessValueSeq
	 * @return sResult
	 * @throws YssException
	 */
	public String buildAllDataSource_GYTest(String sRepCode,String sGuessValueSeq)throws YssException{
		String strSql="";
		ResultSet rs =null;
		String[] dpDsCode= null;
		String sResult="";
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom")
					+ " where FCusRepCode = " + dbl.sqlString(sRepCode) ;
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				//这里还要先并上格式
				//sResult =buildRepHeader(rs.getString("FRepFormatCode"));
				//edit by qiuxufeng 判断无表头就不需要查询表头格式 QDV4工银2010年11月8日01_AB
				String tmpResult =buildRepHeader(rs.getString("FRepFormatCode"));
				if(tmpResult.trim().length() == 0) {
					sResult = "0\f\f ";
				}
				else
				{
					sResult = tmpResult;
				}
				//end
				GuessValue guessVal = new GuessValue(); 
				guessVal.setYssPub(pub);
				String sGuessValueTmp = guessVal.getGuessValueReport(sGuessValueSeq);
				
				sResult += sGuessValueTmp;
				
				//edit by qiuxufeng 并上表行列设置 QDV4工银2010年11月8日01_AB
				if(tmpResult.trim().length() > 0)
				sResult += buildRepFormat(rs.getString("FRepFormatCode")) + "\f\r\n";
				else
				sResult += "\f\f \f\f " + "\f\r\n";
				//end
			}
			return sResult;
			
		} 
		catch (Exception ex){
			throw new YssException(ex);
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	
	

	/**
	 * 明细报表的处理
	 * @param sSubDsCodes
	 * @return
	 * @throws YssException
	 */
	private String buildAllDataSource(String sSubDsCodes) throws YssException {
		String strSql="";
		ResultSet rs =null;
		String[] dpDsCode= null;
		String sResult="";
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom")
					+ " where FCusRepCode = " + dbl.sqlString(sSubDsCodes);
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				//这里还要先并上格式
				//sResult =buildRepHeader(rs.getString("FRepFormatCode"));
				//edit by qiuxufeng 判断无表头就不需要查询表头格式 QDV4工银2010年11月8日01_AB
				String tmpResult =buildRepHeader(rs.getString("FRepFormatCode"));
				if(tmpResult.trim().length() == 0) {
					sResult = "0\f\f ";
				}
				else
					sResult = tmpResult;
				//end
					if(rs.getString("FSubDsCodes")!=null && rs.getString("FSubDsCodes").length()>0){
						dpDsCode=rs.getString("FSubDsCodes").split(",");
						for(int i=0;i<dpDsCode.length;i++){//遍历明细报表的数据源代码
							sResult += buildDsDatas(dpDsCode[i]);
						}
					}
				
				//edit by qiuxufeng 并上表行列设置 QDV4工银2010年11月8日01_AB
				if(tmpResult.trim().length() > 0)
				sResult += buildRepFormat(rs.getString("FRepFormatCode")) + "\f\r\n";
				else
				sResult += "\f\f \f\f " + "\f\r\n";
				//end
			}
			return sResult;
		} catch (Exception ex){
			throw new YssException(ex);
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	private String buildRepHeader(String fmtCode) throws YssException{
		ResultSet rs =null;
		String strSql="";
		StringBuffer buf =new StringBuffer();
		int iTmpRow=0;
		//--- MS01665  QDV4工银2010年8月25日01_A 需协助实现批量导表的功能  add by jiangshichao
		String strRepHeader = "";
		try{
			strSql="select * from "+pub.yssGetTableName("Tb_Rep_cell")+
			" where FrelaCode="+dbl.sqlString(fmtCode)+
			" and FRelaType='FMT' and fcontent is not null order by FRow,FCol";
			rs =dbl.openResultSet(strSql);
			while(rs.next()){
				if(rs.getInt("FRow")>iTmpRow)
					buf.append(ROW_SPLIT);
				iTmpRow=rs.getInt("FRow");
				buf.append(rs.getString("FContent")).append(COL_SPLIT);				
			}
			if(buf.toString().length()>0){
				buf.append(ROW_SPLIT);
				strRepHeader = (iTmpRow+1)+"\f\f"+buf.toString();
				//edit by qiuxufeng 不处理表头格式 
				//strRepHeader = buf.toString();
			}
			return strRepHeader;
		 //--- 	MS01665  QDV4工银2010年8月25日01_A 需协助实现批量导表的功能 end ------------
		}catch(Exception ex){
			throw new YssException("组装【"+fmtCode+"】报表格式出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	/**
	 * 根据报表数据源代码将数据拼起来
	 * @param dpDsCode
	 * @return
	 * @throws YssException
	 */
	private String buildDsDatas(String dpDsCode) throws YssException{
		//1:查出临时表
		//2：生成查询字段，可通过buildDsField(dpDsCode)方法实现
		//3:生成查询语句 
		//4:调用buildReportDetailData(sql)生成字符串数据
		String strSql = "";
		ResultSet rs = null;
		String sTable="";
		String sField="";
		String sql="";
		String strRes="";		
			try{
				if(hmFieldFormat == null)
					hmFieldFormat = new HashMap();
				hmFieldFormat.clear();
				strSql = "select * from " + pub.yssGetTableName("Tb_Rep_DataSource")
				+ " where FRepDsCode = " + dbl.sqlString(dpDsCode);
				rs = dbl.openResultSet(strSql);
				if(rs.next())
				{
					sTable = rs.getString("FStorageTab");
				}
				sField = buildDsField(dpDsCode);
				if(sField.trim().length()==0){
					return "";
				}
				if(sTable==null || sTable.trim().length()==0){
					throw new YssException("数据源【"+dpDsCode+"】没有设置存储表，导出出错！");
				}
				sTable = sTable + "_" + pub.getUserCode();
				if(dbl.yssTableExist(sTable)){				
					sql = "select "+sField+" from "+sTable;
					strRes = buildReportDetailData(sql,sField);
					//add by qiuxufeng 20101209 组装合计项 QDV4太平2010年09月16日02_A
					if(strRes.trim().length() > 0) {
						strRes += buildTotal(sql, dpDsCode);
					}//end
				}
			}catch(Exception ex){
				throw new YssException(ex);
			}finally{
				dbl.closeResultSetFinal(rs);
			}
		return strRes;
	}
	
	/**
	 * 按数据源格式配置生成格式串
	 * @param format
	 * @return
	 * @throws YssException
	 */
	private String buildFormStr(String[] format) throws YssException{
		String str="0.0";
		if(format.length==5){
			if(YssFun.isNumeric(format[2])){
				str ="";
				for(int i=0;i<YssFun.toInt(format[2]);i++){
					str+="0";
				}
			}
			if(format[3].equalsIgnoreCase("1")){
				str ="0."+str;
				if(format[4].equalsIgnoreCase("1")){
					str="#,##"+str;
				}
			}
		}
		return str;
	}
	
	/**
	 * 组装报表行列的高度、隐藏、锁定格式信息和表头合并单元格信息
	 * add by qiuxufeng 20101203
	 * @方法名：buildRepFormat
	 * @参数：String sFormatCode
	 * @返回类型：String
	 */
	private String buildRepFormat(String sFormatCode) throws YssException {
		ResultSet rs =null;
		String strSql="";
		String sReq = "";
		try{
			strSql="select * from " 
					+ pub.yssGetTableName("Tb_Rep_Format")
					+ " where FRepFormatCode = "
					+ dbl.sqlString(sFormatCode);
			rs =dbl.openResultSet(strSql);
			if(rs.next()){
				sReq = "\f\f" + rs.getString("FRCSize") + "\f\f" + rs.getString("FMerge");
			}
			return sReq;
		}catch(Exception ex){
			throw new YssException("组装【" + "列隐藏和单元格合并" + "】报表格式出错！");
		}finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
	/**
	 * 并上合计项的数据
	 * add by qiuxufeng 20101209 QDV4太平2010年09月16日02_A
	 * @方法名：buildTotal
	 * @参数：
	 * @返回类型：String
	 */
	private String buildTotal(String strSql, String dsCode) throws YssException, SQLException {
		String sResult = "";
		String sql = "";
		ResultSet rs = null;
		ResultSet rsTotal = null;
		StringBuffer buf = new StringBuffer();
		double tmpTotal = 0.0;
		try {
			sql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
					" where FRepDsCode = " + dbl.sqlString(dsCode) +
					" and FIsTotal = 1 order by FOrderIndex";
			rs = dbl.openResultSet(sql);
			if(rs.next()) {
				sql = "select * from " + pub.yssGetTableName("Tb_Rep_DsField") +
						" where FRepDsCode = " + dbl.sqlString(dsCode) +
						" order by FOrderIndex";
				rs = dbl.openResultSet(sql);
				rsTotal = dbl.openResultSet_antReadonly(strSql);
				while(rs.next()) {
					tmpTotal = 0.0;
					rsTotal.beforeFirst();
					if(rs.getString("FTotalInd").trim().length() > 0) {
						buf.append(rs.getString("FTotalInd")).append(COL_SPLIT);
					} else if(rs.getBoolean("FIsTotal")) {
						while(rsTotal.next()) {
							tmpTotal += rsTotal.getDouble(rs.getString("FDSField"));
						}
						buf.append(tmpTotal).append(COL_SPLIT);
					} else if(!rs.getBoolean("FIsTotal")){
						buf.append("").append(COL_SPLIT);
					}
				}
				//add by songjie 2011.04.27 资产估值报游标超出最大数错误
				dbl.closeResultSetFinal(rs,rsTotal);
				sResult = buf.toString();
				if(sResult.trim().length() > 0) {
					sResult = sResult.substring(0, sResult.length() - 1);
					sResult += ROW_SPLIT;
				}
			}
		} catch (Exception e) {
			throw new YssException("组装【" + "合计项数据" + "】报表格式出错！");
		} finally{
			dbl.closeResultSetFinal(rs);
			dbl.closeResultSetFinal(rsTotal);
		}
		return sResult;
	}

	/**shashijie 2012-11-19 STORY 3187 拼接格式*/
	public String buildAllDataSource_RepOverage(String sRepCode, String valueDate) throws YssException {
		String strSql="";
		ResultSet rs =null;
		String sResult="";
		
		try {
			strSql = "select * from " + pub.yssGetTableName("Tb_Rep_Custom")
					+ " where FCusRepCode = " + dbl.sqlString(sRepCode) ;
			rs = dbl.openResultSet(strSql);
			if (rs.next()) {
				//报表格式(表头)\f\f
				String tmpResult = buildRepHeader(rs.getString("FRepFormatCode"));
				if(tmpResult.trim().length() == 0) {
					sResult = "0\f\f ";
				} else {
					sResult = tmpResult;
				}
				//报表中间内容\f\f
				sResult += valueDate;
				
				//并上表行列设置\f\f \f\f
				if (tmpResult.trim().length() > 0) {
					sResult += buildRepFormat(rs.getString("FRepFormatCode"))
							+ "\f\r\n";
				} else {
					sResult += "\f\f \f\f " + "\f\r\n";
				}
			}
			return sResult;
		} 
		catch (Exception ex){
			throw new YssException(ex);
		}
		finally{
			dbl.closeResultSetFinal(rs);
		}
	}
	
}
