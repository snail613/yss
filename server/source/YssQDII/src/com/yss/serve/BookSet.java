package com.yss.serve;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.springframework.context.*;
import org.springframework.context.support.*;
import com.yss.dsub.*;
import com.yss.log.*;
import com.yss.main.dao.*;
import com.yss.util.*;

public class BookSet
    extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ApplicationContext ctx;

    public BookSet() {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312");
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = response.getOutputStream();
        String squest = com.yss.util.YssUtil.getBytesStringFromClient(request);
        System.out.println(squest);
        String beanId = request.getParameter("cmd");
        String flag = request.getParameter("flag");
        String showType = request.getParameter("showtype");
        YssPub pub = null;

        Object obj = null;
        IDataSetting bean = null;
        int operType = 0;
        SingleLogOper logOper = null;
        try {
            pub = YssUtil.getSessionYssPub(request);
            if (ctx == null) {
			//调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
			//BUG7549 panjunfang modify 20130428				
                String path = this.getServletContext().getRealPath("");

                ctx = new FileSystemXmlApplicationContext(
    					YssUtil.getAppConContextPath(path,"voucher.xml"));
//                pub.setParaSettingCtx(ctx);//delete by xuxuming,20091209.MS00851    凭证模板界面中，“估值方法设置”报错   
                pub.setVoucherCtx(ctx);//add by xuxuming,保存到相应的容器中
            }
            obj = ctx.getBean(beanId.toLowerCase());
            if (obj instanceof IDataSetting) {
                bean = (IDataSetting) obj;
                bean.setYssPub(pub);

                if (flag.equalsIgnoreCase("add")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_ADD);
                    operType = YssCons.OP_ADD;
                    bean.addSetting();

                    //-----------------------------------------
//               OperateLogBean log = new OperateLogBean();
//               log.setOperateType(YssCons.OP_ADD);

//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);

                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_ADD, pub);
                } else if (flag.equalsIgnoreCase("edit")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_EDIT);

                    //---------------------------------------------------------
//               OperateLogBean log = new OperateLogBean();
//               log.setBData( ( (IDataSetting) obj).getBeforeEditData());
                    //---------------------------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setBData(bean);
                    operType = YssCons.OP_EDIT;
                    bean.editSetting();

                    //-------------------------------------------------
//               log.setOperateType(YssCons.OP_EDIT);

//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);
                    logOper.setIData(bean, YssCons.OP_EDIT, pub);
                } else if (flag.equalsIgnoreCase("del")) {
                    bean.parseRowStr(squest);
                    bean.checkInput(YssCons.OP_DEL);
                    operType = YssCons.RS_DEL;
                    bean.delSetting();

                    //-----------------------------------------
//               OperateLogBean log = new OperateLogBean();
//               log.setOperateType(YssCons.OP_DEL);

//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);
                    //-----------------------------------------
                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_DEL, pub);
                } else if (flag.equalsIgnoreCase("audit")) {
                    bean.parseRowStr(squest);
//             bean.checkInput(YssCons.OP_AUDIT);
                    operType = YssCons.OP_AUDIT;
                    bean.checkSetting();
                    //-----------------------------------------
//               OperateLogBean log = new OperateLogBean();
//
//               log.setOperateType(YssCons.OP_AUDIT);

//
//               log.setOperateResult("1");
//               log.setYssPub(pub);
//               log.insertLog(bean);

                    logOper = SingleLogOper.getInstance();
                    logOper.setIData(bean, YssCons.OP_AUDIT, pub);
                }

                if (showType != null && showType.length() > 0) {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, showType).getBytes());
                } else {
                    bean.parseRowStr(squest);
                    ou.write(getData(bean, flag).getBytes());
                }

            }
        } catch (Exception ex) {
            //-----------------------------------------
            try {
                logOper = SingleLogOper.getInstance();
                logOper.setIData(bean, operType, pub, true);
            } catch (YssException e) {
                e.printStackTrace();
            }
            //-----------------------------------------
        }
    }

    private String getData(IDataSetting bean, String sShowType) throws
        YssException {
        String sResult = "";
        //用于获取前台查询列表的ListView控件的当前可用的数据
        if (sShowType.equalsIgnoreCase("listview1")) {
            sResult = bean.getListViewData1();
        }
        //用于获取前台选择框ListView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("listview2")) {
            sResult = bean.getListViewData2();
        }
        //用于获取前台特殊ListView控件的数据
        else if (sShowType.equalsIgnoreCase("listview3")) {
            sResult = bean.getListViewData3();
        }
        //用于获取前台查询列表的ListView控件的所有的数据
        else if (sShowType.equalsIgnoreCase("listview4")) {
            sResult = bean.getListViewData4();
        }
        //用于获取前台查询列表的TreeView控件所有的数据
        else if (sShowType.equalsIgnoreCase("treeview1")) {
            sResult = bean.getTreeViewData1();
        }
        //用于获取前台选择框TreeView控件的当前可用的数据
        else if (sShowType.equalsIgnoreCase("treeview2")) {
            sResult = bean.getTreeViewData2();
        } else if (sShowType.equalsIgnoreCase("treeview3")) {
            sResult = bean.getTreeViewData3();
        }
        /// <summary>
        /// 修改人：panjunfang
        /// 修改时间:20090514
        /// BugNO  :MS00001  QDV4.1赢时胜（上海）2009年4月20日01_A》跨组合群
        /// 在这加的方法查出组合群代码和组合代码这是因为前台以前调用opervalue不在listview里面
        else if (sShowType.equalsIgnoreCase("grouplistview1")) {
            sResult = bean.getListViewGroupData1();
        } else if (sShowType.equalsIgnoreCase("grouplistview2")) {
            sResult = bean.getListViewGroupData2();
        } else if (sShowType.equalsIgnoreCase("grouplistview3")) {
            sResult = bean.getListViewGroupData3();
        } else if (sShowType.equalsIgnoreCase("grouplistview4")) {
            sResult = bean.getListViewGroupData4();
        } else if (sShowType.equalsIgnoreCase("grouplistview5")) {
            sResult = bean.getListViewGroupData5();
        } else if (sShowType.equalsIgnoreCase("grouptreeview1")) {
            sResult = bean.getTreeViewGroupData1();
        } else if (sShowType.equalsIgnoreCase("grouptreeview2")) {
            sResult = bean.getTreeViewGroupData2();
        } else if (sShowType.equalsIgnoreCase("grouptreeview3")) {
            sResult = bean.getTreeViewGroupData3();
        }

        return sResult;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException,
        ServletException {
        doGet(request, response);
    }
}
