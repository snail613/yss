package com.yss.main.operdeal.datainterface.function;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import com.yss.main.operdeal.datainterface.*;
import com.yss.util.*;

/**
 * 根据条件查表是否有结果集
 * 解释：根据参数中的条件来查表，最后根据是否有结果集来提示
 * 公式名称：CheckResult
 * 公式参数1：查询语句
 * 公式参数2：提示方式
 * ----------杂项参数-----------
 * 公式参数3：提示后是否继续
 * 公式参数4：对话框提示类型
 * 公式参数5：提示信息
 */
public class CheckResultFunctionBean
    extends BaseFunction {
    public CheckResultFunctionBean() {
    }

    public String FormulaFunctions() throws YssException {
        String sResult = "";
        try {
            if (PromtSource.getParams().length == 5) {
                message.setSMessageType(PromtSource.getParams()[3].split(",")[0]);
                message.setSContinue( (PromtSource.getParams()[2].split(",")[0]).equals("1") ? "true" : "false");
                message.setSResult(PromtSource.getParams()[4]);
                sResult = FormulaFunctions5(PromtSource.getParams());
            }else if(PromtSource.getParams().length == 6){//story 1536 add by zhouwei 20111013 设置按钮类型 如取值 Yes:是、YesNo:是与否、OK：确定、RetryCancel:重试取消、OKCancel:确定取消
            	 message.setSMessageType(PromtSource.getParams()[3].split(",")[0]);
                 message.setSContinue( (PromtSource.getParams()[2].split(",")[0]).equals("1") ? "true" : "false");
                 message.setSResult(PromtSource.getParams()[4]);
                 message.setSButtons(PromtSource.getParams()[5]);
                 sResult = FormulaFunctions5(PromtSource.getParams());
            }else if(PromtSource.getParams().length == 7){//story 1566 add by zhouwei 20111019 第七个参数，代表要输出到客户端的字段信息
	           	 message.setSMessageType(PromtSource.getParams()[3].split(",")[0]);
	             message.setSContinue( (PromtSource.getParams()[2].split(",")[0]).equals("1") ? "true" : "false");
	             message.setSResult(PromtSource.getParams()[4]);
	             message.setSButtons(PromtSource.getParams()[5]);
	             sResult = FormulaFunctions5(PromtSource.getParams());
            }else {
                //重载参数的方法的处理
            }
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        }
        return sResult;
    }

    private String FormulaFunctions5(String[] arrParams) throws YssException {
        String sqlStr = "";
        ResultSet rs = null;
        boolean isMsg = false; //add by huangqirong 2013-07-30 Story #4255
        try {
            sqlStr = arrParams[0];
            if (baseOper instanceof ImpCusInterface) {
                sqlStr = ( (ImpCusInterface) baseOper).buildDsSql(sqlStr);
            }
            //add by zhouwei 20120110 导出提示源的处理方式
            if (baseOper instanceof ExpCusInterface) {
                sqlStr = ( (ExpCusInterface) baseOper).buildDsSql(sqlStr);
            }
            //---------------end----------
            rs = dbl.openResultSet(sqlStr);
			//start modify huangqirong 2013-07-30 story #4255 
            while(rs.next()) { //实际查出结果来的值
                if (arrParams[1].split(",")[0].equals("0")) { //若参数要求不查出结果集返回提示的
                    message.setBShow(false); //当参数设置为显示无结果集提示，此时不向前台提示
                } else {
                    message.setBShow(true);
                }
              //story 1566 add by zhouwei 20111019 第七个参数，代表要输出到客户端的字段信息
              if(arrParams.length==7){
            	  getResultByParam(rs, arrParams[6]);
              }
              isMsg = true;
            }            
            if(!isMsg){//  else {
                if (arrParams[1].split(",")[0].equals("0")) { //若参数要求不查出结果集返回提示的
                    message.setBShow(true); //当参数设置为显示无结果集提示，此时要向前台提示
                /**add---huhuichao 2013-8-9 STORY  4276 博时：跨境ETF补充增加一类公司行动 */
				} else if (arrParams.length == 7) {
					if (arrParams[6]
							.equalsIgnoreCase("securitiesid,holding,marketvalueinlocal")) {
						message.setBShow(true);
					}
                    /**add---huhuichao 2013-9-4 STORY 4494    关于QDII针对股票蓝估值提示需求 */
					else{
					message.setBShow(false);
					}
					/**end---huhuichao 2013-9-4 STORY 4494  */
				}
                /**end---huhuichao 2013-8-9 STORY  4276*/
                else {
                    message.setBShow(false);
                }
            }
            //end modify huangqirong 2013-07-30 story #4255
        } catch (Exception ex) {
            throw new YssException(ex.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return message.buildRowStrToPretreat();//story 1536 update by zhouwei 20111013
    }
  //story 1566 add by zhouwei 20111019 根据参数，取出结果集中数值，赋值给输出结果
    private void getResultByParam(ResultSet rs,String paramStr) throws SQLException{
    	/**add---huhuichao 2013-8-9 STORY  4297 H股ETF帐实核对脚本配置*/
    	String messageStr = null;
		if (paramStr
				.equalsIgnoreCase("securitiesid,holding,marketvalueinlocal")
				&& message.getSResult().equalsIgnoreCase("核对一致！是否导入数据？")) {
			messageStr = "核对不一致！是否导入数据？"+"\n"+"\n"+"差异数据如下：" + "\n"+"\n"+"证券ISIN代码"+"\t\t"+
			               "数量"+"\t\t"+"市值"+"\n";
		}
    	/**start modify by huangqirong 2013-7-30 Story #link  */
		else {messageStr=message.getSResult().endsWith("\n") ? message.getSResult() : message.getSResult()+"\n";}
		/**end modify by huangqirong 2013-7-30 Story #link*/   
    	/**end---huhuichao 2013-8-9 STORY  4297*/
    	String[] params=paramStr.split(",");
    	//Map fieldTypeMap=getMapOfFieldType(rs);
    	for(int i=0;i<params.length;i++){//获取第一条记录
    		String param=params[i];
    		if(param!=null){
    			if(dbl.isFieldExist(rs, param.trim())){//判断参数是否是数据源中存在的字段
    				if(i==params.length-1){
    					messageStr+=rs.getString(param.trim())+"\n";	
    				}else{
    					messageStr+=rs.getString(param.trim())+"\t\t";
    				}   				
    			}
    		}
    	}
    	/*
    	 * start modify huangqirong 2013-07-30 story #4255
    	while(rs.next()){
    		for(int i=0;i<params.length;i++){//获取第一条记录
        		String param=params[i];
        		if(param!=null){
        			if(dbl.isFieldExist(rs, param.trim())){//判断参数是否是数据源中存在的字段
        				if(i==params.length-1){
        					messageStr+=rs.getString(param.trim())+"\n";	
        				}else{
        					messageStr+=rs.getString(param.trim())+"\t\t";
        				}   				
        			}
        		}
        	}
    	}
    	end modify huangqirong 2013-07-30 story #4255
    	*/
    	message.setSResult(messageStr);
    }
    //获取字段名称，类型映射关系
    private Map getMapOfFieldType(ResultSet rs) throws SQLException{
    	Map fieldTypeMap=new HashMap();
    	ResultSetMetaData rsmd=rs.getMetaData();
    	int count=rsmd.getColumnCount();
    	for(int i=0;i<count;i++){
    		fieldTypeMap.put(rsmd.getColumnName(i).toUpperCase().trim(), rsmd.getColumnTypeName(i).toUpperCase().trim());
    	}
    	return fieldTypeMap;
    }
}
