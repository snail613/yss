package com.yss.serve.setting;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.funsetting.*;
import com.yss.main.syssetting.*;
import com.yss.util.*;

public class SystemSetting
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//----MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦 添加---
    private Hashtable userTable = null;
    //-----------------------------------------------
    public SystemSetting() {
    }

    public void init() throws ServletException {
        //----MS00003 QDV4.1-参数布局散乱不便操作 蒋锦 2009.03.10---
        userTable = new Hashtable(); //在servlet初始化时将此用户操作结果容器生成。
        //----------------------------------------
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = com.yss.util.YssUtil.getBytesStringFromClient(request);
        String[][] reqAry = com.yss.util.YssUtil.ClientData(squest);
        String beanId = request.getParameter("cmd");
        String fLag = request.getParameter("flag");
        //add by zhangfa 20101203 当角色下有用户时，删除此角色应该提示 
        String showType = request.getParameter("showtype");
        //-----------end 20101203---------------------------------
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据   
        YssPub pub = null;
        int operType = 0;
        SingleLogOper logOper = null;
        //---- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦
        FlowBean flow = null;
        //------------------------------------
        //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
        AssetGroupBean cgb = null;
        UserBean ub = null;
        RightBean right = null;
        RoleBean role = null;
        RightTypeBean rightTypeBean = null;
        //---add by songjie 2012.06.11 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
        try {
            pub = YssUtil.getSessionYssPub(request);
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
            if (beanId.equalsIgnoreCase("initface")) {
                MenuBean mb = new MenuBean();
                mb.setYssPub(pub);

                MenubarBean mbar = new MenubarBean();
                mbar.setYssPub(pub);
                
                //---add by songjie 2012.02.07 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A start---//
                if(fLag != null && fLag.equals("searchMenuBar") && 
                   squest != null && !"".equals(squest)){
                	mb.setSearchContent(squest);
                	mbar.setSearchContent(squest);
                }
                //---add by songjie 2012.02.07 STORY #2196 QDV4赢时胜(上海开发部)2012年02月07日02_A end---//
                
//                String sendStr = mb.getTreeViewData3() + YssCons.YSS_LINESPLITMARK +
//                    YssCons.YSS_LINESPLITMARK +  //modified by yeshenghong for tb_fun_menu tables is useless 20121226 story2917
                String sendStr =     mbar.getTreeViewData3()
                    + YssCons.YSS_LINESPLITMARK +
                    YssCons.YSS_LINESPLITMARK + mbar.getTreeViewGroupData1();
                    
                ou.write(sendStr.getBytes());
            //story 1898  by zhouwei  20111124 QDV4赢时胜(上海开发部)2011年11月18日01_A 切换组合群
            }else if(beanId.equalsIgnoreCase("changeGroup")){
            	  MenubarBean mbar = new MenubarBean();
                  mbar.setYssPub(pub);
                  ou.write(mbar.getMenuRightOfAssetGroupCode().getBytes());
            //-----end  story 1898 QDV4赢时胜(上海开发部)2011年11月18日01_A-----	
            } else if (beanId.equalsIgnoreCase("groupsetting")) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                cgb = new AssetGroupBean();
                cgb.setYssPub(pub);
                //add by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B
                String status = request.getParameter("status");
                if (fLag.equalsIgnoreCase("query")) {
                    String groupCode = request.getParameter("groupcode");
                    ou.write(cgb.getData_group(groupCode).getBytes());
                } else if (fLag.equalsIgnoreCase("update")) {
                	//delete by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B
//                    String status = request.getParameter("status");
                	//edit by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B
                	
                	//add by huangqirong 2011-09-22 story #1286
                	int rows=cgb.getIsExsistMsg(reqAry);
                	if((status.equals("3")&&rows>0) || (status.equals("4")&&rows>0))
                		ou.write("allready".getBytes());
                	else
                	//---end---
                	{
                		cgb.saveData_group(reqAry, false, status);
                	}	
                }else if (fLag.equalsIgnoreCase("delete")) {
                	//edit by songjie 2011.03.19 BUG:1466 QDV4赢时胜(测试)2011年03月15日01_B
                    cgb.saveData_group(reqAry, true, status);
                   //-----------add by guojianhua 增加日志操作  2010 09 14 ------------
				    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    operType = YssCons.OP_DEL;
                    cgb.setModuleName("system");
                    cgb.setFunName("AssetGroupSet");
                    cgb.setRefName("00000L");
               
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(cgb, operType, pub);
                    //---------------  end  --------------------
                } else if (fLag.equalsIgnoreCase("updateMenu")) {
                    cgb.updateMenu(reqAry, "Tb_Fun_Menu");
//                  -----------add by guojianhua 增加日志操作  2010 09 14 ------------
                    operType = YssCons.OP_ADD;
                    cgb.setModuleName("system");
                    cgb.setFunName("AssetGroupSet");
                    cgb.setRefName("00000L");
               
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(cgb, operType, pub);
                    //---------------  end  --------------------
                } else if (fLag.equalsIgnoreCase("updateMenuBar")) {
                    cgb.updateMenu(reqAry, "Tb_Fun_Menubar");
                } else if (fLag.equalsIgnoreCase("updateRithgType")) {
                    cgb.updateRithgType(reqAry, "TB_SYS_RightType");
                } else if (fLag.equalsIgnoreCase("updateOperationType")) {
                    cgb.updateRithgType(reqAry, "Tb_Sys_OperationType");
                } else if (fLag.equalsIgnoreCase("assetgrouplist")) {
                    ou.write(cgb.getListViewData().getBytes());
                }else if (fLag.equalsIgnoreCase("ETFassetgrouptree")){//用于获取ETF组合群树的信息 20091010 panjunfang add ，MS00004 ETF估值处理 QDV4.1赢时胜（上海）2009年9月28日03_A
                    cgb.setBETF(true);
                    ou.write(cgb.getTreeViewData().getBytes());
                }
                //用于获取组合群树的信息 20081203   王晓光 MS00036
                else if (fLag.equalsIgnoreCase("assetgrouptree")) {
                    ou.write(cgb.getTreeViewData().getBytes());
                }else if (fLag.equalsIgnoreCase("assetgrouptreenew"))
                {// add by yeshenghong story3702  数据中心的操作接口  20130418
                	ou.write(cgb.getTreeViewDataNew().getBytes());
                }
                //加载全部组合群的全部组合信息   add by guolongchao 20111104 STORY 1572 权限复制功能扩展
                else if (fLag.equalsIgnoreCase("treeviewportcodes")) {
                    ou.write(cgb.getTreeViewPortCodes().getBytes());
                }
                //用于获取全部组合树的信息 20090429  wangzuochun MS00010 QDV4.1赢时胜上海2009年2月1日01_A
                else if (fLag.equalsIgnoreCase("treeviewport")) {
                    ou.write(cgb.getTreeViewPort().getBytes());
                }
                //用于获取全部组合群树的信息 20090429  wangzuochun MS00010 QDV4.1赢时胜上海2009年2月1日01_A
                else if (fLag.equalsIgnoreCase("treeviewgroup")) {
                    ou.write(cgb.getTreeViewGroup().getBytes());
                }
                // add by fangjiang 2011.08.18 STORY #1288
                else if (fLag.equalsIgnoreCase("getcheckinfo")) {
                    ou.write(cgb.isNeedCheck().getBytes());
                }

            } else if (beanId.equalsIgnoreCase("userset")) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                ub = new UserBean();
                ub.setYssPub(pub);
                if (fLag.equalsIgnoreCase("query")) {
                	//------ add by wangzuochun 2010.07.08 MS01393   建立一个用户代码为中文的用户后，在对该用户进行修改操作时会报错    QDV4赢时胜深圳2010年7月5日01_B    
                    String userCode = (request.getParameter("usercode") == null ? null : new String(request.getParameter("usercode").getBytes("ISO8859_1"),"GBK"));
                    //---------------------MS01393-------------------//
                    ou.write(ub.getUser(userCode).getBytes());
                } else if (fLag.equalsIgnoreCase("save")) {
                    operType = YssCons.OP_ADD;
                    ub.parseRowStr(squest); //add huangqirong 2012-09-27 bug #5804
                    ub.saveUser(reqAry, false);

                    //-----------------------------------------
                    ub.setModuleName("system");
                    ub.setFunName("userSet");
                    ub.setRefName("000005");
                    //ub.parseRowStr(squest); //modify huangqirong 2012-09-27 bug #5804
                    ub.getOperValue(null);
                    if (pub.getUserCode() == null ||
                        pub.getUserCode().trim().length() == 0) {
                        return;
                    }
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(ub, operType, pub);
                } else if (fLag.equalsIgnoreCase("update")) {

                    logOper = SingleLogOper.getInstance();
                    operType = YssCons.OP_EDIT;
                    ub.setModuleName("system");
                    ub.setFunName("userSet");
                    ub.setRefName("000005");
                    ub.parseRowStr(squest);
                    logOper.setBData(ub);
                    ub.updateUser(reqAry);
                    ub.getOperValue(null);
                    logOper.setIData(ub, operType, pub);
                } else if (fLag.equalsIgnoreCase("delete")) {
				    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    operType = YssCons.OP_DEL;
                    ub.parseRowStr(squest);
                    ub.getOperValue(null);
                    ub.saveUser(reqAry, true);
                    /**shashijie 2012-5-28 STORY 2620 删除权限 */
					ub.deleteUserRight(ub.getFUserCode());
					/**end*/
                    //-----------------------------------------
                    ub.setModuleName("system");
                    ub.setFunName("userSet");
                    ub.setRefName("000005");

                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(ub, operType, pub);

                } else if (fLag.equalsIgnoreCase("userlist")) {
                    ou.write(ub.getListViewData().getBytes());
                } else if (fLag.equalsIgnoreCase("userlist1")){
                	ou.write(ub.getListViewData1().getBytes());
                }
                else if (fLag.equalsIgnoreCase("getUserMenu")) {
                    ou.write(ub.getData_menu("Tb_Fun_Menu").getBytes());
                } else if (fLag.equalsIgnoreCase("getUserMenuBar")) {
                    ou.write(ub.getData_menu("Tb_Fun_Menubar").getBytes());
                }
                //更新密码有效时间和安全性 2008-11-12 linjunyun 修改 Bug:MS00016
                else if (fLag.equalsIgnoreCase("updatePassLevelAndTime")) {
                    ub.updatePassLevelAndTime(squest);
                }
                //查询密码有效时间和安全性 2008-11-12 linjunyun 修改 Bug:MS00016
                else if (fLag.equalsIgnoreCase("queryPassLevelAndTime")) {
                    ou.write(ub.getPassLevelAndTime(pub.getUserCode()).getBytes());
                }
                
                /**Start 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001
                 * 从密码复杂度设置中获取初始密码。若密码复杂度设置界面没有设置重置初始密码，则默认为"1"*/
                else if (fLag.equalsIgnoreCase("getresetpwd"))
                {
                	ou.write(ub.getCurrentResetPwd().getBytes());
                }
                /**End 20130702 added by liubo.Story #4135.需求上海-[YSS_SH]QDIIV4.0[中]20130628001*/
            }
            //=====add by xuxuming,20091105.MS00776 ,登录密码的问题    QDV4海富通2009年11月2日01_AB====
            //====增加设置密码复杂度的处理模块================
            else if(beanId.equalsIgnoreCase("passcomplex")){
            	PassComplexBean passComplex = new PassComplexBean();
            	passComplex.setYssPub(pub);
            	passComplex.parseRowStr(squest);
            	if(fLag.equalsIgnoreCase("change")){
            		ou.write(passComplex.doChange().getBytes());
            	}else if(fLag.equalsIgnoreCase("query")){
            		ou.write(passComplex.getListViewData1().getBytes());
            	}
            	
            }
            //=====end======================================================
            //数据字典设置
            else if (beanId.equalsIgnoreCase("datadict")) {
                DataDictBean datadict = new DataDictBean(pub);
                if (fLag.equalsIgnoreCase("listview1")) {
                    datadict.protocolParse(squest);
                    ou.write(datadict.getListViewData().getBytes());
                }
                if (fLag.equalsIgnoreCase("listview3")) {
                    datadict.protocolParse(squest);
                    ou.write(datadict.getListViewData3().getBytes());
                } else if (fLag.equalsIgnoreCase("listview2")) {
                    datadict.protocolParse(squest);
                    ou.write(datadict.getListViewData2().getBytes());
                } else if (fLag.equalsIgnoreCase("listviewNew")) {
                    datadict.protocolParse(squest);
                    ou.write(datadict.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("add")) {
                    datadict.protocolParse(squest);
                    datadict.checkInput(YssCons.OP_ADD);
                    datadict.addDataDict().getBytes();
                    ou.write(datadict.getListViewData().getBytes());
                } else if (fLag.equalsIgnoreCase("edit")) {
                    datadict.protocolParse(squest);
                    datadict.checkInput(YssCons.OP_EDIT);
                    datadict.editDataDict().getBytes();
                    ou.write(datadict.getListViewData().getBytes());
                } else if (fLag.equalsIgnoreCase("multedit")) {
                    datadict.protocolParse(squest);
                    datadict.checkInput(YssCons.OP_EDIT);
                    datadict.editMultDataDict().getBytes();
                    ou.write(datadict.getListViewData().getBytes());

                } else if (fLag.equalsIgnoreCase("del")) {
                    datadict.protocolParse(squest);
                    datadict.delDataDict().getBytes();
                    ou.write(datadict.getListViewData().getBytes());
                } else if (fLag.equalsIgnoreCase("delAll")) {
                    datadict.protocolParse(squest);
                    datadict.delAllDataDict();
                    ou.write(datadict.getListViewData().getBytes());
				//------------------add by guolongchao 20111216 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls-----------start
                } else if (fLag.equalsIgnoreCase("getLines")) {            
	                ou.write(datadict.getLines(squest).getBytes());
	            }
				//------------------add by guolongchao 20111216 STORY1903 QDV4赢时胜（上海）2011年11月18日01_A.xls-----------end
                //数据表字典设置 Add by pengjinggang20090623
            }else if (beanId.equalsIgnoreCase("tabledict")) {
                    TableDictBean datadict = new TableDictBean(pub);
                    if (fLag.equalsIgnoreCase("listview1")) {
                        datadict.protocolParse(squest);
                        ou.write(datadict.getListViewData().getBytes());
                    }
                    if (fLag.equalsIgnoreCase("listview3")) {
                        datadict.protocolParse(squest);
                        ou.write(datadict.getListViewData3().getBytes());
                    } else if (fLag.equalsIgnoreCase("listview2")) {
                        datadict.protocolParse(squest);
                        ou.write(datadict.getListViewData2().getBytes());
                    } else if (fLag.equalsIgnoreCase("listviewNew")) {
                        datadict.protocolParse(squest);
                        ou.write(datadict.getListViewData1().getBytes());
                    } else if (fLag.equalsIgnoreCase("add")) {
                        datadict.protocolParse(squest);
                        datadict.checkInput(YssCons.OP_ADD);
                        datadict.addTableDict().getBytes();
                        ou.write(datadict.getListViewData().getBytes());
                    } else if (fLag.equalsIgnoreCase("edit")) {
                        datadict.protocolParse(squest);
                        datadict.checkInput(YssCons.OP_EDIT);
                        datadict.editTableDict().getBytes();
                        ou.write(datadict.getListViewData().getBytes());
                    } else if (fLag.equalsIgnoreCase("multedit")) {
                        datadict.protocolParse(squest);
                        datadict.checkInput(YssCons.OP_EDIT);
                        datadict.editMultTableDict().getBytes();
                        ou.write(datadict.getListViewData().getBytes());

                    } else if (fLag.equalsIgnoreCase("del")) {
                        datadict.protocolParse(squest);
                        datadict.delTableDict().getBytes();
                        ou.write(datadict.getListViewData().getBytes());
                    } else if (fLag.equalsIgnoreCase("delAll")) {
                        datadict.protocolParse(squest);
                        datadict.delAllTableDict();
                        ou.write(datadict.getListViewData().getBytes());
                    }

            } else if (beanId.equalsIgnoreCase("userrightset")) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                right = new RightBean(pub);
                if (fLag.equalsIgnoreCase("loadrighttype")) {
                    // 给方法增加一个参数squest区分权限类型 2009.04.22 wangzuochun MS00010 QDV4.1赢时胜上海2009年2月1日01_A
                    ou.write(right.getFundRightType(squest).getBytes());
                } else if (fLag.equalsIgnoreCase("loaduserright")) {
                    // 给方法增加一个参数squest区分权限类型 2009.04.22 wangzuochun MS00010 QDV4.1赢时胜上海2009年2月1日01_A
                    ou.write(right.getFundRightType(squest).getBytes());
                }
                //------- add by wangzuochun 2010.03.01 MS00921  增加浏览用户所有权限和导出相关信息的功能  QDV4银华2010年01月08日01_A    
                else if (fLag.equalsIgnoreCase("exportforuser")) {
                	ou.write(right.exportForUser(squest).getBytes());
                }
                else if (fLag.equalsIgnoreCase("exportforrole")) {
                	ou.write(right.exportForRole(squest).getBytes());
                }
                else if (fLag.equalsIgnoreCase("exportforport")) {
                	ou.write(right.exportForPort(squest).getBytes());
                }
                else if (fLag.equalsIgnoreCase("exportforgroup")) {
                	ou.write(right.exportForGroup(squest).getBytes());
                }
                //---------------------- MS00921 ------------------------//
                
                //add by guolongchao 20120426 添加自审功能 QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----start
                else if (fLag.equalsIgnoreCase("getAuditOwnState")) {
                	ou.write(right.getAuditOwnState(squest).getBytes());
                }
                //add by guolongchao 20120426 添加自审功能 QDV4赢时胜（南方基金）2012年4月11日01_A需求规格说明书.doc-----start
                
                //fanghaoln 20090425 MS0001 QDV4.1 权限明细到组合
                else if (fLag.equalsIgnoreCase("getallPortfrmcode")) {
                    ou.write(right.getAllPortRightFrmCode().getBytes()); // 有些窗体不要判断组合级别的权限。把所有组合级别的权限的窗体名称返回到前台进行判断
                } else if (fLag.equalsIgnoreCase("saveuserright")) {
                    operType = YssCons.OP_EDIT;
                    logOper = SingleLogOper.getInstance();
                    //edit by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    right.setModuleName("system");//模块代码由 空 改为 系统设置 
                    
                    if (squest.split("<Logging>").length >= 3)
                    {
                    	right.setFunName(squest.split("<Logging>")[2]);
                    }
                    else
                    {
                    	right.setFunName(squest.split("<Logging>")[1]);
                    }
                    right.setRefName("000006");
                    right.parseRowStr(squest);

                    logOper.setBData(right);
                    //20130121 modified by liubo.Story #2839
                    //在实际存储权限时，saveUserRight方法不会使用经过解析后的请求字符串，所以要在这里再做一次对“<Logging>”标签的解析
                    //==================================
//                    ou.write(right.saveUserRight(squest).getBytes());
                    ou.write(right.saveUserRight(squest.split("<Logging>")[0]).getBytes());
                    //==============end====================
                    logOper.setIData(right, operType, pub);
                }
                //20120918 added by liubo.Story #2737
                //获取用户继承到的权限的明细信息
                //======================================
                else if (fLag.equalsIgnoreCase("getRightsInheritanced"))
                {
                	ou.write(right.getRightsInheritanced(squest).getBytes());
                }
                //======================================

                //----MS00010 add by songjie 2009-04-30----//
                //QDV4.1赢时胜上海2009年2月1日01_A  权限明细到组合
                else if (fLag.equalsIgnoreCase("copyuserright")) {
                    right.copyUserRight(squest);
                } else if (fLag.equalsIgnoreCase("copyuserportright")) {
                    right.copyUserPortRight(squest);
                } else if (fLag.equalsIgnoreCase("copyuserandportright")) {
                    right.copyUserAndPortRight(squest);
                } else if (fLag.equalsIgnoreCase("copyUserAndPortrightAll")) {//add by guolongchao  STORY 1572 添加：用户组合复制(所有权限)
	                right.copyUserAndPortRightALL(squest);
	            }	           
                //----MS00010 add by songjie 2009-04-30----//

                else if (fLag.equalsIgnoreCase("saveroleright")) {
                	
                	//20130305 added by liubo.Story #2839
                	//保存角色权限时，也需要将操作数据保存进日志
                	//===============================
                	operType = YssCons.OP_EDIT;
                    logOper = SingleLogOper.getInstance();
                    //edit by songjie 2012.10.29 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    right.setModuleName("system");//模块代码由 空 改为 系统设置 
                    right.setFunName("rightSet_Role");
                    right.setRefName("000006");
                    right.parseRowStr(squest);

                    logOper.setBData(right);
                    
//                    ou.write(right.saveRoleRight(squest).getBytes());
                    ou.write(right.saveRoleRight(squest.split("<Logging>")[0]).getBytes());
                    
                    logOper.setIData(right, operType, pub);
                	//=============end==================
                    
                } else if (fLag.equalsIgnoreCase("getuseright")) {
                    right.protocolParse(squest);
                    ou.write(right.getUserRight().getBytes());
                } else if (fLag.equalsIgnoreCase("getroleright")) {
                    right.protocolParse(squest);
                    ou.write(right.getRoleRight().getBytes());
                } else if (fLag.equalsIgnoreCase("getuserport")) {
                    right.protocolParse(squest);
                    ou.write(right.getUserPorts().getBytes());
                } else if (fLag.equalsIgnoreCase("getbyrole")) {
                    right.protocolParse(right.getFirstPara(squest));
                    ou.write(right.getByRoleRigth(right.getSecondPara(squest)).
                             getBytes());
                } else if (fLag.equalsIgnoreCase("getuserroleright")){//2009.08.06 蒋锦 添加 MS00577 QDV4赢时胜（上海）2009年7月24日04_B
                    right.protocolParse(squest);
                    ou.write(right.getUserRoleRight().getBytes());
                } else if (fLag.equalsIgnoreCase("getOperTypeStr")){
                    ou.write(right.getOperTypeStr().getBytes());
                }else if(fLag.equalsIgnoreCase("AssetGroup")){// add by  baopingping 20110715 #story 1167 重新给后台的组合群赋值
                	if(showType!=null){
                		String[] GroupUser=showType.split("\f");
                		String GroupCode=GroupUser[0];
                		String useCode=GroupUser[1];
                		pub.yssLogon(GroupCode, useCode);
                	}//------------end----------------------
                }
                /**shashijie 2012-7-11 STORY 2661 */
                else if (fLag.equalsIgnoreCase("isRoleRight")){
                    right.protocolParse(squest);
                    ou.write(right.isRoleRight().getBytes());
                }
				/**end*/
                /**yeshenghong 2013-03-09 财务估值集成 */
                else if (fLag.equalsIgnoreCase("listview1")){
                    ou.write(right.getListViewData1().getBytes());
                }
				/**end*/
                
            } else if (beanId.equalsIgnoreCase("roleset")) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                role = new RoleBean(pub);
                if (fLag.equalsIgnoreCase("listview1")) {
                    role.protocolParse(squest);
                    ou.write(role.getListViewData().getBytes());
                } else if (fLag.equalsIgnoreCase("userroles")) {
                    role.protocolParse(squest);
                    ou.write(role.getUserRoles().getBytes());
                } else if (fLag.equalsIgnoreCase("query")) {
                    ou.write( (role.getListViewData().getBytes()));
                } else if (fLag.equalsIgnoreCase("add")) {
                    role.protocolParse(squest);
                    ou.write(role.addRole().getBytes());
                   // -----------add by guojianhua 增加日志操作  2010 09 14 ------------
                    operType = YssCons.OP_ADD;
                    role.setModuleName("system");
                    role.setFunName("roleSet");
                    role.setRefName("000003");
               
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(role, operType, pub);
                    //---------------  end  --------------------
                } else if (fLag.equalsIgnoreCase("edit")) {
                    role.protocolParse(squest);
                    ou.write(role.editRole().getBytes());
                    //-----------add by guojianhua 增加日志操作  2010 09 14 ------------
                    operType = YssCons.OP_EDIT;
                    role.setModuleName("system");
                    role.setFunName("roleSet");
                    role.setRefName("000003");
                     
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(role, operType, pub);
                    //---------------  end  --------------------
                } else if (fLag.equalsIgnoreCase("del")) {
                    role.protocolParse(squest);
                    /**shashijie 2012-5-28 STORY 2620 删除权限 */
                    role.deleteUserRight(role.getRoleCode());
					/**end*/
                    ou.write(role.delRole().getBytes());
                  //-----------add by guojianhua 增加日志操作  2010 09 14 ------------
                    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
					operType = YssCons.OP_DEL;
                    role.setModuleName("system");
                    role.setFunName("roleSet");
                    role.setRefName("000003");
               
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(role, operType, pub);
                    //---------------  end  --------------------
                //   add by zhangfa 20101203 当角色下有用户时，删除此角色应该提示 
                }else if (fLag.equalsIgnoreCase("opervalue")) {
                	role.protocolParse(squest);
                    ou.write(role.getOperValue(showType).getBytes());
                }
                //---------------end 20101203------------------------------------
            } else if (beanId.equalsIgnoreCase("department")) {
                DepartmentBean deptBean = new DepartmentBean(pub);
                if (fLag.equalsIgnoreCase("listview1")) {
                    deptBean.parseRowStr(squest);
                    ou.write(deptBean.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("listview2")) {
                    deptBean.parseRowStr(squest);
                    ou.write(deptBean.getListViewData2().getBytes());
                } else if (fLag.equalsIgnoreCase("add")) {
                    deptBean.parseRowStr(squest);
                    deptBean.checkInput(YssCons.OP_ADD);
                    deptBean.saveSetting(YssCons.OP_ADD);
                    ou.write(deptBean.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("edit")) {
                    deptBean.parseRowStr(squest);
                    deptBean.checkInput(YssCons.OP_EDIT);
                    deptBean.saveSetting(YssCons.OP_EDIT);
                    ou.write(deptBean.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("del")) {
                    deptBean.parseRowStr(squest);
                    deptBean.checkInput(YssCons.OP_DEL);
                    deptBean.saveSetting(YssCons.OP_DEL);
                    ou.write(deptBean.getListViewData1().getBytes());
                }
            } else if (beanId.equalsIgnoreCase("position")) {
                PositionBean positionBean = new PositionBean();
                positionBean.setYssPub(pub);
                if (fLag.equalsIgnoreCase("listview1")) {
                    positionBean.parseRowStr(squest);
                    ou.write(positionBean.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("listview2")) {
                    String strDeptCode = request.getParameter("deptcode");
                    ou.write(positionBean.getListViewData2(strDeptCode).getBytes());
                } else if (fLag.equalsIgnoreCase("add")) {
                    positionBean.parseRowStr(squest);
                    positionBean.checkInput(YssCons.OP_ADD);
                    positionBean.saveSetting(YssCons.OP_ADD);
                    ou.write(positionBean.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("edit")) {
                    positionBean.parseRowStr(squest);
                    positionBean.checkInput(YssCons.OP_EDIT);
                    positionBean.saveSetting(YssCons.OP_EDIT);
                    ou.write(positionBean.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("del")) {
                    positionBean.parseRowStr(squest);
                    positionBean.checkInput(YssCons.OP_DEL);
                    positionBean.saveSetting(YssCons.OP_DEL);
                    ou.write(positionBean.getListViewData1().getBytes());
                }
            } else if (beanId.equalsIgnoreCase("righttype")) {
			    //edit by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                rightTypeBean = new RightTypeBean();
                rightTypeBean.setYssPub(pub);
                if (fLag.equalsIgnoreCase("listview1")) {
                    rightTypeBean.parseRowStr(squest);
                    ou.write(rightTypeBean.getListViewData().getBytes());
                } else if (fLag.equalsIgnoreCase("add")) {
                    rightTypeBean.parseRowStr(squest);
                    rightTypeBean.checkInput(YssCons.OP_ADD);
                    rightTypeBean.saveSetting(YssCons.OP_ADD);
                    ou.write(rightTypeBean.getListViewData().getBytes());
                  // -----------add by guojianhua 增加日志操作  2010 09 14 ------------
                    rightTypeBean.setModuleName("system");
                    rightTypeBean.setFunName("RightType");
                    rightTypeBean.setRefName("000118");
                    //add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    operType = YssCons.OP_ADD;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(rightTypeBean,YssCons.OP_ADD, pub);
                    //---------------  end  --------------------
                } else if (fLag.equalsIgnoreCase("edit")) {
                    rightTypeBean.parseRowStr(squest);
                    rightTypeBean.checkInput(YssCons.OP_EDIT);
                    //---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(rightTypeBean);
					//---add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
                    
                    rightTypeBean.saveSetting(YssCons.OP_EDIT);
                    ou.write(rightTypeBean.getListViewData().getBytes());
                    //-----------add by guojianhua 增加日志操作  2010 09 14 ------------
                    rightTypeBean.setModuleName("system");
                    rightTypeBean.setFunName("RightType");
                    rightTypeBean.setRefName("000118");
                    //add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    operType = YssCons.OP_EDIT;
                    logOper.setIData(rightTypeBean,YssCons.OP_EDIT, pub);
                    //---------------  end  --------------------
                } else if (fLag.equalsIgnoreCase("del")) {
                    rightTypeBean.parseRowStr(squest);
                    rightTypeBean.checkInput(YssCons.OP_DEL);
                    rightTypeBean.saveSetting(YssCons.OP_DEL);
                    ou.write(rightTypeBean.getListViewData().getBytes());
                  //-----------add by guojianhua 增加日志操作  2010 09 14 ------------
                    rightTypeBean.setModuleName("system");
                    rightTypeBean.setFunName("RightType");
                    rightTypeBean.setRefName("000118");
                    //add by songjie 2012.08.14 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A
                    operType = YssCons.OP_DEL;
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(rightTypeBean,YssCons.RS_DEL, pub);
                    //---------------  end  --------------------
                }
            } else if (beanId.equalsIgnoreCase("operation")) {
                OperationTypeBean operation = new OperationTypeBean();
                operation.setYssPub(pub);
                if (fLag.equalsIgnoreCase("listview1")) {
                    operation.parseRowStr(squest);
                    ou.write(operation.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("add")) {
                    operation.parseRowStr(squest);
                    operation.checkInput(YssCons.OP_ADD);
                    operation.saveOperationType();
                    ou.write(operation.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("del")) {
                    operation.parseRowStr(squest);
                    operation.checkInput(YssCons.OP_DEL);
                    operation.delOperationType();
                    ou.write(operation.getListViewData1().getBytes());
                } else if (fLag.equalsIgnoreCase("edit")) {
                    operation.parseRowStr(squest);
                    operation.checkInput(YssCons.OP_EDIT);
                    operation.editOperationType();
                    ou.write(operation.getListViewData1().getBytes());
                    
                }
            }
            //20120725 added  by liubo.Story #2737.权限继承设置
            //====================================
            else if (beanId.equalsIgnoreCase("PerInheritancet"))
            {
            	PerInheritance per = new PerInheritance();
            	per.setYssPub(pub);
            	if (fLag.equalsIgnoreCase("listview1")) {
            		per.parseRowStr(squest);
                    ou.write(per.getListViewData1().getBytes());
                }
            	else if (fLag.equalsIgnoreCase("add")) {
            		per.parseRowStr(squest);
            		per.checkInput(YssCons.OP_ADD);
            		per.addSetting();
                    ou.write(per.getListViewData1().getBytes());
                } 
            	else if (fLag.equalsIgnoreCase("del")) {
            		per.parseRowStr(squest);
            		per.delSetting();
                    ou.write(per.getListViewData1().getBytes());
                } 
            	else if (fLag.equalsIgnoreCase("edit")) {
            		per.parseRowStr(squest);
            		per.checkInput(YssCons.OP_EDIT);
            		per.editSetting();
                    ou.write(per.getListViewData1().getBytes());
                }
            	else if (fLag.equalsIgnoreCase("clear")) {
            		per.parseRowStr(squest);
            		per.deleteRecycleData();
            		ou.write(per.getListViewData1().getBytes());
            	}
            	else if (fLag.equalsIgnoreCase("audit")) {
            		per.parseRowStr(squest);
            		per.checkSetting();
            		ou.write(per.getListViewData1().getBytes());
            	}
            	else if (fLag.equalsIgnoreCase("revert")) {
            		per.parseRowStr(squest);
            		per.checkSetting();
            		ou.write(per.getListViewData1().getBytes());
            	}
            	else if (fLag.equalsIgnoreCase("assetgrouptree"))
            	{
            		ou.write(per.getAssetGroupTreeViewData(squest).getBytes());
            	}
            	else if (fLag.equalsIgnoreCase("trustorname"))
            	{
            		ou.write(per.ConvertTrusteeNames(squest).getBytes());
            	}
            }
            //=================end===================
        } catch (Exception ye) {
		   //---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A start---//
		    try{
        		  logOper = SingleLogOper.getInstance();
                if(cgb != null){
                	logOper.setIData(cgb, operType, pub, true);
                }
                if(ub != null){
                	logOper.setIData(ub, operType, pub, true);
                }
                if(right != null){
                	logOper.setIData(right, operType, pub, true);
                }
                if(role != null){
                	logOper.setIData(role, operType, pub, true);
                }
                if(rightTypeBean != null){
                	logOper.setIData(rightTypeBean, operType, pub, true);
                }
        	}catch(Exception e){
        		e.printStackTrace();
        	}
			//---add by songjie 2012.09.07 STORY #2344 QDV4赢时胜(上海开发部)2012年3月2日05_A end---//
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.getLocalizedMessage().getBytes()); //使用新的异常处理方法 by caocheng 2009.03.03
            ou.close();
        }
        //-------- MS00003 QDV4.1-参数布局散乱不便操作 2009.03.10 蒋锦------------------------------
        finally {
            try {
                if (null != pub.getUserCode() && pub.getUserCode().length() > 0) {
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLog( (Boolean) userTable.get(pub.
                        getUserCode())); //操作流程状态和日志数据
                    userTable.remove(pub.getUserCode()); //清除个用户的信息.
                }
            } catch (YssException ex) {
                ou.write(ex.getLocalizedMessage().getBytes());
            }
            pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
            pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
        } 
        //------------------------------------------------------------------------
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
