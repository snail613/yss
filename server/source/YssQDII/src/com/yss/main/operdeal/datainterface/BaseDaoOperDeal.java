package com.yss.main.operdeal.datainterface;

import java.sql.ResultSet;

import com.yss.dsub.BaseBean;
import com.yss.main.datainterface.DaoCusConfigureBean;
import com.yss.main.operdeal.datainterface.pojo.CommonPrepFunBean;
import com.yss.util.YssException;
import com.yss.util.YssFun;

public class BaseDaoOperDeal
    extends BaseBean {
    protected String portCodes = "";
    protected java.util.Date beginDate;
    protected java.util.Date endDate;
    protected String cusCfgCode;
    protected String allData;
    protected String tradeSeat;
    protected String check = ""; //20071107  chenyibo  增加在数据导入的时候是否要审核
    protected String PretreatCode = ""; //MS00032 增加预处理代码
    protected String fileFilter="";//文件筛选条件 by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
    protected CommonPrepFunBean prepFunBean = null; // 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
    protected java.util.Date dealDate;//处理日期    MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据
   
    public BaseDaoOperDeal() {
    }

    public void init(String sPorts) {
        this.portCodes = sPorts;
    }
  // MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据
   public void setDealDate(java.util.Date dealDate){
	   this.dealDate = dealDate;
   }
   // MS01615 QDV4太平2010年08月16日01_A 以接口的方式进行批量导入定存数据
   
    public void init(java.util.Date dBeginDate, java.util.Date dEndDate, String sPortCodes, String sCusCfgCode, String tradeSeat) {
        this.portCodes = sPortCodes;
        this.beginDate = dBeginDate;
        this.endDate = dEndDate;
        this.cusCfgCode = sCusCfgCode;
        this.tradeSeat = tradeSeat;
    }

    //20071107   chenyibo  重载一个方法
    public void init(java.util.Date dBeginDate, java.util.Date dEndDate, String sPortCodes, String sCusCfgCode, String check, String tradeSeat) {
        this.portCodes = sPortCodes;
        this.beginDate = dBeginDate;
        this.endDate = dEndDate;
        this.cusCfgCode = sCusCfgCode;
        this.check = check; //chenyibo   20071107
        this.tradeSeat = tradeSeat;
    }

    public void setAllData(String allData) {
        this.allData = allData; //20071205   chenyb
    }

    //导入操作时获取文件名
    public String getImpPathFileName() throws YssException {
        return "";
    }

    public String doInterface() throws YssException {
        return "";
    }

    /**
     * 新增处理接口处的预处理方法，根据一个预处理代码来执行一个预处理 MS00032
     * 返回 预处理执行的结果
     */
    public String doOnePretreat(String pretreatCode) throws YssException {
        return "";
    }

    public void impInterface() throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException("导入数据出错", e);
        }
    }

    public void expInterface() throws YssException {
        try {

        } catch (Exception e) {
            throw new YssException("导出数据出错", e);
        }
    }

    /**
     * 初始化各个参数
     */
    protected void initParams() {
        //====// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20
        prepFunBean = new CommonPrepFunBean();
        prepFunBean.setAllData(allData);
        prepFunBean.setBeginDate(beginDate);
        prepFunBean.setCheck(check);
        prepFunBean.setCusCfgCode(cusCfgCode);
        prepFunBean.setEndDate(endDate);
        prepFunBean.setPortCodes(portCodes);
        prepFunBean.setTradeSeat(tradeSeat);
        //====// 新需求：QDV4交银施罗德2008年10月20日01_A by leeyu 2008-10-20

   }
   
   /**
    * 根据文件内容设置生成排序字符串
    * by leeyu　QDV4易方达2009年8月13日01_B MS00995 20100223
    * @param cusCfgCode 接口代码
    * @return 返回排序字符串
    * @throws YssException
    */
	protected String buildOrderByStr(String cusCfgCode) throws YssException {
		String sOrderBy = "";
		ResultSet rs = null;
		String sqlStr = "";
		try {
			/**add---shashijie 2013-3-1 STORY 3366 获取公共表查询*/
			//组合群SQL
			sqlStr = getSelectFileContentByFCusCfgCode(pub.yssGetTableName("Tb_Dao_FileContent"), "0", cusCfgCode);
            
        	sqlStr += " Union All ";
            //公共表SQL
        	sqlStr += getSelectFileContentByFCusCfgCode("Tb_Dao_FileContent", "1", cusCfgCode);
			/**end---shashijie 2013-3-1 STORY 3366*/
			rs = dbl.openResultSet(sqlStr);
			while (rs.next()) {
				sOrderBy = sOrderBy
						+ (rs.getString("FTabFeild") + " "
								+ rs.getString("FOrder") + ",");
			}
			if (sOrderBy.endsWith(",")) {
				sOrderBy = sOrderBy.substring(0, sOrderBy.length() - 1);
			}
		} catch (Exception ex) {
			throw new YssException("生成排序字段出错！", ex);
		} finally {
			dbl.closeResultSetFinal(rs);
		}
		return sOrderBy.equalsIgnoreCase("") ? "" : " Order by " + sOrderBy;
	}
   
	/**shashijie 2013-3-1 STORY 3366*/
	private String getSelectFileContentByFCusCfgCode(String tableName,
			String saveType, String cusCfgCode) {
		String sql = "";
		//页面列表头一列显示数据来源公共还是组合群
		if (saveType.equals("0")) {
			sql = " select distinct '组合群' as saveType , ";
		} else {
			sql = " select distinct '公共' as saveType , ";
		}
		sql += "  FTabFeild,FOrder from "
			+ tableName
			+ " where FCusCfgCode=" + dbl.sqlString(cusCfgCode)
			+ " and " + "(upper(FOrder)='ASC' or upper(FOrder)='DESC')";
		return sql;
	}

	/** 获取本接口的筛选条件
     * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
     * @return
     * @throws YssException
     */
    public String getFileFilterStr(DaoCusConfigureBean cusCfgBean) throws YssException{
    	if(fileFilter.length()==0){
    		buildFileFilter(cusCfgBean);
    	}
    	return fileFilter;
    }
    
    /**
     * 具体的获取筛选条件的方法
     * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
     * @param cusCfg
     * @throws YssException
     */
    private void buildFileFilter(DaoCusConfigureBean cusCfg) throws YssException{
    	ResultSet rs =null;
    	String sqlStr="";
    	String relation=" and "; //字段间默认采用 and 关系，先定死
    	StringBuffer buf=new StringBuffer();
    	try{
    		sqlStr="select FContent,FCusCfgCode,FFieldCode,FFieldType from "+
    		pub.yssGetTableName("Tb_Dao_FileFilter")+
    		" where FCusCfgCode="+dbl.sqlString(cusCfg.getCusCfgCode());
    		rs =dbl.openResultSet(sqlStr);
    		while(rs.next()){
    			String fileType=rs.getString("FFieldType");
    			String[] arrFilterContent=rs.getString("FContent").trim().split(";");//先去掉空格,按;划分
    			if(arrFilterContent.length>1) buf.append("(");
				for (int i = 0; i < arrFilterContent.length; i++) {
					String[] arrContent=null;	//按等于号分隔
					String content="";			//内容
					String tmpRela="";			//内容关系
					arrFilterContent[i]=arrFilterContent[i].trim();//去掉空格
					if(arrFilterContent[i].length()==0) continue;
					arrContent=arrFilterContent[i].split("=");
					if(arrContent.length==2){
						if(arrContent[1].startsWith("{"))
							arrContent[1] =arrContent[1].substring(1);
						if(arrContent[1].toUpperCase().endsWith("AND")){
							tmpRela=" and ";
						}else if(arrContent[1].toUpperCase().endsWith("OR")){
							tmpRela=" or ";
						}else{
							tmpRela=" ";
						}
						if(arrContent[1].lastIndexOf("}")>-1)
							arrContent[1] = arrContent[1].substring(0,arrContent[1].lastIndexOf("}"));
						
						if(arrContent[1].toUpperCase().startsWith("FIX_")){
							content = arrContent[1].substring(4);
						}else{
							if(arrContent[1].equalsIgnoreCase("Port")){//组合
								content=getPortCode();
							}else if(arrContent[1].equalsIgnoreCase("Date")){//日期
								content=getDate();
							}else if(arrContent[1].equalsIgnoreCase("TradeSeat")){//席位
								content=getTradeSeatCode();
							}else if(arrContent[1].equalsIgnoreCase("Holder")){//股东
								content=getHolderCode();
							}else if(arrContent[1].equalsIgnoreCase("Assest")){//资产代码
								content=getAssestCode();
							}else{
								content="";
							}
						}
					}
					if (fileType.equalsIgnoreCase("S")) { // 字符型
						if(content.indexOf(",")>0){//如果是多个条件在一起，则采用in的处理
							buf.append(arrContent[0]).append(" in(").append(operSql.sqlCodes(content)).append(")").append(tmpRela);
						}else if(content.indexOf("%")>0){//如果是多个条件在一起，则采用like的处理
							buf.append(arrContent[0]).append(" like '").append(content).append("'").append(tmpRela);
						}else{
							buf.append(arrContent[0]).append("=").append(dbl.sqlString(content)).append(tmpRela);
						}
					} else if (fileType.equalsIgnoreCase("D")) {// 日期型
						if(content.trim().length()==0) continue;
						if(cusCfg.getFileType().equalsIgnoreCase("dbf")){
							buf.append(arrContent[0]).append("={^").append(YssFun.formatDate(YssFun.toDate(content),"yyyy-MM-dd")).append("}").append(tmpRela);
						}else if(cusCfg.getFileType().equalsIgnoreCase("mdb")){
							buf.append("format(").append(arrContent[0]).append(",'yyyy-MM-dd')=#").append(YssFun.formatDate(YssFun.toDate(content),"yyyy-MM-dd")).append("#").append(tmpRela);
						}else{
							//这里先处理 access与dbf文件类型的　其他文件类型先不处理
						}
					} else {
						if(content.trim().length()==0) continue;
						buf.append(arrContent[0]).append("=").append(content).append(YssFun.toNumber(tmpRela));
					}
				}//end for
				if(arrFilterContent.length>1) buf.append(")");
				buf.append(relation);
    		}//end while
    		fileFilter =buf.toString();
    		if(fileFilter.endsWith(" and ")){//去掉最后一个添加的" and "
    			fileFilter =fileFilter.substring(0,fileFilter.length()-5);
    		}
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage());
    	}finally{
    		dbl.closeResultSetFinal(rs);
    	}
    }
    /**
     * 获取组合的方法
     * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
     * @return
     * @throws YssException
     */
    public String getPortCode() throws YssException{
    	return "";
    }
    
    /**
     * 获取日期
     * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
     * @return
     * @throws YssException
     */
    public String getDate() throws YssException{
    	return "";
    }
    /**
     * 获取组合的交易席位代码 
     * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
     * @return
     * @throws YssException
     */
    public String getTradeSeatCode() throws YssException{
    	return "";
    }
    /**
     * 获取组合的股东代码
     * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
     * @return
     * @throws YssException
     */
    public String getHolderCode()throws YssException{
    	return "";
    }
    /**
     * 获取资产代码
     * by leeyu 20100225 QDII4.1赢时胜上海2010年02月10日01_AB MS00878
     * @return
     * @throws YssException
     */
    public String getAssestCode() throws YssException{
    	return "";
    }
}
