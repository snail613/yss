package com.yss.dsub;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

	/**
	 * SQL语句解析器类
	 * @author: 
	 * @date: 2011-4-16
	 */
	public class SqlParser{
	   
	  private StringBuffer strSql =new StringBuffer();//最终要返回的 带?SQL语句
	  private StringBuffer Sql =new StringBuffer();//保存外界传入的 SQL 进来的时候就转换成sb代替
	  private  ArrayList valueList=new ArrayList();//保存变量
	  private  ArrayList typeList=new ArrayList();//保存变量类型 下标与上面的变量一致
	  private StringBuffer lessSql=new StringBuffer();//剩余部分的SQL语句
	  private int dbType = 0;
	  public SqlParser(){
		   
	  }
	  /**
	   * 此方法专门解析where后的条件
	   * @param sql
	   * @return
	   */
	  public String getSelectSql(String sql){//获取查询语句的SQL
		  setlength0();
		  sql= trimAll(sql);//去除掉 多余的空格  此步操作 理论上可以去掉 但是解析字符串中发现 甚是麻烦故去掉杂乱空格
		  Sql.append(sql);
		  for(int i=0;i<Sql.length();i++){
			  lessSql.setLength(0);
			  char chr=Sql.charAt(i);
			  if((chr+"").equalsIgnoreCase(" ")){//如果是空格的直接调过把空格写入进入下一个
				  strSql.append(chr);	
				  continue;
			  }else if(chr=='='||chr=='>'||chr=='<'){	
				  if((i+1)!=Sql.length() && Sql.charAt(i+1)=='='){
					  lessSql.append(Sql.substring(i+2, Sql.length()).trim());	
					  strSql.append(chr+"=");
					  i=i+appendstrSql()+1;
				  }else{
					  lessSql.append(Sql.substring(i+1, Sql.length()).trim());//剩余的SQL为解可能出现参数的位置
					  strSql.append(chr);
					  i=i+appendstrSql();
				  }				  
			  }else if(chr=='l'){	
				  if(!isSpaceAhead(i)){//如果可能为关键字开头的字母上一位不是空格则不用考虑
					  strSql.append(chr);
				  }else{
					  if(Sql.substring(i).toString().startsWith("like")){//如果此关键字是like
						  i=i+4;//要跳到like的后面字符
						  lessSql.append(Sql.substring(i, Sql.length()).trim());//剩余的SQL为解可能出现参数的位置
						  strSql.append("like ");
						  i+=appendstrSql();
					  }else{
						  strSql.append(chr);  
					  }			  					  
				  }
			  }else if(chr=='b'){	
				  if(!isSpaceAhead(i)){//如果可能为关键字开头的字母上一位不是空格则不用考虑
					  strSql.append(chr);
				  }else{
					  if(Sql.substring(i).toString().startsWith("between")){//如果此关键字是between  
						  i=i+7;//要跳到like的后面字符
						  boolean iskuohu=false;
						  lessSql.append(Sql.substring(i, Sql.length()).trim());//剩余的SQL为解可能出现参数的位置
						  if(lessSql.toString().startsWith("(")){
							  i++;
							  iskuohu=true;
							  lessSql.deleteCharAt(0);
							  strSql.append("between (");
						  }else{
							  strSql.append("between ");
						  }
						  if(lessSql.toString().toLowerCase().startsWith("to_date")){
							appendstrSql();
							i = i + lessSql.indexOf("and") + 4;
							strSql.append(" and ");
							lessSql.setLength(0);
							lessSql.append(Sql.substring(i, Sql.length()).trim());
							i += appendstrSql();
						  }else{
							  i += appendstrSql();
						  }
					  }else{
						  strSql.append(chr);  
					  }			  					  
				  }
//			  }else if(chr=='i'){
//				  if(!isSpaceAhead(i)){//如果可能为关键字开头的字母上一位不是空格则不用考虑
//					  strSql.append(chr);
//				  }else if(this.isKeyWord_Join(i)){
//					  strSql.append(chr);
//				  }else{
//					  if(Sql.substring(i).toString().startsWith("in")){//如果此关键字是like
//						  i=i+4;//要跳到in的后面字符
//						  lessSql.append(Sql.substring(i, Sql.length()).trim());//剩余的SQL为解可能出现参数的位置
//						  strSql.append("in (");
//						  i+=appendstrSql();
//					  }else{
//						  strSql.append(chr);  
//					  }			  					  
//				  }
			  }else{
				  strSql.append(chr);		  
			  }  
		  }
		  return strSql.toString();
	  }	  
	  /**
	   * 由于近来的SQL语句 各种符号都有 这里先把空白部分变成空格方便后续处理
	   * 当然 就写了这么多 如果你非要写一个SQL语句包含好几千的空格那也没办法
	   * @param sql2
	   * @return
	   */
	  private String trimAll(String sql2) {
		String []sql2array=(sql2+" ").split("'");
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<sql2array.length;i++){
			if(i%2==0){
				
				sql2array[i]=sql2array[i].replaceAll("/f", " ");
				sql2array[i]=sql2array[i].replaceAll("/t", " ");
				sql2array[i]=sql2array[i].replaceAll("/n", " ");
				sql2array[i]=sql2array[i].replaceAll("round", "ROUND");
				sql2array[i]=sql2array[i].replaceAll("/r", " ");
				sql2array[i]=sql2array[i].replaceAll("	", " ");
				sql2array[i]=sql2array[i].replaceAll("          ", " ");
				sql2array[i]=sql2array[i].replaceAll("         ", " ");
				sql2array[i]=sql2array[i].replaceAll("        ", " ");
				sql2array[i]=sql2array[i].replaceAll("       ", " ");
				sql2array[i]=sql2array[i].replaceAll("      ", " ");
				sql2array[i]=sql2array[i].replaceAll("     ", " ");
				sql2array[i]=sql2array[i].replaceAll("    ", " ");
				sql2array[i]=sql2array[i].replaceAll("   ", " ");
				sql2array[i]=sql2array[i].replaceAll("  ", " ");
				sb.append(sql2array[i]);
			}else{
				sb.append("'"+sql2array[i]+"'");
			}
		}	
		return replaceEqualsBackBlank(sb.toString());
	}
	  /**
	   * 这里把字符串内的 逗号,  替换成##### 避免 insert 用,解析的时候出错
	   * @param sql2
	   * @return
	   */
	  private String replacedou(String sql2) {//把字符串内的,替换成##### 避免拆分错误
			String []sql2array=(sql2+" ").split("'");
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<sql2array.length;i++){
				if(i%2==1){
					sql2array[i]=sql2array[i].replaceAll(",", "#####");
					sb.append("'"+sql2array[i]+"'");
				}else{
					sb.append(sql2array[i]);
				}
			}	
			return sb.toString();
		}
	  /**
	   * 解析剩余部分的SQL语句
	   * @return
	   */
	private int appendstrSql(){
		  String str="";//当前语句的 第一个参数值
		  int nextindex;//获取下一个间隔符的 位置  字符串的就是'  数字的就是空格  日期型的就是括号结尾  SQL server的 也是'
		  
		  // 说明："-.0123456789E+" 都作为数字处理，那么把E作为表别名的时候解析就会有问题，所以这里进行判断   add by jiangshichao 2011.10.10
		  boolean bNumFlag = false;
		  if(isNumeric(lessSql.charAt(0)+"")){
			  bNumFlag = true;
			  if((lessSql.charAt(0)+"").equalsIgnoreCase("E")&&(lessSql.charAt(1)+"").equalsIgnoreCase(".")){
				  bNumFlag = false;
			  }
		  }
		  
		  
		  if(dbType!=YssCons.DB_SQL&&lessSql.toString().startsWith("'")){
			  nextindex=lessSql.substring(1).indexOf("'")+2;  // 获取第二个引号的位置 
			  str=lessSql.substring(0, nextindex);
			  valueList.add(str.replaceAll("'", ""));//把当前值保存的 集合里面 
			  typeList.add("String");//把当前值的类型保存到集合里面
			  strSql.append("? ");
		  }else if(dbType==YssCons.DB_SQL&&lessSql.toString().startsWith("'")){
			  nextindex=lessSql.substring(1).indexOf("'")+2; 
			  str=lessSql.substring(0, nextindex);
			 
			  if(isDate(str)){
				  valueList.add(str);
				  typeList.add("Date");
			  }else{
				  valueList.add(str.replaceAll("'", ""));
				  typeList.add("String");
			  }
			  strSql.append("? ");
		  }else if(lessSql.toString().startsWith("to_date")||lessSql.toString().startsWith("date")){//这中是 oracle日期型  db2日期型 传递进来的参数一定是 YYYY-MM-DD 
			  String format ="";
			  int firstindex=0,endindex=0,formatstartindex=0,formatendindex=0;
			  
			  nextindex=lessSql.indexOf(")"); //这里的 值是可以作为返回值 但是不是有效数据的地方
			  firstindex=lessSql.toString().indexOf("'")+1;//这个才是日期的有效位置起始
			  endindex=lessSql.substring(firstindex).toString().indexOf("'")+firstindex;//这个才是日期的有效位置截止	
			  
			  str=lessSql.substring(firstindex, endindex);
			  
			  //add by jiangshichao 2011.07.08  对特殊日期型的处理	 
			  if(YssFun.isDate(str)){
				  formatstartindex = endindex+3;
				  formatendindex = lessSql.substring(formatstartindex).toString().indexOf("'")+formatstartindex;
				  format = lessSql.substring(formatstartindex, formatendindex);
				  //普通类型的处理  to_date('2011-07-08','yyyy-mm-dd')
				  valueList.add(str+"\t"+format);
			  }else {
				  //对特殊类型的处理 to_date(SYSDATE,'yyyy-mm-dd')
				 firstindex=lessSql.toString().indexOf("(")+1;//这个才是日期的有效位置起始
				 endindex=lessSql.substring(firstindex).toString().indexOf(",")+firstindex;//这个才是日期的有效位置截止	
				 formatstartindex = endindex+2;
				 formatendindex = lessSql.substring(formatstartindex).toString().indexOf("'")+formatstartindex;
				 str=lessSql.substring(firstindex, endindex);
				 format = lessSql.substring(formatstartindex, formatendindex);
				  valueList.add(str+"\t"+format);
			  }
			  //--- add by jiangshichao 2011.07.08 ------------------
			 typeList.add("Date");
		     strSql.append("? ");
		     nextindex++;//因为要调过最后一个括号
			  
		 // }else if(lessSql.length() != 0 && isNumeric(lessSql.charAt(0)+"")){ 
		  }else if(lessSql.length() != 0 && bNumFlag){ 
			  nextindex=lessSql.indexOf(" ");//对于 数值来说 是以空格 来结束的
			  int tempIndex = lessSql.indexOf(")");//同时获取 第一个括号的位置 比较 第一个 空格和括号位置的先后来确定 数字的结束位置
			  // (tempIndex!=-1&& tempIndex < nextindex)||nextindex==-1 这中写法是 首先判断有括号则判断括号是否在空格后面 当然在空格前面的说明 是 120)的形式 或者 后面每空格了说明是结尾120) 也进这个条件
			  if( (tempIndex!=-1&& tempIndex < nextindex)||nextindex==-1){//对于 数值来说如果没有空格 那么肯定有 括号来结束 不可能出现 数值紧接字母的情况比如 123And 123Or  123a（这个是字符串） 			  
				  nextindex=tempIndex ;
			  }
			  if(nextindex==-1){//如果依然没找到结束符说明已经到了sql的最后直接 截取剩下的字符串长度即可
				  nextindex = lessSql.length();
				  str =lessSql.toString();
			  } else{
				  str=lessSql.substring(0, nextindex);
				 
			  }
			 
			  valueList.add(str);
			  str ="";
			  //把当前值保存的 集合里面 
			  typeList.add("Double");//把当前值的类型保存到集合里面
			  strSql.append("? ");		
			  
		 
		  }else{
			  nextindex=1;
			  if(lessSql.length()>0){
				  strSql.append(lessSql.charAt(0));
			  }
		  }		  
		 return nextindex;
	  
	  }
	/**
	 * 获取 rs对象方法
	 * @param sql 传入的SQL语句
	 * @param type1  resultSetType
	 * @param type2  resultSetConcurrency
	 * @param conn  数据库连接
	 * @return  返回pstm对象
	 * @throws SQLException
	 * @throws YssException
	 */
	public PreparedStatement getPstmRs(String sql ,int type1,int type2,Connection conn) throws YssException{
		String pstmSql = "";
		PreparedStatement pStmt = null;
		int lstSize = valueList.size();
		try{
			pstmSql = getSelectSql(sql);
			pStmt = conn.prepareStatement(pstmSql,type1,type2);
			lstSize = valueList.size();
			for ( int i = 0 ; i < lstSize ; i++){
				if(typeList.get(i).toString().equalsIgnoreCase("String")){
					pStmt.setString(i+1, String.valueOf(valueList.get(i)));
				}else if(typeList.get(i).toString().equalsIgnoreCase("Date")){
					String sDate = String.valueOf(valueList.get(i)).split("\t")[0];
					String format = String.valueOf(valueList.get(i)).split("\t")[1];
					pStmt.setDate(i+1, YssFun.toSqlDate( YssFun.parseDate(sDate, format)));
				}else if(typeList.get(i).toString().equalsIgnoreCase("Double")){
					pStmt.setDouble(i+1, Double.parseDouble(String.valueOf(valueList.get(i))));
				}else if(typeList.get(i).toString().equalsIgnoreCase("Object")){
					pStmt.setObject(i+1, null);//object始终 给NULL 日后有变更需要更改注释
				}
			}
		}catch(Exception e ){
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append(" ★☆★☆★☆★☆★☆★☆★☆★  解析原始SQL语句出错  ☆★☆★☆★☆★☆★☆★☆★☆ \r\n ");
			errorMsg.append(" 原始SQL语句：").append(sql).append("\r\n");
			errorMsg.append(" 解析后的SQL语句: ").append(pstmSql).append("\r\n");
			errorMsg.append(" ★☆★☆★☆★☆★☆★☆★☆★  解析原始SQL语句出错  ☆★☆★☆★☆★☆★☆★☆★☆ \r\n ");
			throw new YssException(errorMsg.toString());
		}
		return pStmt;
	}
	/**
	 * 执行 insert或update的语句
	 * @param sql 传入SQL语句
	 * @param conn 数据库连接
	 * @return
	 * @throws SQLException
	 * @throws YssException
	 */
//	public PreparedStatement executePstm(String sql  ,Connection conn) throws SQLException, YssException{
//		String pstmSql = "";
//		String tmpSql = sql;
//		if(tmpSql.trim().toUpperCase().indexOf("INSERT") == 0  && tmpSql.toUpperCase().indexOf("VALUES") > -1 ){//以insert 并且包含values 开头 默认为 插入
//			pstmSql = getInertIntoSql(sql);
//		}else if(tmpSql.trim().toUpperCase().indexOf("UPDATE") == 0  ){//以insert  开头 默认为 修改
//			pstmSql = getUpdateSetWherePstm(sql);//截取 update 至 where 之间的部分进行拼接
//			if(tmpSql.toUpperCase().indexOf(" WHERE ")>-1){
//				pstmSql =pstmSql +" "+	getSelectSql(sql.substring(tmpSql.toUpperCase().indexOf(" WHERE ")));//处理where后的
//			}
//		}else{//其他未 delete/ select /（insert into select） 三种情况
//			pstmSql = getSelectSql(sql);
//		}
//		PreparedStatement pStmt = conn.prepareStatement(pstmSql);
//		int lstSize = valueList.size();
//		for ( int i = 0 ; i < lstSize ; i++){
//			if(typeList.get(i).toString().equalsIgnoreCase("String")){
//				pStmt.setString(i+1, String.valueOf(valueList.get(i)));
//
//			}else if(typeList.get(i).toString().equalsIgnoreCase("Date")){
//
//				pStmt.setDate(i+1, YssFun.toSqlDate( YssFun.toDate(String.valueOf(valueList.get(i)))));
//			}else if(typeList.get(i).toString().equalsIgnoreCase("Double")){
//
//				pStmt.setDouble(i+1, Double.parseDouble(String.valueOf(valueList.get(i))));
//			}else if(typeList.get(i).toString().equalsIgnoreCase("Object")){
//
//				pStmt.setObject(i+1, null);//object始终 给NULL 日后有变更需要更改注释
//			}
//		}
//		 
//		return pStmt;
//		
//	}
	/**
	 * 此方法专门解析 insert语句
	 * @param sql
	 * @return
	 */
//	public String getInertIntoSql(String sql){
//		String tempStr = "";//记录被替换部分
//		StringBuffer thhstr =new StringBuffer();//替换后的 ？ 字符串
//		String[] obj = null;
//		PreparedStatement pStmt = null;
//		sql=replacedou(sql);
//		sql=trimAll(sql);
//		String clSQL = sql;//保存最初是的sql  
//		sql = sql.replaceAll("','yyyy-MM-dd", "'");//系统中有 YYYY/MM/DD 但没有用的地方
//		if( clSQL.trim().toUpperCase().indexOf("values".toUpperCase()) > -1 ){
//			//得到 values 后面括号内的内容 如果 表字段 ，或 insert into 的具体值中包含 values 会存在问题 到时候需要对 sql进行修改 
//			sql = sql.trim().substring(clSQL.trim().toUpperCase().indexOf("values".toUpperCase())+6);
//		} 
//		//没有关键字values的 insert 语句 那么 从 他的第一个左括号开始截取
//		tempStr =sql.trim().substring(clSQL.trim().toUpperCase().indexOf("(")+1,sql.trim().length()-1);//sql.trim().length()-2去掉最后一个 右括号“)”
//		obj = tempStr.trim().split(",");
//		
//		 
//		
//		thhstr.append(sql.trim().substring(0,clSQL.toUpperCase().indexOf("values".toUpperCase())+6)+"(");//参数值前的部分
//		for ( int i = 0 ; i < obj.length ; i++ ){
//			 
//			 if(obj[i] == null || obj[i].toString().equals("")){
//				 //空数据 setObject null
//				  valueList.add("null");//对于Object对应的值 永远为null
//				  typeList.add("Object");
//				  thhstr.append("?");
//				  if(i!=(obj.length-1)){
//					thhstr.append(",");
//				  }
//			 }else if(obj[i].trim().indexOf("to_date('")  == 0 || obj[i].trim().indexOf("DATE('")  == 0 ){//oracle 和db2下为 日期类型的条件
//				 valueList.add( replaceTo_Date(obj[i].toString())); 
//				 typeList.add("Date");
//				 thhstr.append("?");
//				 if(i!=(obj.length-1)){
//					 thhstr.append(",");
//				 }
//			 }else if(obj[i].trim().indexOf("'")==0 ){//第一个为字符的说明是字符串
//				 if( isDate(obj[i].replaceAll("'", "")) &&sql.indexOf(" LVarList ")==-1 &&dbType==YssCons.DB_SQL){//可以转换为日期说明 是sqlserver 数据库下的日期类型
//					 valueList.add(obj[i].trim().replaceAll("'", "")); 
//					 typeList.add("Date");
//				 }else{//字符串
//					 valueList.add(obj[i].trim().replaceAll("'", "").replaceAll("#####", ",")); 
//					 typeList.add("String");
//				 }
//				 thhstr.append("?");
//				 if(i!=(obj.length-1)){
//					 thhstr.append(",");
//				 }
//			}else if(isNumeric(obj[i].trim())) {//如果是数字
//				 valueList.add(obj[i]);
//				 typeList.add("Double");//把当前值的类型保存到集合里面
//				 thhstr.append("?");
//				 if(i!=(obj.length-1)){
//					thhstr.append(",");
//				 }
//			} else{
//				thhstr.append(obj[i]);
//				if(i!=(obj.length-1)){
//					thhstr.append(",");
//				}
//			}
//		}
//		 
//		
// 
//		return thhstr.toString()+")";
//	}
	/**
	 * 此方法专门解析 update语句
	 * @param sql
	 * @return
	 */
//	private String  getUpdateSetWherePstm(String sql) {
//	 	String[] obj = null;
//		PreparedStatement pStmt = null;
//		sql=replacedou(sql);
//		sql=trimAll(sql);
//		sql = sql.replaceAll("','yyyy-MM-dd'", "'");//系统中有 YYYY/MM/DD 但没有用的地方
//		String clSQL = sql;//保存最初是的sql 实际没什么用
//		//得到 set 和where 之间的 部分start
//		String tempsql ="";
//		if(clSQL.trim().toUpperCase().indexOf(" WHERE ")>-1){
// 			tempsql=sql.trim().substring( clSQL.trim().toUpperCase().indexOf(" SET ")+4,clSQL.trim().toUpperCase().indexOf(" WHERE "));
//		}else{
//			tempsql=sql.trim().substring( clSQL.trim().toUpperCase().indexOf(" SET ")+4,sql.trim().length());			
//		}
//		sql=sql+" ";
//		clSQL = clSQL + " ";
//			//得到 set 和where 之间的 部分end
//		//按照 逗号 split
//		obj =tempsql.split(",");//得到的格式为 数据库字段 = 具体的更新后的值
//		String tempUpdateValue="";
//		tempsql = sql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4);
//		for (int i = 0 ; i < obj.length ; i++){//考虑 如果是数据库字段=数据库字段+1的情况怎么处理？这种绑定变量貌似不支持吧 判断如果 没有双引号且不为空的情况是否 为 数字 如果不是数字需要加上双引号 这样应该可以解决
//			//将  数据库字段 = 具体的更新后的值  中具体的值替换为 问号 并将原有值保存到 updateValue list中
//			 tempUpdateValue = obj[i].toString().substring(obj[i].toString().indexOf("=")+1).trim();
//			 if(obj[i].indexOf("FFHTS")>-1){
//				 pStmt=null;
//			 }
//			  if(tempUpdateValue.toUpperCase().trim().indexOf("TO_DATE('")  == 0 || tempUpdateValue.trim().indexOf("DATE('")  == 0 ){//oracle 和db2下为 日期类型的条件
//				 valueList.add( replaceTo_Date(tempUpdateValue.toString())); 
//				 typeList.add("Date");
//				 
//				 obj[i] = obj[i].toString().substring(0,obj[i].toString().indexOf("=")+1)+" ? ";
//				 if( i == 0 && i == (obj.length -1)){
//					tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i];
//				 }else if (i == 0 && i != (obj.length -1) ){
//					 tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i]+", ";
//				 }else if( i == obj.length -1 ){
//						tempsql = tempsql+" " +obj[i];
//				 }else{
//					  tempsql =tempsql+ obj[i]+", ";
//				 }
//				 
//			 }else if(tempUpdateValue.trim().indexOf("'")==0 ){//第一个为字符的说明是字符串
//				 if( isDate(tempUpdateValue.replaceAll("'", "")) &&sql.indexOf(" LVarList ")==-1&&dbType==YssCons.DB_SQL){//可以转换为日期说明 是sqlserver 数据库下的日期类型
//					 valueList.add(tempUpdateValue.replaceAll("'", "")); 
//					 typeList.add("Date");
//				 }else{//字符串
//					 valueList.add(tempUpdateValue.replaceAll("'", "").replaceAll("#####", ",")); 
//					 typeList.add("String");
//				 }
//				 obj[i] = obj[i].toString().substring(0,obj[i].toString().indexOf("=")+1)+" ? ";
//				 if( i == 0 && i == (obj.length -1)){
//					tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i];
//				 }else if (i == 0 && i != (obj.length -1) ){
//					 tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i]+", ";
//				 }else if( i == obj.length -1 ){
//						tempsql = tempsql+" " +obj[i];
//				 }else{
//					  tempsql =tempsql+ obj[i]+", ";
//				 }
//			}else if(isNumeric(tempUpdateValue.trim())) {//如果是数字
//				 valueList.add(tempUpdateValue.trim());
//				 typeList.add("Double");//把当前值的类型保存到集合里面
//				 obj[i] = obj[i].toString().substring(0,obj[i].toString().indexOf("=")+1)+" ? ";
//				 if( i == 0 && i == (obj.length -1)){
//					tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i];
//				 }else if (i == 0 && i != (obj.length -1) ){
//					 tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i]+", ";
//				 }else if( i == obj.length -1 ){
//						tempsql = tempsql+" " +obj[i];
//				 }else{
//					  tempsql =tempsql+ obj[i]+", ";
//				 }
//			}else{//其他情况不替换
//				 if( i == 0 && i == (obj.length -1)){
//					tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i];
//				 }else if (i == 0 && i != (obj.length -1) ){
//					 tempsql = tempsql.substring(0, clSQL.trim().toUpperCase().indexOf(" SET ")+4)+" " +obj[i]+", ";
//				 }else if( i == obj.length -1 ){
//						tempsql = tempsql+" " +obj[i];
//				 }else{
//					  tempsql =tempsql+ obj[i]+", ";
//				 }
//			}
//			
//			
//		}
//		return tempsql;
//	}
	/**
	 * 去掉日期形式里面的无用字符 比如 yyyymmdd这些
	 * @param str
	 * @return
	 */
	public static String replaceTo_Date(String str ){
		String rtuSql = "";
		int startIndex = 0;
		int endIndex = 0;
		for (int i = 0 ; i < str.length() ; i++ ){
			if(startIndex == 0 && String.valueOf(str.charAt(i)).equals("'")){
				startIndex = i;
			}else if(endIndex == 0 && String.valueOf(str.charAt(i)).equals("'")){
				endIndex = i;
				break;
			}
		}
		return str.substring(startIndex+1,endIndex);
	}

	private void setlength0(){
	  Sql.setLength(0);//暂时设置为0 以后可以写其他方法的时候重用此变量
	  strSql.setLength(0);//暂时设置为0 以后可以写其他方法的时候重用此变量
	  lessSql.setLength(0);
	}
	private boolean isDate(String date){//仅支持YYYY-MM-DD 情况 此处仅有SQL SERVER数据库要用到此函数
		return YssFun.isDate(date);
	}
	private boolean isNumeric(String number){//判断是否是数字
		String numbers = "-.0123456789E+";
		for (int i = 0 ; i < number.length() ; i++){
			if(numbers.indexOf(number.charAt(i)) == -1){
				return false;
			}
		}
		return true;
	}
	private boolean isSpaceAhead(int index){//判断上一位字符是否是空格	   是空格则返回true
		return (Sql.charAt(index-1)+"").equalsIgnoreCase(" ");
	}
	
	// 判断是否为关键字
	private boolean isKeyWord_Join(int index){
		boolean flag = false;
		if(Sql.substring(index-2, index+1).toString().toUpperCase().equalsIgnoreCase("JOIN")){
			flag = true;
		}
		
		return flag;
	}
	public ArrayList getValueList() {
		return valueList;
	}
	public void setValueList(ArrayList valueList) {
		this.valueList = valueList;
	}
	public ArrayList getTypeList() {
		return typeList;
	}
	public void setTypeList(ArrayList typeList) {
		this.typeList = typeList;
	}
	public int getDbType() {
		return dbType;
	}
	public  void setDbType(int dbType0) {
		dbType = dbType0;
	}
	/**
	 * 替换等号后面的空格
	 * @param str
	 * @return
	 */
	public String replaceEqualsBackBlank(String str){
		StringBuffer sb = new StringBuffer(str);
		int tempIndex = 0;
		for (int i = 0 ; i < sb.length(); i++){
			tempIndex = 0;
			if((sb.charAt(i) == '=' || sb.charAt(i) == '<' || sb.charAt(i) == '>' ) && sb.charAt(i+1)==' '){
				for(int j = 1 ; j < 100 ; j++){//最多可以删掉等号后面100个空格
					if(sb.charAt(i+j) != ' '){
						tempIndex = i+j;//记录不是等号后面第一个不是空格的位置
						break;//发现第一个不是空格的时候结束循环 
					}
				}
			 	sb.delete(i+1, tempIndex);	
			}
			 
			 
		}
		return sb.toString();
		
	}
	
}
