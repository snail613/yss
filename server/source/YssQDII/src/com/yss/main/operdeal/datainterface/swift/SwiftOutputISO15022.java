package com.yss.main.operdeal.datainterface.swift;

import java.sql.*;
import java.util.*;
import com.yss.util.*;
import com.yss.main.dao.IDataSetting;
import com.yss.main.datainterface.swift.*;



/**
 * QDV4赢时胜（深圳）2009年5月12日01_A MS00455
 * by leeyu 20090610
 * <p>Title: 导出ISO15022类报文的处理类</p>
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
public class SwiftOutputISO15022
    extends BaseSwiftOutputOper {
	private ArrayList alCont =null;
    private String sign=":,!,[,],/,n,c,a,*,x,d,z";
    private int iTotalLen=0; //总单条SWIFT总长度
    public static ArrayList tradeNumPool = null; //编号池，存放一组待使用的编号
   
    
    public SwiftOutputISO15022() {
    }

    public IDataSetting parseEntityStr(Object objData) throws YssException {    	
    	//根据ISO15022标准，解析数据，并到 DaoSwiftEntitySet中
        String sData = (String) objData;
        DaoSwiftEntitySet swiftEntityData = new DaoSwiftEntitySet(); //解析后存放值的
        //Do parse something
        String[] arrFormat = sData.split(":");
        if (swiftType.toUpperCase().startsWith("MT5")) { //MT5 类型的解析
            if (arrFormat.length == 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                if (arrFormat[2].length() == 0) {
                    if (arrFormat[3].indexOf("/") > -1) {
                        swiftEntityData.setSQualifier(arrFormat[3].substring(0, arrFormat[3].indexOf("/")));
                        swiftEntityData.setSContent(arrFormat[3].substring(arrFormat[3].indexOf("/")));
                    } else {
                        swiftEntityData.setSQualifier("");
                        swiftEntityData.setSContent(arrFormat[3]);
                    }
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else if (swiftType.toUpperCase().startsWith("MT3")) { //MT3 类型的解析
            if (arrFormat.length == 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                if (arrFormat[2].length() == 0) {
                    swiftEntityData.setSContent(arrFormat[3]);
                    swiftEntityData.setSQualifier("");
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else if (swiftType.toUpperCase().startsWith("MT9")) { //MT9 类型的解析
            if (arrFormat.length == 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                if (arrFormat[2].length() == 0) {
                    swiftEntityData.setSContent(arrFormat[3]);
                    swiftEntityData.setSQualifier("");
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else if (swiftType.toUpperCase().startsWith("MT1")) { //MT1 类型的解析
            if (arrFormat.length == 4) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                if (arrFormat[2].length() == 0) {
                    swiftEntityData.setSContent(arrFormat[3]);
                    swiftEntityData.setSQualifier("");
                } else {
                    swiftEntityData.setSQualifier(arrFormat[2]);
                    swiftEntityData.setSContent(arrFormat[3]);
                }
            } else if (arrFormat.length == 3) { //例 :20C::abc/234,34  :20C:ABCD:abc/234,34
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSContent(arrFormat[2]);
                swiftEntityData.setSQualifier("");
            } else if (arrFormat.length == 2) {
                swiftEntityData.setSTag(arrFormat[1]);
                swiftEntityData.setSQualifier("");
                swiftEntityData.setSContent("");
            }
        } else {

        }
        return swiftEntityData;
    }

    public String buildEntityStr(IDataSetting entity, Object objData) throws
        YssException {
    	String[] arrObj=null;
        int iConIndex=0;   //定义属于第几个开始顺序
        String startStr=""; //取SWIFT标识符的选项值
        StringBuffer buf = new StringBuffer();
        if (entity == null)
            return "";
        DaoSwiftEntitySet daoEntity =(DaoSwiftEntitySet)entity;
        if(!(daoEntity.getSStatus().equalsIgnoreCase("M") || daoEntity.getSStatus().equalsIgnoreCase("O")))
            return "";
        buf.append(daoEntity.getSContent() == null?"":(daoEntity.getSContent().equalsIgnoreCase("/")?"":daoEntity.getSContent().trim()));//将内容添加进来
        startStr=setContent(daoEntity);
        if(startStr.startsWith(":"))
            buf.append(":");
        buf.append(daoEntity.getSQualifier()==null?"":daoEntity.getSQualifier().trim());
        if (objData == null|| String.valueOf(objData).length()==0)//当值为空或是值的长度为0时返回模板相关的数据
            return ":"+daoEntity.getSTag()+":"+buf.toString();
        iTotalLen = (daoEntity.getSQualifier()==null?"":daoEntity.getSQualifier().trim()).length();
        iTotalLen += (daoEntity.getSContent() == null?"":daoEntity.getSContent().trim()).length();
        parseCon(daoEntity.getSOption());
        arrObj =String.valueOf(objData).split(",");//采用逗号分隔的分隔方式
        String sCont="";
        for(int i=0;i<alCont.size();i++){
            sCont =(String)alCont.get(i);
            if(sCont!=null&& sCont.trim().length()>0){
                if(sCont.equalsIgnoreCase("/")|| sCont.equalsIgnoreCase(":")){
                    buf.append(sCont);
                    iTotalLen+=sCont.length();
                }else if(sCont.equalsIgnoreCase("\\[") || sCont.equalsIgnoreCase("\\]")){//如果是［］号则不加
                    buf.append("");
                }else{
                    if (sCont.equalsIgnoreCase("n") || sCont.equalsIgnoreCase("c") || sCont.equalsIgnoreCase("a")
                        || sCont.equalsIgnoreCase("x") || sCont.equalsIgnoreCase("d") ||sCont.equalsIgnoreCase("z")) {
                        String sNum="";   //定义数值
                        String limitr=""; //定义限制符
                        String choise=""; //选择符
                        String type="";   //定义类型
                        String value="";  //定义值
                        type = sCont;
                        sNum = getPrevious(alCont, i);
                        if((i+1)<alCont.size())
                            choise = String.valueOf(alCont.get(i+1));
                        if (sNum.equalsIgnoreCase("!")) { //当获取的为限制符时
                            limitr = sNum;
                            sNum = getPrevious(alCont, i - 1); //在获取限制前一位的数据，此数据为数据
                        }
                        if(sCont.equalsIgnoreCase("x")){//较特殊
                            limitr="*";
                            if(getPrevious(alCont, i - 1).equalsIgnoreCase("*"))
                                sNum = getPrevious(alCont, i - 2)+ "*" +sNum;
                        }
                        if(choise.equalsIgnoreCase("\\]")){
                            if (arrObj.length <= iConIndex || arrObj[iConIndex].length() == 0){
                                iConIndex++; //当为此条件时说明为一组表达式，故计数器加1
                                continue;
                            }
                        }
                        buf.append(getValue(sNum,limitr,type,arrObj[iConIndex]));
                        iConIndex++; //当为此条件时说明为一组表达式，故计数器加1
                    }else{
                        buf.append("");
                    }
                }//end esle
            }
        }
        //最后再判断一下总长度，为防止长度比预想的长，此种情况一般是不会出现的。
        if(buf.length()>iTotalLen)
            buf.setLength(iTotalLen);
        return ":"+daoEntity.getSTag()+":"+buf.toString();
    }
	
    /**
     * 导出报文数据时用此方法
     */
	public String exportSwiftDatas() throws YssException {
		DaoSwiftOutInfo dwoi=new DaoSwiftOutInfo();
		try{
			dwoi.setYssPub(pub);
			saveSwift.parseRowStr(this.operDatas, "out");
//			dwoi.setSFSwiftType(saveSwift.getSwiftType());
//			dwoi.setSSwiftStatus(saveSwift.getSwiftStatus());
//			dwoi.setsSwiftCode(saveSwift.getSwiftCode());//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
			setDaoSwiftOutInfoAttr(dwoi);
 			this.saveSwift.updateFExcuteTag();
// 			dwoi.getSetting();
// 			dwoi.setSSwiftIndex(saveSwift.getWwiftIndex());//将报文序号传进去 20100104
// 			dwoi.setsSwiftDesc(saveSwift.getSwiftDesc()); //将报文备注信息添加进去　20100104
			return dwoi.getHeadEndInfo();
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}
	}
	
	// add by jiangshichao 2010.02.28
	public void setDaoSwiftOutInfoAttr(DaoSwiftOutInfo dwoi) throws YssException{
		dwoi.setSFSwiftType(saveSwift.getSwiftType());
		dwoi.setSSwiftStatus(saveSwift.getSwiftStatus());
		dwoi.setsSwiftCode(saveSwift.getSwiftCode());//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
		dwoi.getSetting();
		dwoi.setSSwiftIndex(saveSwift.getWwiftIndex());//将报文序号传进去 20100104
		dwoi.setsSwiftDesc(saveSwift.getSwiftDesc()); //将报文备注信息添加进去　20100104
		dwoi.setBIC(saveSwift.getRelaNum(), saveSwift.getOperType(),saveSwift.getSwiftDate());
	}
	
	
	
	public DaoSwiftEntitySet setSWIFTNum(DaoSwiftEntitySet entity,YssSwiftRela swiftRela) throws YssException{
		DaoSwiftEntitySet entityCone =null;
		try{
    		entityCone =(DaoSwiftEntitySet)entity.clone();
    		if(entityCone.getSContent().equalsIgnoreCase("<NewIndex>")){    				
    			entityCone.setSContent(swiftRela.getSwiftNewIndex());
    			if(entityCone.getSQualifier().trim().length()>0)
    				entityCone.setSContent("//"+entityCone.getSContent()); //这里采用固定写法,先写死在这里
    		}else if(entityCone.getSContent().equalsIgnoreCase("<RefIndex>")){
    			entityCone.setSContent(swiftRela.getSwiftRefIndex());
    			if(entityCone.getSQualifier().trim().length()>0)
    				entityCone.setSContent("//"+entityCone.getSContent()); //这里采用固定写法,先写死在这里
    		}
    		if(swiftRela.getSwiftStatus().equalsIgnoreCase("CANC")){
    			if(entityCone.getSContent().equalsIgnoreCase("NEWT")||entityCone.getSContent().equalsIgnoreCase("NEWM")){
    				entityCone.setSContent("CANC");
    			}
    		}
    	}catch(Exception ex){
    		throw new YssException("处理SWIFT序号出错",ex);
    	}
    	return entityCone;
    }
	
	

	/**
	 * 生成撤消的报文方法
	 */
	public String markCancSwift() throws YssException{
		SaveSwiftContentBean swiftContent =new SaveSwiftContentBean();
		try{
			swiftContent.setYssPub(pub);
			swiftContent.parseRowStr(operDatas, "out");
			swiftContent.update();
			
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}
		return "";
	}
	/**
	 * 生成报文的过程，此方法为报文导出的关键方法 
	 * by leeyu 20091111 光棍节日完成
	 */
	public String markAndExecute() throws YssException {
		//１：先查询报文设置表的全部导出报文模板
		//２：执行这些模板中的数据源，将生成的数据插入到报文数据表中
		String sqlStr="";
		ResultSet rs =null;
		ResultSet rsDs=null;
		Connection conn =null;
		boolean bTrans =false;
		boolean flag = false;
		ArrayList alCont=new ArrayList();
		SaveSwiftContentBean swiftContentBean =null;
		String swiftText="";
		try{
			HashMap hmNum=new HashMap();
			conn =dbl.loadConnection();
			conn.setAutoCommit(bTrans);
			bTrans =true;
			//if(!swiftOperType.equalsIgnoreCase("")){ //STORY #2475 根据FindBugs工具，对系统进行全面检查  zhangjun
				
			//}
			sqlStr="select * from "+pub.yssGetTableName("Tb_Dao_Swift")+
			       " where FReflow='out' and FCheckState=1 and FSwiftStatus='NEW' "+
			      (swiftOperType.equalsIgnoreCase("")?"":" and fopertype = '"+swiftOperType+"'")+
			      (swiftStandard.equalsIgnoreCase("")?"":" and fcriterion = '"+swiftStandard+"'");//add by jiangshichao 2010.02.28
			rs =dbl.openResultSet(sqlStr);
			while(rs.next()){
				//0:首先删除最终临时表的数据
				if(dbl.yssTableExist(rs.getString("FTableCode")))
					deleteSWIFTTab(rs.getString("FTableCode"));
				String[] arrDs =null;
				//1:先将数据插入到临时表中。
				if(rs.getString("FDSCode")!=null){
					arrDs = rs.getString("FDSCode").split(",");
					for (int i = 0; i < arrDs.length; i++){
						if(arrDs[i].trim().length()>0)
							baseOperDeal.doOnePretreat(arrDs[i]);
					}
				}
				swiftType=rs.getString("FSwiftType");
				//2:查询报文模板中的表，取出表中的数据，并保存到报文数据表中
				/************************************************************
				 * modify by jiangshichao 2010.02.21 
				 * 如果该笔业务对应的报文已存在，且审核状态为已审核、报文状态为正常的报文不可重新再生成。
			     * 对审核状态为已审核、报文状态为已撤销的报文可重新产生，但需要给出提示信，以提示操作人员该业务已存在撤销报文。
			     * 对未审核的报文可以任意重新生成。
			     */
	
//				sqlStr="select * from "+rs.getString("FTableCode");
         		sqlStr = "select a.*, nvl(freflow,'out')as freflow,nvl(fcheckstate,0) as fcheckstate,nvl(fswiftstatus,'NEW') as fswiftstatus from "
         			     +rs.getString("FTableCode")+ " a left join  ( select * from "+pub.yssGetTableName("tb_data_originalswift")+" where fcheckstate<>3) b on a.ftradenum = b.frelanum";
				rsDs = dbl.openResultSet(sqlStr);
				HashMap hmType =dbFun.getFieldsType(rsDs);
				while(rsDs.next()){	
					//如果是已审核的正常报文不再重新生成
					if(!rsDs.getString("fcheckstate").equalsIgnoreCase("1")){
					   swiftContentBean =new SaveSwiftContentBean();
					   swiftContentBean.setYssPub(pub);
					   swiftContentBean.setSwiftCode(rs.getString("FSwiftCode"));//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
					   swiftContentBean.setSwiftDate(YssFun.formatDate(startDate,"yyyy-MM-dd"));//swift 日期
				       swiftContentBean.setFileName(getFileNameStr(rs.getString("FSwiftCode")));//文件全名//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
					   swiftContentBean.setSwiftType(rs.getString("FSwiftType"));//swift 类型
					   
					   if(hmNum.get(rs.getString("FSwiftType"))==null){
						   hmNum.put(rs.getString("FSwiftType"), swiftContentBean.getMaxNum());
						   swiftContentBean.setSwiftNum("EXP"+rs.getString("FSwiftType")+YssFun.formatDate(startDate,"yyyyMMdd")+
								                        String.valueOf(hmNum.get(rs.getString("FSwiftType"))));//swift 编号
					   }else{
						   hmNum.put(rs.getString("FSwifttype"),YssFun.formatNumber(YssFun.toInt(String.valueOf(hmNum.get(rs.getString("FSwiftType"))))+1,"0000000"));
						   swiftContentBean.setSwiftNum("EXP"+rs.getString("FSwiftType")+YssFun.formatDate(startDate,"yyyyMMdd")+
								                        String.valueOf(hmNum.get(rs.getString("FSwiftType"))));//swift 编号						
					   }	
					   
					   swiftContentBean.setSwiftIndex(swiftContentBean.getSwiftNum().substring(16,swiftContentBean.getSwiftNum().length()));//swift 序号
					   swiftContentBean.setSwiftStatus("NEW");//swift 状态
					   swiftContentBean.setRelaNum(rsDs.getString("FTradeNum"));//关联编号,这个编号需要固定					
					   swiftContentBean.setPortCode((portCode==null||portCode.length()==0)?" ":portCode);//组合代码					
					   swiftContentBean.setReflow("out");//swift 流向
					   swiftContentBean.setCheckState(0);//swift 审核状态
					   //生成原报文
					   yssSwiftRela.setSwiftNewIndex(swiftContentBean.getWwiftIndex());//添加新编号
					   yssSwiftRela.setSwiftRefIndex("");//关联编号设置为空
				       yssSwiftRela.setSwiftStatus("NEW");

					   swiftText=buildSwiftText(getSwiftTemp(rs.getString("FSwiftType"),rs.getString("FSwiftStatus"),rs.getString("FSwiftCode")),rsDs,hmType,yssSwiftRela);
					   swiftContentBean.setSwiftText(swiftText);//swift 报文内容					
					   alCont.add(swiftContentBean);	
					}
					
					/*******************************************************
					 * 生成撤销报文的情况：
					 * 1. 第一次生成报文时，会生成撤销报文，审核状态是未激活
					 * 2. 勾选了已审核已撤销报文时，会重新生成，但是不会改变审核状态
					 */
					 if(tradeNums.containsKey(rsDs.getString("ftradenum"))){
						flag= true;
					 }
					if((rsDs.getString("fcheckstate").equalsIgnoreCase("0"))||
					   (rsDs.getString("fcheckstate").equalsIgnoreCase("1")&&rsDs.getString("FSWIFTSTATUS").equalsIgnoreCase("CANC")&&flag)){
					   //3:再同时生成一条撤消的报文,并将报文的审核状态置3
					   SaveSwiftContentBean saveSwiftCancBean = new SaveSwiftContentBean();
					   swiftContentBean.setYssPub(pub);
					   //by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
					   DaoSwiftSet daoCalc =new DaoSwiftSet();
					   daoCalc.setYssPub(pub);
					   
					  // saveSwiftCancBean.setSwiftCode(rs.getString("FSwiftCode"));//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
					   saveSwiftCancBean.setSwiftDate(YssFun.formatDate(startDate,"yyyy-MM-dd"));//swift 日期
					   saveSwiftCancBean.setFileName(getFileNameStr(rs.getString("FSwiftCode")));//文件全名//by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
					   saveSwiftCancBean.setSwiftType(rs.getString("FSwiftType"));//swift 类型
					   saveSwiftCancBean.setRelaNum(rsDs.getString("FTradeNum"));//关联编号,这个编号需要固定					
					   saveSwiftCancBean.setPortCode((portCode==null||portCode.length()==0)?" ":portCode);//组合代码					
					   saveSwiftCancBean.setReflow("out");//swift 流向
					   
					   daoCalc.setSwiftType(rs.getString("FSwiftType"));
					   daoCalc.setSSwiftStatus("CANC");
					   
					   
					   if(swiftType.startsWith("MT1") || swiftType.startsWith("MT2")){ //如果是MT1类与MT2类的话，则用新增的那组模板
						  daoCalc.setSSwiftStatus("NEW");
					    }
					   daoCalc.getSetting();
					   
					   
					   saveSwiftCancBean.setSwiftCode(daoCalc.getsWiftCode());
					   
					   if(swiftType.startsWith("MT1") || swiftType.startsWith("MT2")){ //如果是MT1类与MT2类的话，则用新增的那组模板
						  saveSwiftCancBean.setSwiftType(swiftType.substring(0,3)+"92");
					   }
					   //by leeyu 20091217 修改 QDV4赢时胜上海2009年12月17日07_B MS00874
				       yssSwiftRela.setSwiftRefIndex(yssSwiftRela.getSwiftNewIndex());
				       
					   if(hmNum.get(rs.getString("FSwiftType"))==null){
						   hmNum.put(rs.getString("FSwiftType"), saveSwiftCancBean.getMaxNum());
						   saveSwiftCancBean.setSwiftNum("EXP"+rs.getString("FSwiftType")+YssFun.formatDate(startDate,"yyyyMMdd")+
								                          String.valueOf(hmNum.get(rs.getString("FSwiftType"))));//swift 编号
					   }else{
						   hmNum.put(rs.getString("FSwifttype"),YssFun.formatNumber(YssFun.toInt(String.valueOf(hmNum.get(rs.getString("FSwiftType"))))+1,"0000000"));
						   saveSwiftCancBean.setSwiftNum("EXP"+rs.getString("FSwiftType")+ YssFun.formatDate(startDate,"yyyyMMdd")+
								                         String.valueOf(hmNum.get(rs.getString("FSwiftType"))));//swift 编号
					   }
					   
					   saveSwiftCancBean.setSwiftIndex(saveSwiftCancBean.getSwiftNum().substring(16,saveSwiftCancBean.getSwiftNum().length()));//swift 序号
					   yssSwiftRela.setSwiftNewIndex(saveSwiftCancBean.getWwiftIndex());
					   yssSwiftRela.setSwiftStatus("CANC");
					   
				       if(swiftType.startsWith("MT1") || swiftType.startsWith("MT2")){
						  swiftText=buildMTn92SwiftText(swiftType,yssSwiftRela);
					   }else{
						  swiftText=buildSwiftText(getSwiftTemp(rs.getString("FSwiftType"),rs.getString("FSwiftStatus"),rs.getString("FSwiftCode")),rsDs,hmType,yssSwiftRela);
					   }
				       
					   saveSwiftCancBean.setSwiftText(swiftText);
					   saveSwiftCancBean.setSwiftStatus("CANC");//swift 状态	
					   saveSwiftCancBean.setSwiftIndex(yssSwiftRela.getSwiftNewIndex()+"|"+saveSwiftCancBean.getSwiftType()+yssSwiftRela.getSwiftRefIndex());
					  
					   if(flag){
						   dbl.executeSql("delete from " + pub.yssGetTableName("Tb_Data_OriginalSWIFT")+" where FSwiftStatus = 'CANC' and frelanum = '"+rsDs.getString("FTradeNum")+"'");
						   saveSwiftCancBean.setCheckState(1);//审核已撤销的报文的审核状态不变
						   flag=false; //将flag重置为默认值
					   }else{
						   saveSwiftCancBean.setCheckState(3);//定义3时表示未激活的报文数据,是不会在前台显示的		   
					   }
				       
					   alCont.add(saveSwiftCancBean);
					 }
				}//end rsDs;
				rsDs.getStatement().close();
				sqlStr = "delete from " + pub.yssGetTableName("Tb_Data_OriginalSWIFT") +
                " where FSwiftType="+dbl.sqlString(rs.getString("FSwiftType"))+
                " and fcheckstate <>1 and FDate ="+dbl.sqlDate(startDate)+
                (this.portCode.trim().length()>0?(" and FPortCode="+dbl.sqlString(portCode)):"");////by leeyu 20091217 添加  QDV4赢时胜上海2009年12月17日07_B MS00874
				dbl.executeSql(sqlStr);
			}//end rs
			rs.getStatement().close();
			saveSwift.set(alCont);			
			saveSwift.insert(true);
			conn.commit();
			conn.setAutoCommit(bTrans);
			bTrans = false;
		}catch(Exception ex){
			throw new YssException(ex.getMessage(),ex);
		}finally{
			dbl.closeResultSetFinal(rs,rsDs);
			dbl.endTransFinal(conn,bTrans);
		}
		return loadSWIFTListView();//最后将报文数据返回到前台
	}

	/**
     * 处理SWIFT的选项值(去除标识符所占有的选项值)，并返回标识符所占有的选项值
     * @param entity DaoSwiftEntitySet
     * @return String
     */
    private String setContent(DaoSwiftEntitySet entity){
        int iLen=0;
        String startStr="";
        if(entity.getSOption()!=null &&entity.getSOption().trim().length()>0){
            iLen =entity.getSQualifier().trim().length();
            if(entity.getSQualifier()==null || entity.getSQualifier().trim().length()==0)
                iLen =entity.getSContent().trim().length();//当标识符为空，试取内容的长度
            if(entity.getSOption().startsWith(iLen+"c")){
                startStr=entity.getSOption().substring(0,(iLen+"c").length());
            }else if(entity.getSOption().startsWith(iLen+"!c")){
                startStr=entity.getSOption().substring(0,(iLen+"!c").length());
            }else if(entity.getSOption().startsWith(":"+iLen+"c")){
                startStr=entity.getSOption().substring(0,(":"+iLen+"c").length());
            }else if(entity.getSOption().startsWith(":"+iLen+"!c")){
                startStr=entity.getSOption().substring(0,(":"+iLen+"!c").length());
            }
            if(startStr.length()>0)
                entity.setSOption(entity.getSOption().substring(startStr.length()));
        }
        return startStr;
    }
	 /**
     * 解析选项值，并放入到集合中
     * @param sContent String
     * @throws YssException
     */
    private void parseCon(String sContent) throws YssException{
        String[] arrSign = sign.split(",");
        String sTmpSign = "";
        ArrayList liFormula = new ArrayList();
        liFormula.add(sContent);
        for (int i = 0; i < arrSign.length; i++) {
            if (arrSign[i].length() == 1) {
                if (arrSign[i].equalsIgnoreCase("[") ||
                    arrSign[i].equalsIgnoreCase("]")) {
                    arrSign[i] = "\\" + arrSign[i];
                }
                sTmpSign = "[" + arrSign[i] + "]";
            } else {
                sTmpSign = arrSign[i];
            }
            for (int iLi = 0; iLi < liFormula.size(); iLi++) {
                String[] sTmp = ( (String) liFormula.get(iLi)).split(sTmpSign);
                ArrayList liTemp = new ArrayList();
                if (sTmp.length <= 1) {
                    if (sTmp.length == 0 ||
                        sTmp[0].length() == ( (String) liFormula.get(iLi)).length()) {
                        continue;
                    }
                }
                for (int iT = 0; iT < sTmp.length; iT++) {
                    liTemp.add(sTmp[iT]);
                    liTemp.add(arrSign[i]);
                }
                if (liTemp.size() > 2) {
                    liTemp.remove(liTemp.size() - 1);
                }
                liFormula.remove(iLi);
                liFormula.addAll(iLi, liTemp);
                iLi += liTemp.size();
            }
        }
        if(alCont==null)alCont =new ArrayList();
        alCont.clear();
        for(int i=0;i<liFormula.size();i++){
            if( String.valueOf(liFormula.get(i)).length()==0)continue;
            alCont.add(liFormula.get(i));
        }
    }
    /**
     * 根据选项的长度、类型、限定符等重新处理内容值，返回一个合法格式的内容值
     * @param number String
     * @param limitr String
     * @param type String
     * @param value String
     * @return String
     * @throws YssException
     */
    private String getValue(String number,String limitr,String type,String value) throws YssException{
        int iNum=0;
        String tmpAdd="";//补充的数据
        try{
            if(value==null) return "";
            if (type.equalsIgnoreCase("n")) {
                //数字需格式化处理
                iNum = YssFun.toInt(number);
                if(limitr.equalsIgnoreCase("!")){
                    if(value.length()>iNum){
                        value=value.substring(value.length()-iNum);//截掉左边部分数据
                    }else if(value.length()<iNum){
                        for(int i=0;i<iNum-value.length();i++)
                            tmpAdd="0"+tmpAdd;//左边补0
                        value=tmpAdd+value;
                    }
                }else{
                    if(value.length()>iNum)
                        value=value.substring(value.length()-iNum);//截掉左边部分数据
                }
            } else if (type.equalsIgnoreCase("d")) {
                //数值
                iNum = YssFun.toInt(number);
                value = YssFun.formatNumber(YssFun.toDouble(value.equalsIgnoreCase("null")?"0":value),"###.##");
                if(value.indexOf(".")<0)
                    value+=".";//在末尾添加逗号
                value = value.replace('.',',');
                if(limitr.equalsIgnoreCase("!")){
                    if(value.length()>iNum){
                        String[] arrTemp =value.split(",");
                        if(arrTemp[0].length()>iNum)
                            arrTemp[0] = arrTemp[0].substring(arrTemp[0].length()-iNum+1);//截掉左边部分数据
                        value = arrTemp[0]+",";
                        if(arrTemp.length==2){
                            if (arrTemp[1].length() >= (iNum - arrTemp[0].length() - 1))
                                arrTemp[1] = arrTemp[1].substring(0, (iNum - arrTemp[0].length() - 1)); //截掉右边部分数据
                            value = value +arrTemp[1];
                        }
                    }else if(value.length()<iNum){
                        for(int i=0;i<iNum-value.length();i++)
                            tmpAdd="0"+tmpAdd;//左边补0
                        value=tmpAdd+value;
                    }
                }else{
                    if(value.length()>iNum)
                        value=value.substring(value.length()-iNum);//截掉左边部分数据
                }
            } else if (type.equalsIgnoreCase("x")) {
                //换行
                int row=1;
                int rowLen=0;
                String tempRow="",tempValue="";
                if(number.indexOf("*")>0){
                    row = YssFun.toInt(number.split("[*]")[0]);
                    rowLen = YssFun.toInt(number.split("[*]")[1]);
                }else{
                    rowLen = YssFun.toInt(number);
                }
                iNum=row*rowLen;
                for(int iRow=0;iRow<row;iRow++){//循环行
                    if(value.length()>=rowLen){
                        tempRow = value.substring(0, rowLen);
                    }else{
                        tempRow = value;
                    }
                    value =value.substring(tempRow.length());
                    tempValue = tempValue + tempRow+"\n";                    
                }
                value = tempValue;
                if(value.endsWith("\n")){//去年最后一行的换行符 by leeyu 20100104
                	value=value.substring(0,value.length()-1);
                }
            } else {
                //判断是否截位处理
                iNum = YssFun.toInt(number);
                if(value.length()>iNum)
                    value =value.substring(value.length()-iNum);//截掉左边部分数据
                if(limitr.equalsIgnoreCase("!")){
                    if(value.length()<iNum){
                        for(int i=0;i<iNum-value.length();i++)
                            tmpAdd=" "+tmpAdd;//左边补空格
                        value=tmpAdd+value;
                    }
                }
            }
            iTotalLen += iNum;
        }catch(Exception ex){
            throw new YssException("内容格式不正确，生成数据不成功!\r\n"+ex.getMessage());
        }
        return value;
    }
    /**
     * 获取list中的上一个长度大于0内容的选项值
     * @param alContent ArrayList
     * @param iCurr int
     * @return String
     */
    private String getPrevious(ArrayList alContent,int iCurr){
        String sRes ="";
        for(int i=iCurr-1;i>=0;i++){
            sRes =(String) alContent.get(i);
            if(sRes!=null&& sRes.trim().length()>0)
                break;
        }
        return sRes;
    }
    
    /**
     * 生成SWIFT的报文原谅数据
     * @param alContent
     * @param rs
     * @param hmFieldType
     * @return
     * @throws YssException
     */
    private String buildSwiftText(ArrayList alContent,ResultSet rs,HashMap hmFieldType,YssSwiftRela swiftRela) throws YssException{
    	StringBuffer buf=new StringBuffer();
    	DaoSwiftEntitySet entity =null;
    	String fieldType="";
    	try{
    		for(int i=0;i<alContent.size();i++){
    			entity =(DaoSwiftEntitySet)alContent.get(i);
    			if(entity.getSStatus().equalsIgnoreCase("M")||entity.getSStatus().equalsIgnoreCase("O")){
    				entity=setSWIFTNum(entity,swiftRela);
    				if(entity.getSStatus().equalsIgnoreCase("M")){
    					if(entity.getSTableField().trim().length()>0 && rs.getString(entity.getSTableField())!=null){
        					fieldType = String.valueOf(hmFieldType.get(entity.getSTableField().toUpperCase()));
        					if (fieldType.indexOf("CHAR") > -1) {
        						buf.append(buildEntityStr(entity, rs.getString(entity.getSTableField()))).append("\n");
        					} else if (fieldType.indexOf("DATE") > -1) {
        						buf.append(buildEntityStr(entity, rs.getDate(entity.getSTableField()))).append("\n");
        					}else{
        						buf.append(buildEntityStr(entity, YssFun.formatNumber(rs.getDouble(entity.getSTableField()), "###0.##"))).append("\n");
        					}
        				}else{
        					buf.append(buildEntityStr(entity, "")).append("\n");
        				}
    				}else{
    					if(entity.getSTableField().trim().length()>0 && rs.getString(entity.getSTableField())!=null){
        					fieldType = String.valueOf(hmFieldType.get(entity.getSTableField().toUpperCase()));
        					if (fieldType.indexOf("CHAR") > -1) {
        						buf.append(buildEntityStr(entity, rs.getString(entity.getSTableField()))).append("\n");
        					} else if (fieldType.indexOf("DATE") > -1) {
        						buf.append(buildEntityStr(entity, rs.getDate(entity.getSTableField()))).append("\n");
        					}else{
        						buf.append(buildEntityStr(entity, YssFun.formatNumber(rs.getDouble(entity.getSTableField()), "###0.##"))).append("\n");
        					}
        				}
    					if(entity.getSContent().trim().length()>0 && !entity.getSContent().equalsIgnoreCase("/")){
    						buf.append(buildEntityStr(entity, "")).append("\n");
    					}
    				}
    			}
    		}//end for
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage(),ex);
    	}
    	if(buf.length()>1){
    		buf.setLength(buf.length()-1);
    	}
    	return buf.toString();
    }
    
    /**
     * 处理 MT1XX与MT2XX的报文的撤消报文情况,这里采用写死的方法,关键字需在前台撤消报文时由用户填写值来定
     * @param swiftType
     * @param swiftRela
     * @return
     * @throws YssException
     */
    private String buildMTn92SwiftText(String swiftType,YssSwiftRela swiftRela) throws YssException{
    	StringBuffer buf =new StringBuffer();
    	try{
    		if(swiftType.startsWith("MT1")||swiftType.startsWith("MT2")){
    			//这里写入的是报文MTn92类格式报文,其中<Session><ISN><79><CopyField>都是需用户在撤消时手输入的
    			buf.append(":20:").append(swiftRela.getSwiftNewIndex()).append("\n");
    			buf.append(":21:").append(swiftRela.getSwiftRefIndex()).append("\n");
    			buf.append(":11S:")
    			   .append(swiftType.substring(2, 5))
    			   .append(YssFun.formatDate(startDate,"yyMMdd"))
    			   .append("<Session><ISN>")
    			   .append("\n");
    			buf.append(":79:<79>").append("\n");
    			buf.append("<CopyField>");
    		}
    	}catch(Exception ex){
    		throw new YssException(ex.getMessage(),ex);
    	}
    	return buf.toString();
    }
}
