package com.yss.main.report;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.yss.dsub.BaseBean;
import com.yss.main.dao.IClientReportView;
import com.yss.main.operdeal.report.BaseBuildCommonRep;
import com.yss.pojo.param.comp.YssCommonRepCtl;
import com.yss.util.YssException;
import com.yss.util.YssFun;
import com.yss.util.YssUtil;
import com.yss.vsub.YssFinance;


/**shashijie 2013-3-25 STORY 3368 报表导出处理*/
public class RepMultiExportManage extends BaseBean implements IClientReportView{

	private String sqlStr = null;//执行的SQL语句
	private String sqlPara = null;//执行SQL语句的参数
	private String fashion = "No";//处理模式,默认不循环任何条件
	
	private ArrayList alRepParam;//参数值放置容器
	
	
	
	/**shashijie 2013-3-25 STORY 3368 获取报表数据 */
	public String getReportData(String sReportType) throws YssException {
		String reStr = null;
		String strSql = null;
		strSql = this.sqlStr;
		ResultSet rs = null;
		ResultSetMetaData metaRs = null;
		String rowData = "";
		StringBuffer repBuf = new StringBuffer();
		try {
			rs = dbl.openResultSet(buildSqlStr(strSql));//打开记录集
			metaRs = rs.getMetaData();//获取记录集对应的元数据
			while (rs.next()) {
                rowData = getRowDataByMetaData(metaRs,rs);//获取每行的数据（各列具体的值）
                repBuf.append(rowData);               
                repBuf.append("\r\n");//拼装每列的数据              
			}
			if (repBuf.length() > 0)
			{
				repBuf.delete(repBuf.length() - 2, repBuf.length());//删除\r\n
			}
			reStr = repBuf.toString();
		} catch (SQLException e) {
			throw new YssException("获取批量导出报表出错", e);
		} finally {
			dbl.closeResultSetFinal(rs);
		}

		return reStr;
	}
	
	/**shashijie 2013-3-25 STORY 3368  返回每行中各列的值用\t分割 */
	private String getRowDataByMetaData(ResultSetMetaData metaData, ResultSet rs)
			throws YssException {
		String reStr = "";
		String columnName = "";//索引值的列名
		int colType;
		
		StringBuffer bufRow = new StringBuffer();
		try {
			for (int cols = 1; cols <= metaData.getColumnCount(); cols++) {//循环列
				columnName = metaData.getColumnName(cols);//获取索引值的列名
				colType = metaData.getColumnType(cols);//获取索引值的列数据类型
				
				bufRow.append(getColData(rs,columnName,colType));//获取此列的数据
				if (cols < metaData.getColumnCount())
				{
					bufRow.append("\t");//拼装列值
				}
			}//end for
            reStr = bufRow.toString();
		} catch (SQLException e) {
			throw new YssException("获取批量导出报表元数据出错", e);
		}

		return reStr;
	}
	
	/**shashijie 2013-3-25 STORY 3368  对记录集中的数据分不同类型进行处理 */
	private String getColData(ResultSet rs,String colName,int colType){
		String colData = "";
		try {
			if (colType == java.sql.Types.VARCHAR)//若为字符串
			{
				colData = rs.getString(colName);
			}else if (colType == java.sql.Types.NUMERIC)//若为数字
			{
				colData = new Double(rs.getDouble(colName)).toString();
			}else if (colType == java.sql.Types.DATE)//若为日期型
			{
				colData = YssFun.formatDate(rs.getDate(colName));
			}else if (colType == java.sql.Types.CHAR){//单字符类型
				colData = rs.getString(colName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return colData;
	}


	public String getReportHeaders(String sReportType) throws YssException {
		
		return null;
	}

	public String buildRowStr() throws YssException {
		
		return null;
	}

	public String getOperValue(String sType) throws YssException {
		
		return null;
	}

	public void parseRowStr(String sRowStr) throws YssException {
		String[] reqAry = null;
		try {
			if (sRowStr.trim().length() == 0) {
				return;
			}
			reqAry = sRowStr.split("\t");
			this.sqlStr = reqAry[0];//前台传入的ＳＱＬ语句
			this.sqlPara = reqAry[1];//前台传入的SQL参数
			this.fashion = reqAry[2];//处理模式
		} catch (Exception e) {
			throw new YssException("解析出错", e);
		}

	}

	/**shashijie 2013-3-25 STORY 3368 通过前台传入的SQL参数，拼装出能够完整执行的SQL*/
	private String buildSqlStr(String SQL) throws YssException
	{
		//保留原始表前缀
		String currentAsset = pub.getPrefixTB();
		String sqlStr = "";
		try {
			if(this.sqlPara != null)
			{
				parsePara(this.sqlPara);//解析前台传入的SQL参数
				//若选择循环组合群处理则要修改表前缀
				if (this.fashion.indexOf("Group")>-1) {
					YssCommonRepCtl ctl = (YssCommonRepCtl)alRepParam.get(2);//组合群代码在第三个字符中
					pub.setPrefixTB(ctl.getCtlValue());//表前缀
					pub.setAssetGroupCode(ctl.getCtlValue());//组合群
				}
				sqlStr = buildSql(SQL);//解析SQL
			}
		} catch (Exception e) {
			throw new YssException("解析出错", e);
		} finally {
			pub.setPrefixTB(currentAsset);//表前缀
			pub.setAssetGroupCode(currentAsset);//组合群
		}
		return sqlStr;
	}
	
	/**shashijie 2013-3-25 STORY 3368 解析SQL参数*/
	private void parsePara(String sqlPara)
	{ 
		alRepParam = new ArrayList();
		String[] sRepCtlParamAry = sqlPara.split("\n");//分割前台传入的多个参数
        for (int i = 0; i < sRepCtlParamAry.length; i++) {//循环参数
        	if (YssUtil.isNullOrEmpty(sRepCtlParamAry[i])) {
				continue;
			}
        	YssCommonRepCtl repCtl = new YssCommonRepCtl();//参数值对象
        	repCtl.parseRowStr(sRepCtlParamAry[i]);//解析每个参数，并形成参数对象
            alRepParam.add(repCtl);//将参数对象放置如全局容器中，以便在拼装SQL时使用
        }
	}
	
	/**shashijie 2013-3-25 STORY 3368 解析带转换参数的SQL*/
	private String buildSql(String sDs) throws YssException {
		System.out.println("\r\n\r\n"+sDs+"\r\n\r\n");
		for (int i = 0; i < alRepParam.size(); i++) {
			//参数值对象
			YssCommonRepCtl repCtl = (YssCommonRepCtl) alRepParam.get(i);
			//根据条件参数判断
			sDs = doReplaceCtl(sDs,repCtl);
		}
		sDs = replaceAll(sDs,alRepParam);//全局替换
		sDs = new BaseBuildCommonRep().wipeSqlCond(sDs);//将未赋值的SQL条件去除
		
		//若还有未解析的字符串则递归调用
		if (isAnalyze(sDs)) {
			sDs = buildSql(sDs);
		}
		
		sDs = sDs.replaceAll("~Base", "base");

		return sDs;
	}

	/**shashijie 2013-4-2 STORY 3368 根据控件值替换SQL条件*/
	private String doReplaceCtl(String SQL, YssCommonRepCtl ctl) throws YssException {
		String sDs = SQL;
		//数据初步验证
		if (YssUtil.isNullOrEmpty(SQL) || ctl == null) {
			return sDs;
		}
		
		String sDataType = "";//数据类型的标识 S:字符型,I:数字型,D:日期型
		String sSqlValue = "";//替换值
		//替换参数的标识
		String sInd = "<" + (ctl.getCtlIndex()) + ">";
		int iPos = sDs.indexOf(sInd);
		//如果没有不带空格的标示,就再查找带空格的标示试试
		if (iPos <= 0) {
			sInd = " < " + (ctl.getCtlIndex()) + " >";
			iPos = sDs.indexOf(sInd);
		}
		if (iPos <= 0) {
			sInd = "< " + (ctl.getCtlIndex()) + " >";
			iPos = sDs.indexOf(sInd);
		}
		if (iPos > 1) {
			sDataType = sDs.substring(iPos - 1, iPos);
			//SQL的String转换
			if (sDataType.equalsIgnoreCase("S") && !YssUtil.isNullOrEmpty(ctl.getCtlValue())) {
				sSqlValue = dbl.sqlString(ctl.getCtlValue());
			} //原封不动
			else if (sDataType.equalsIgnoreCase("I") && !YssUtil.isNullOrEmpty(ctl.getCtlValue())) {
				sSqlValue = ctl.getCtlValue();
			} //日期
			else if (sDataType.equalsIgnoreCase("D") && !YssUtil.isNullOrEmpty(ctl.getCtlValue())) {
				sSqlValue = dbl.sqlDate(YssFun.parseDate(ctl.getCtlValue(), "yyyy-MM-dd"));
			} //转换代码，例如 001,002转换成'001','002'
			else if (sDataType.equalsIgnoreCase("N") && !YssUtil.isNullOrEmpty(ctl.getCtlValue())) {
				sSqlValue = operSql.sqlCodes(ctl.getCtlValue());
			} //替换当前日期年份
			else if (sDataType.equalsIgnoreCase("Y") && !YssUtil.isNullOrEmpty(ctl.getCtlValue())) {
				sSqlValue = YssFun.formatDate(ctl.getCtlValue(), "yyyy");
			} //组合获取套帐
			else if (sDataType.equalsIgnoreCase("Z") && !YssUtil.isNullOrEmpty(ctl.getCtlValue())) {
				YssFinance cw = new YssFinance();
				cw.setYssPub(pub);
				String asset = cw.getBookSetId(pub.getAssetGroupCode(), ctl.getCtlValue());
				sSqlValue = YssFun.formatNumber(Double.valueOf(asset), "000");
			}
			//有替换值时才可以替换
			if (!YssUtil.isNullOrEmpty(sSqlValue)) {
				sDs = sDs.replaceAll(sDataType + sInd, sSqlValue);
			}
			
		}
		return sDs;
	}

	/**shashijie 2013-4-2 STORY 3368 全局替换SQL语句*/
	private String replaceAll(String SQL, ArrayList param) throws YssException {
		//数据判断
		if (YssUtil.isNullOrEmpty(SQL) || param==null || param.isEmpty()) {
			return SQL;
		}
		
		String sDs = SQL;
		//用户
		if (sDs.indexOf("<U>") > 0) {
			sDs = sDs.replaceAll("<U>", pub.getUserCode());
		} else if (sDs.indexOf("< U >") > 0) {
			sDs = sDs.replaceAll("< U >", pub.getUserCode());
		} else if (sDs.indexOf("<User>") > 0) {
			sDs = sDs.replaceAll("<User>", pub.getUserCode());
		} else if (sDs.indexOf("< User >") > 0) {
			sDs = sDs.replaceAll("< User >", pub.getUserCode());
		}
		//年份
		if (sDs.indexOf("<Year>") > 0) { // 把"<Year>"的标识替换成当前日期的年份
			YssCommonRepCtl repCtl = (YssCommonRepCtl) param.get(0);
			sDs = sDs.replaceAll("<Year>",YssFun.formatDate(repCtl.getCtlValue(), "yyyy"));
		} else if (sDs.indexOf("< Year >") > 0) { // 把"< Year >"的标识替换成结束日期的年份
			YssCommonRepCtl repCtl = (YssCommonRepCtl) param.get(0);
			sDs = sDs.replaceAll("< Year >",YssFun.formatDate(repCtl.getCtlValue(), "yyyy"));
		}
		//组合群
		if (sDs.indexOf("<Group>") > 0) {
			sDs = sDs.replaceAll("<Group>", pub.getAssetGroupCode());
		} else if (sDs.indexOf("< Group >") > 0) {
			sDs = sDs.replaceAll("< Group >", pub.getAssetGroupCode());
		}
		
		//替换当前日期月份
		if (sDs.indexOf("<Month>") > 0) {
			YssCommonRepCtl repCtl = (YssCommonRepCtl) param.get(0);
			sDs = sDs.replaceAll("<Month>",YssFun.formatDate(repCtl.getCtlValue(), "MM"));
		}
		//套帐
		if (sDs.indexOf("<Set>") > 0) {
			YssCommonRepCtl repCtl = (YssCommonRepCtl) param.get(3);
			YssFinance cw = new YssFinance();
			cw.setYssPub(pub);
			String asset = cw.getBookSetId(pub.getAssetGroupCode(), repCtl.getCtlValue());
			sDs = sDs.replaceAll("<Set>",YssFun.formatNumber(Double.valueOf(asset), "000"));
		}
		return sDs;
	}

	/**shashijie 2013-4-1 STORY 3368 如果还有未解析的字符串则返回 true*/
	private boolean isAnalyze(String sDs) {
		boolean flag = false;
		if (sDs.indexOf("<") > 0) {
			String temp = sDs.substring(sDs.indexOf("<")+1);
			String fnum = temp.substring(0,temp.indexOf(">"));
			if (YssFun.isNumeric(fnum)) {
				flag = true;
			} else {
				if (temp.indexOf("<") > 0) {
					flag = isAnalyze(temp);
				}
			}
		}
		return flag;
	}

	public String GetBookSetName(String sPortCode) throws YssException {
		return null;
	}

	public String checkReportBeforeSearch(String sReportType)
			throws YssException {
		return null;
	}

	public String getSaveDefuntDay(String sRepotyType) throws YssException {
		return null;
	}

	/**add---shashijie 2013-3-22 返回 fashion 的值*/
	public String getFashion() {
		return fashion;
	}

	/**add---shashijie 2013-3-22 传入fashion 设置  fashion 的值*/
	public void setFashion(String fashion) {
		this.fashion = fashion;
	}
	   
	   
	  

}
