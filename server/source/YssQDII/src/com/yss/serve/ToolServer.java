package com.yss.serve;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.yss.dbupdate.autoupdatetables.standardframebuild.*;
import com.yss.dsub.*;
import com.yss.imp.*;
import com.yss.main.cashmanage.*;
import com.yss.tools.*;
import com.yss.util.*;

import org.apache.log4j.*;
public class ToolServer
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ToolServer() {
    }

    public void init() throws ServletException {

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = com.yss.util.YssUtil.getBytesStringFromClient(request);
        String beanId = request.getParameter("cmd");
        String flag = request.getParameter("flag");
        //2008.05.08 蒋锦 添加
        String showType = request.getParameter("showtype");
        String dblbl = request.getParameter("dblbl");
        String strSafe = request.getParameter("safe");//MS01044 QDV4招商基金2010年3月19日03_A 对数据库配置文件进行加密设置   panjunfang modify 20100407 
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据     
        HttpSession session = request.getSession();
        YssPub pub = null;
        DbBase dbtmp = null;
        //============ 将日志写入到log文件    2010.02.08 add by jiangshichao ==================================== 
        String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";//文件分隔符  
        String path = this.getServletContext().getRealPath("");//应用程序的绝对路径 BUG7549 panjunfang add 20130428	

        Logger log = null;
                    try {
						YssUtil.setSysProp(request);
						YssUtil.createFold(System.getProperty("logsDir")+fileSeparator, "Ysstech_lOGS");
				        log = Logger.getLogger("D");
					} catch (YssException e) {
						e.printStackTrace();
					}
        //------------------------------------------------------------------
        try {
            dbtmp = (DbBase) session.getAttribute(YssCons.SS_DBLINK);
            if (dbtmp == null) {
                pub = new YssPub(); //alex20050127改变pub创建时机，统一在这里一次创建
                pub.setDblbl(dblbl);
                dbtmp = new DbBase();
                if(strSafe != null && strSafe.equalsIgnoreCase("y")){//如果前台配置文件中配置了安全模式 MS01044 QDV4招商基金2010年3月19日03_A 对数据库配置文件进行加密设置   panjunfang modify 20100407    
                	dbtmp.setbSafeMode(true);
                }

                session.setAttribute(YssCons.SS_DBLINK, dbtmp);
                session.setAttribute(YssCons.SS_PUBLIC, pub);
            } else {
                dbtmp = YssUtil.getSessionDbBase(request);
                pub = YssUtil.getSessionYssPub(request);
            }
            pub.setDbLink(dbtmp);
            if (!beanId.equalsIgnoreCase("dbconfig")){//如果是数据库加密配置，不需要创建数据库连接 #648 QDV4赢时胜（上海）2010年12月10日01_B panjunfang modify 20101213
            	dbtmp.loadConnection(dblbl);
            }
            pub = YssUtil.getSessionYssPub(request);
            
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
            //此处是为了获取配置路径
            /*pub.setWebRoot(YssUtil.getURLRoot(request));
                      //模拟登录
                      pub.yssLogon("001", "001");*/

            if (beanId.equalsIgnoreCase("righttype")) {
                RightTypeInOut bean = new RightTypeInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outRightType().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inRightType(squest);
                }
            }
            /*===============================================
             *自动创建表 MS00376 QDV4交银施罗德2009年4月13日01_AB
             *add by 王作春 20090417
             ===============================================*/
            else if (beanId.equalsIgnoreCase("readscript")) {
                ReadScript reads = new ReadScript();
                reads.setYssPub(pub);
                if (flag.equalsIgnoreCase("copy")) {
                    reads.readSql();
                }
            } else if (beanId.equalsIgnoreCase("exphxnavtofa")) { //华夏净值 add liyu 0926 01:37
                expHxNAVToTA navtofa = new expHxNAVToTA();
                navtofa.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) { //导出     add liyu 0926
                    navtofa.parseRowStr(squest);
                    ou.write(navtofa.ExpToTxt().getBytes());
                } else if (flag.equalsIgnoreCase("find")) { //查找基金代码
                    navtofa.parseRowStr(squest);
                    ou.write(navtofa.findFundCode().getBytes());
                }
            } else if (beanId.equalsIgnoreCase("operationtype")) {
                OperationTypeInOut bean = new OperationTypeInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outOperationType().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inOperationType(squest);
                }
            } else if (beanId.equalsIgnoreCase("menu")) {
                MenuInOut bean = new MenuInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outMenu().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inMenu(squest);
                }
            } else if (beanId.equalsIgnoreCase("menubar")) {
                MenuBarInOut bean = new MenuBarInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outMenuBar().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inMenuBar(squest);
                } else if (flag.equalsIgnoreCase("navout")) {
                	ou.write(bean.outNavMenuBar().getBytes());
                } else if (flag.equalsIgnoreCase("navin")) {
                    bean.inNavMenuBar(squest);
                }
            }
            else if (beanId.equalsIgnoreCase("refinvoke")) {
                RefInvokeInOut bean = new RefInvokeInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outRefInvoke().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inRefInvoke(squest);
                }
            } else if (beanId.equalsIgnoreCase("iconfig")) {
                IConfigInOut bean = new IConfigInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outIConfig().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inIConfig(squest);
                }
            } else if (beanId.equalsIgnoreCase("iconfigrela")) {
                IConfigRelaInOut bean = new IConfigRelaInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outIConfigRela().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inIConfig(squest);
                }
            } else if (beanId.equalsIgnoreCase("datadict")) {
                DataDictInOut bean = new DataDictInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outDataDict().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inDataDict(squest);
                }
            } else if (beanId.equalsIgnoreCase("broker")) {
                ImpBroker bean = new ImpBroker();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
//               ou.write(bean.outDataDict().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.saveBrokerData(squest);
                }
            } else if (beanId.equalsIgnoreCase("nfbroker")) {	//南方券商接口特别处理 sunkey@Modify 20091208
                ImpBrokerNF bean = new ImpBrokerNF();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                } else if (flag.equalsIgnoreCase("in")) {
					//start modify huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
                	ou.write(bean.saveBrokerData(squest).getBytes());
					//end modify huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
                }
            } else if (beanId.equalsIgnoreCase("hbbroker")) {
                ImpHbBroker bean = new ImpHbBroker();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                } else if (flag.equalsIgnoreCase("in")) {
                    //====add by xuxuming,20090924.MS00709,交易数据接口汇率提示,QDV4赢时胜上海2009年9月23日01_A
                    String[] strSplitRow = squest.split("~n~");
                    if (strSplitRow.length >= 2 && strSplitRow[1].equalsIgnoreCase("ExRate")) {
                        String strTemp = bean.getExRateData(squest);
                        if ("none".equalsIgnoreCase(strTemp)) { //没有汇率时，返回给前台作判断
                            ou.write(strTemp.getBytes());
                        } else { //有汇率时，接着执行数据导入
                            bean.saveBrokerData(squest);
                        }
                    } else {//此时为用户确信要导入数据
                        bean.saveBrokerData(squest);
                    }
                    //========end==========================================================================
                }
            } else if (beanId.equalsIgnoreCase("bloomberg")) {
                ImpBloomberg bean = new ImpBloomberg();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.saveBloombergData(squest);
                }
            } else if (beanId.equalsIgnoreCase("nfbloomberg")) {	//南方彭博接口 sunkey@Modify 20091208
                ImpBloombergNF bean = new ImpBloombergNF();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                } else if (flag.equalsIgnoreCase("in")) {
					//start modify huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
                	ou.write(bean.saveBloombergData(squest).getBytes());
					//end modify huangqirong 2013-07-31 STORY #4321 南方固定接口修改代码合并
                }
            } else if (beanId.equalsIgnoreCase("bnyinterface")) {
                BnyInterface bean = new BnyInterface();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outBnyData(squest).getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inBnyData(squest);
                }
            } else if (beanId.equalsIgnoreCase("nfbnyinterface")) {	//南方专用纽约银行接口 sunkey@Modify 20091208
                BnyInterfaceNF bean = new BnyInterfaceNF();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outBnyData(squest).getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inBnyData(squest);
                }
            }else if (beanId.equalsIgnoreCase("dbconfig")){//安全模式下的数据库连接信息的增删改 MS01044 QDV4招商基金2010年3月19日03_A 对数据库配置文件进行加密设置   panjunfang modify 20100407    
            	GenDBInfo dbInfo = new GenDBInfo();
            	dbInfo.parseRowStr(squest);
            	if (flag.equalsIgnoreCase("add")) {
                	dbInfo.addDBInfo();
            	}else if(flag.equalsIgnoreCase("edit")) {
            		dbInfo.editDBInfo();
            	}else if (flag.equalsIgnoreCase("del")) {
            		dbInfo.delDBInfo();
            	}
                ou.write("ok".getBytes());
                ou.close();
            }

            //词汇
            else if (beanId.equalsIgnoreCase("vocabulary")) {
                VocabularyInOut bean = new VocabularyInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outVocabulary().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inVocabulary(squest);
                }
            }

            else if (beanId.equalsIgnoreCase("vocabularytype")) {
                VocabularyTypeInOut bean = new VocabularyTypeInOut();
                bean.setYssPub(pub);
                if (flag.equalsIgnoreCase("out")) {
                    ou.write(bean.outVocabularyType().getBytes());
                } else if (flag.equalsIgnoreCase("in")) {
                    bean.inVocabulary(squest);
                }
            }

            else if (beanId.equalsIgnoreCase("cashnummanager")) {
                String[] sTmp = squest.split("\r\n");
                String[] str0 = sTmp[0].split("\t");
                String[] str1 = sTmp[1].split("\t");
                NumSortManager cnm = new NumSortManager();
                cnm.setYssPub(pub);
                if (str0[0].trim().length() > 0) {
                    cnm.sAftPrefix = str0[0].trim().toUpperCase();
                }
                if (str1[0].trim().length() > 0) {
                    cnm.sMastTab = str1[0].trim();
                }
                if (str1[1].trim().length() > 0) {
                    cnm.sMastTabField = str1[1].trim();
                }
                cnm.sTabs = sTmp[2].split("\f\f");
                ou.write(cnm.getNumChange().getBytes());
            } else if (beanId.equalsIgnoreCase("savingoutacc")) {
                SavingOutAccBean acc = new SavingOutAccBean();
                pub.yssLogon("001", "admin");
                //2008.05.08 蒋锦 添加
                pub.setWebRoot(YssUtil.getURLRoot(request));
                pub.setWebRootRealPath(path);//BUG7549 panjunfang add 20130428
                acc.setYssPub(pub);
                acc.getOperValue(showType);
            }
            //MS00352 《QDV4赢时胜（上海）2009年4月7日01_A》表结构自动更新的表结构导出到前台
            //2009.03 蒋锦
            else if (beanId.equalsIgnoreCase("databaseframe")) {
                if (flag.equalsIgnoreCase("exp")) {
                    StandardBuilder build = new StandardBuilder();
                    build.setYssPub(pub);
                    ou.write(build.exportData().getBytes());
                }
            }
        }catch (Exception ye) {
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.getLocalizedMessage().getBytes());//by guyichuan 2011.07.25  BUG2202净值导出界面存在问题
            ou.close();
        }
        finally
        {
        	/**shashijie 2012-7-2 STORY 2475 */
        	if (pub != null) {
	        	pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
	        	pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
        	}
        	/**end*/
        }
    }

    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        doGet(request, response);
    }

    //Clean up resources
    public void destroy() {
    }

}
