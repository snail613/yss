package com.yss.main.operdeal.datainterface.swift;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.yss.main.datainterface.swift.DaoSwiftEntitySet;
import com.yss.main.funsetting.VocabularyBean;
import com.yss.main.operdeal.datainterface.*;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssFun;

/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * <p>Title: 导出报文的基类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class BaseSwiftOutputOper
    extends BaseSwiftOper {
    protected int checkStateId=0;  	//审核状态
    protected YssSwiftRela yssSwiftRela=new YssSwiftRela();
    public BaseSwiftOutputOper() {
    }

	/**
	 * 获取关联的业务数据
	 * 
	 * @return String
	 * @throws YssException
	 */
	public String getLoadRelaTrade() throws YssException{
		return "";
	}

	public void parseReqsRow(String sResStr, String type) throws YssException {
		
    } 
    
    /**
     * 执行对应的审核数据功能
     * @return
     * @throws YssException
     */
    public String checkSetting() throws YssException{
    	ArrayList alCont= new ArrayList();
		SaveSwiftContentBean SwiftContent =null;
		String[] arrList = operDatas.split("\r\b");
		for(int i=0;i<arrList.length;i++){
			SwiftContent =new SaveSwiftContentBean();
			SwiftContent.parseRowStr(arrList[i],"out");
			alCont.add(SwiftContent);
		}
		this.saveSwift.audit(alCont);
		return "";//loadSWIFTListView();
    }
    
    /**
     * 删除报文数据的方法
     * @throws YssException
     */
    public void deleteSetting() throws YssException{
    	ArrayList alCont= new ArrayList();
		SaveSwiftContentBean SwiftContent =null;
		String[] arrList = operDatas.split("\r\b");
		for(int i=0;i<arrList.length;i++){
			SwiftContent =new SaveSwiftContentBean();
			SwiftContent.parseRowStr(arrList[i],"out");
			//this.saveSwift.delete(SwiftContent);
			SwiftContent.setCheckState(3);
			alCont.add(SwiftContent);
		}
		saveSwift.audit(alCont);
    }
    /**
     * 对导出SWIFT报文的后续工作处理
     * @return
     * @throws YssException
     */
    public abstract String exportSwiftDatas() throws YssException;
    
    /**
     * 生成SWIFT报文数据的功能 
     * @return
     * @throws YssException
     */
    public abstract String markAndExecute() throws YssException;
    
    /**
     * 生成撤消的报文数据
     * @return
     * @throws YssException
     */
    public abstract String markCancSwift() throws YssException;
	/**
	 * 初始化
	 * 
	 * @throws YssException
	 */
	public void initBean() throws YssException {
		// headerCode =
		// "FSwiftNum\tFSwiftType\tFDate\tFStatus\tFExeStatus\tFSwiftDesc";
		headerCode = "FTradeNum\tFDate\tFPortCode\tFTradeDesc\tFSwiftType\tFSwiftNum\tFStatus\tFExeStatus";
		headerName = "业务编号\t业务日期\t组合代码\t业务描述\t报文类型\t报文序号\t报文状态\t执行状态";
		rowData = new Object[8];
		saveSwift = new SaveSwiftContentBean();
		saveSwift.setYssPub(pub);
		baseOperDeal = new ImpCusInterface(); //这里用导入预处理 ，可能速度更快些
		baseOperDeal.setYssPub(pub);
		baseOperDeal.init(startDate, endDate, portCode, swiftType, "");
	}

	/**
	 * 返回一条Header列的报文信息
	 * 
	 * @return String
	 * @throws YssException
	 */
	public String buildRowStr() throws YssException {
		StringBuffer buf = new StringBuffer();
		buf.append(rowData[0]).append("\t");
		buf.append(rowData[1]).append("\t");
		buf.append(rowData[2]).append("\t");
		buf.append(rowData[3]).append("\t");
		buf.append(rowData[4]).append("\t");
		buf.append(rowData[5]).append("\t");
		buf.append(rowData[6]).append("\t");
		buf.append(rowData[7]).append("\t");
		// buf.append("\r\n");//行分隔符
		buf.append(checkStateId).append("\t");
		return buf.toString();
	}
	
	/**
	 * 初始化
	 */
	public String initSWIFTListView() throws YssException {
		String sVocStr = "";
		VocabularyBean vocabulary = new VocabularyBean();
		vocabulary.setYssPub(pub);
		sVocStr = vocabulary.getVoc(YssCons.YSS_SWIFT_CRITERION + ","
				+ YssCons.YSS_SWIFT_OPERTYPE);
		return headerName + "\r\f\r\f\r\f" + headerCode + "\r\f" + "voc"
				+ sVocStr;
	}
	
	/**
	 * 加载显示SWIFT报文,用于在前台界面上查询显示用
	 */
	public String loadSWIFTListView() throws YssException {
    	//String originalName = ""; //原文Swift文件名
        //String swiftName = ""; //导入的Swift文件名
        ResultSet rs = null;
        String sqlStr = "";
        StringBuffer bufResult = new StringBuffer(); //保存结果的 "业务编号\t业务日期\t组合代码\t业务描述\t报文类型\t报文序号\t报文状态\t执行状态";
        try {
            //1:根据条件获取所有流向为导入的SWIFT报文模板
            sqlStr = "select o.*,s.FSwiftDesc,s.FOperType,s.FOperTypeName,s.FCriterion,c.FCancelIndex from " + pub.yssGetTableName("Tb_Data_Originalswift") +
             	" o join (select b1.FSwiftCode,b1.FSwiftDesc,b1.FSwiftStatus,b1.FReflow,"+
             	//--------- add by jiangshichao MT1 和 MT2 类型的撤销特殊处理
             	" case when b1.fswiftstatus='CANC' AND substr(b1.fswifttype,0,3)='MT1' then 'MT192' when b1.fswiftstatus='CANC' AND substr(b1.fswifttype,0,3)='MT2'"+
                " then 'MT292' else b1.FSwiftType end as fswifttype,"+
                //----------------------------------------------------------
             	" b1.FOperType,b1.FCriterion,b2.FVocName as FOperTypeName from " +//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                pub.yssGetTableName("Tb_Dao_Swift") + " b1 left join (select FVocCode, FVocName from Tb_Fun_Vocabulary where FVocTypeCode = '" +
                YssCons.YSS_SWIFT_OPERTYPE + "') b2 " +
                " on b1.FOperType = b2.FVocCode) s on o.Fswifttype = s.FSwifttype and o.FSwiftStatus=s.FSwiftStatus and s.FReflow=o.FReflow "+//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                " left join (select case when "+dbl.sqlInstr("FSwiftIndex", "'|'")+">0 then "+dbl.sqlSubStr("FSwiftIndex", dbl.sqlInstr("FSwiftIndex", "'|'")+"+6")+
                " end as FSwiftIndex,FSwiftIndex as FCancelIndex,FSwiftType,FReflow,FDate,FPortCode,FSwiftCode  from "+pub.yssGetTableName("Tb_Data_Originalswift")+" where FCheckState<>3 )c "+//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                " on o.FReflow=c.FReflow and o.FDate=o.FDate and o.FPortCode=c.FPortCode and o.FSwiftIndex=c.FSwiftIndex "+
                " where o.FReflow = 'out' and o.FCheckState<>3 " +
                (startDate!=null && !YssFun.formatDate(startDate).equalsIgnoreCase("9998-12-31")&&!YssFun.formatDate(startDate).equalsIgnoreCase("1900-01-01")?" and o.FDate ="+dbl.sqlDate(startDate):"")+
                //(swiftReflow != null && swiftReflow.length() > 0 ? " and s.FReflow =" + dbl.sqlString(swiftReflow) : "") +//by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
                ( (swiftOperType != null && swiftOperType.length() > 0 && !swiftOperType.equalsIgnoreCase("99")) ? " and s.FOperType=" + dbl.sqlString(swiftOperType) : "") +
                ( (swiftStandard != null && swiftStandard.length() > 0 && !swiftStandard.equalsIgnoreCase("99")) ? " and s.FCriterion=" + dbl.sqlString(swiftStandard) : "") +
                (swiftType != null && swiftType.length() > 0 ? " and s.FSwiftType=" + dbl.sqlString(swiftType) : "")+
                " order by o.FSwiftType,o.FSwiftStatus";
            rs = dbl.openResultSet(sqlStr);
            while (rs.next()) {
                //swiftName = getFileNameStr(rs.getString("FSwiftType"),rs.getString("FSwiftStatus"));
                //originalName = getOriginalSWIFTName(rs.getString("FSwiftType"),rs.getString("FSwiftStatus"));
                rowData[0] = rs.getString("FRelaNum"); //业务编号
                rowData[1] = YssFun.formatDate(rs.getDate("FDate"), "yyyy-MM-dd"); //业务日期
                rowData[2] = rs.getString("FPortCode"); //组合代码
                rowData[3] = rs.getString("FSwiftDesc"); //业务描述
                rowData[4] = rs.getString("FSwiftType"); //报文类型                
                rowData[5] = rs.getString("FSwiftNum"); //报文序号
                rowData[6] = rs.getString("FSwiftStatus").equalsIgnoreCase("CANC")?"已撤消":"正常";//报文状态
                rowData[7] = rs.getString("FExcuteTag")==null||rs.getString("FExcuteTag").equalsIgnoreCase("0")?"未导出":"已导出";//执行状态
                checkStateId = rs.getInt("FCheckState");
                bufResult.append(this.buildRowStr()).append("\r\f"); //0:单行数据信息
                bufResult.append(rs.getString("FOperType")).append("\t");
                bufResult.append(rs.getString("FOperTypeName")).append("\r\f"); //1:分组信息，这里取业务类型数据
                bufResult.append(rs.getString("FFullFileName")).append("\r\f"); //2:文件名信息
                bufResult.append(dbl.clobStrValue(rs.getClob("FText")).replaceAll("\t", "  ")).append("\r\f"); //3:报文原文数据
                bufResult.append(rs.getString("FCriterion")).append("\t"); //4:其他项，包括：报文标准,关联编号 ,报文序号,报文状态,执行状态,业务类型
                bufResult.append(rs.getString("FRelaNum")).append("\t");
                bufResult.append(rs.getString("FCancelIndex")==null?rs.getString("FSwiftIndex"):rs.getString("FCancelIndex")).append("\t");
                bufResult.append(rs.getString("FSwiftStatus")).append("\t");
                bufResult.append(rs.getString("FExcuteTag")==null?"":rs.getString("FExcuteTag")).append("\t");
                bufResult.append(rs.getString("FOpertype")).append("\t");
                bufResult.append(rs.getString("FSwiftCode"));//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
                bufResult.append("\r\b\n");
            }
            if (bufResult.length() > 3) {
                bufResult.setLength(bufResult.length() - 3);
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage(), ex);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return bufResult.toString();
    }
	
	
	//-------MS00983 QDV4赢时胜（深圳）2010年02月08日01_A Swift接口导入导出文件相关需求 ----------------------------------------------
	public String sendStatus() throws YssException{
		String filePath="";
		String status = "";//收发状态
		ResultSet rs = null;
	    String sqlStr = " select a.fpath as fpath from (select * from "+pub.yssGetTableName("Tb_Dao_Swift")+" )a right join"+
	                    " (select FSWIFTCODE from "+pub.yssGetTableName("Tb_data_originalswift")+" where freflow='out' and fswiftstatus='NEW' and frelanum='"+this.sRelanum+"')b"+
	                    " on a.FSWIFTCODE = b.FSWIFTCODE";
	    try {
			rs = dbl.openResultSet(sqlStr);
			  if (rs.next()) {
		        	filePath = rs.getString("fpath");
		        }else {
		        	 throw new YssException("请核对报文导出路径是否已设置！！！");
		        }
			  if(filePath.lastIndexOf("emission")>0){
				  filePath = filePath.split("emission")[0];
			  }else{
				  throw new YssException("请将【报文代码为："+saveSwift.getSwiftCode()+"】的文件导出路径设置成SWIFT客户端安装目录下的emission目录");
			  }
			  
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//String filePath = "C:\\Program Files\\SWIFT\\Alliance Lite\\files\\";//Swift终端安装默认路径
		String fileName = this.swiftType+"_"+this.sRelanum.substring(1)+".fin";//报文文件名

		boolean error_Exists = (new File(filePath+"error\\"+""+fileName)).exists();
		boolean reception_Exists = (new File(filePath+"reception\\"+""+fileName)).exists();
	    boolean emission_Exists = (new File(filePath+"emission\\"+""+fileName)).exists();
		/**********************************************************************************
		 * 1. 在 error 目录有匹配文件   
		 *       文件错误
		 * 2. 在 reception 目录有匹配文件
		 *       文件有405标识  --- 内容错误
		 *       文件无405标识  --- 正确发送
		 * 3. 在 reception 目录没有匹配文件
		 *       emission 有匹配文件  等待发送
		 *                无                      正在发送
		 */
		try {
			if (error_Exists) {
				status = "【" + fileName + "】无法发出的错误文件！！";
			} else if (reception_Exists) {
				String content = readSwfitConten(filePath + "reception\\" + ""+ fileName);
                String[] arr = content.split("405:"); 
                if(arr.length>1){
                	status = "【" + fileName + "】报文内容有误！！";
                }else {
                	status = "【" + fileName + "】已正确发送！！";
                }
			} else if (emission_Exists) {
				status = "【" + fileName + "】等待发送！！";
			} else {
				status = "【" + fileName + "】正在发送！！";
			}
		} catch (IOException e) {

			e.printStackTrace();
		}finally{
			dbl.closeResultSetFinal(rs);
		}
		return status;
	}
	
	public  String readSwfitConten(String fileName) throws IOException {
		  StringBuffer sb = new StringBuffer();
		  BufferedReader in = new BufferedReader(new FileReader(fileName));
		  String s;
		  while((s = in.readLine()) != null) {
		    sb.append(s);
		    sb.append("\n");
		  }
		  in.close();
		  return sb.toString();
		 }
	//-------MS00983 QDV4赢时胜（深圳）2010年02月08日01_A Swift接口导入导出文件相关需求    end----------------------------------------------//
	
    /*----------------其他功能------------*/
	/**
	 * 设置报文编号
	 * @param entity
	 * @param swiftRela
	 */
	public abstract DaoSwiftEntitySet setSWIFTNum(DaoSwiftEntitySet entity,YssSwiftRela swiftRela) throws YssException;
    
    /**
     * 定义一个内部类,用于处理SWIFT的报文序号问题
     * @author liyu
     *
     */
    class YssSwiftRela{
    	private YssSwiftRela(){    		
    	}
    	private String swiftNewIndex=""; //SWIFT新增编号
    	private String swiftRefIndex=""; //SWIFT关联编号 暂无用
    	private String swiftStatus ="";  //SWIFT操作状态
    	
    	private boolean bContinue=false; 
    	public String getSwiftNewIndex() {
			return swiftNewIndex;
		}
		public void setSwiftNewIndex(String swiftNewTIndex) {
			this.swiftNewIndex = swiftNewTIndex;
		}		
		public String getSwiftRefIndex() {
			return swiftRefIndex;
		}
		public void setSwiftRefIndex(String swiftRelaIndex) {
			this.swiftRefIndex = swiftRelaIndex;
		}
		public boolean isbContinue() {
			return bContinue;
		}
		public void setbContinue(boolean bContinue) {
			this.bContinue = bContinue;
		}
		public String getSwiftStatus() {
			return swiftStatus;
		}
		public void setSwiftStatus(String swiftStatus) {
			this.swiftStatus = swiftStatus;
		}		
    }

}
