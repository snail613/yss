package com.yss.serve;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.yss.dsub.*;
import com.yss.main.funsetting.*;
import com.yss.main.syssetting.*;
import com.yss.tools.CreateXML;
import com.yss.util.*;

public class RightServer
    extends HttpServlet {
    /**
	 * 
	 */
	//---------------------彭鹏    20110517    自动更新---------------------//
	public static final String version = "1.0.1.0060sp4em5"; //版本号
	//-------------------------------------------------------------------//
	private static final long serialVersionUID = 1L;
	//----MS00003 QDV4.1-参数布局散乱不便操作 ---
    private Hashtable userTable = null;
    //----------------------------------------
    public RightServer() {
    }

    public void init() throws ServletException {
        //----MS00003 QDV4.1-参数布局散乱不便操作 ---
        userTable = new Hashtable(); //在servlet初始化时将此用户操作结果容器生成。
        //----------------------------------------
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws
        ServletException, IOException {
        request.setCharacterEncoding("GB2312"); //设置了以后，parameter就不要转编码了
        response.setContentType(YssCons.CONTENT_TEXT);
        OutputStream ou = (OutputStream) response.getOutputStream();
        String squest = YssUtil.getBytesStringFromClient(request);
        String beanId = request.getParameter("cmd");
        String fLag = request.getParameter("flag");
        String isBrown = request.getParameter("isBrown");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据  
        String tabMainCode = request.getParameter("tabMainCode");//add by guolongchao 20110916 STORY 1285 读数完成后是否浏览数据   
        YssPub pub = null;
        //---- MS00003 QDV4.1-参数布局散乱不便操作
        FlowBean flow = null;
        //-------------------------------------
        try {
            pub = YssUtil.getSessionYssPub(request);
            if(isBrown!= null && !isBrown.equals("")&&isBrown.equals("true"))          
                pub.setBrown(true);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现
            if(tabMainCode!= null && !tabMainCode.equals(""))          
                pub.setTabMainCode(tabMainCode);//add by guolongchao 20110916 STORY 1285QDV4嘉实基金2011年6月29日02_A代码实现   
            RightBean rb = new RightBean();
            rb.setYssPub(pub);
            if (beanId.equalsIgnoreCase("right")) {
                if (fLag.equalsIgnoreCase("queryRight")) {
                    String type = request.getParameter("type");
                    ou.write(rb.getRight("Tb_Fun_MenuBar", type).getBytes()); //modify by caocheng MS00001:QDV4.1赢时胜上海2009年2月1日01_A
                } else if (fLag.equalsIgnoreCase("queryOperation")) {
                    String type = request.getParameter("type");
                    ou.write(rb.getRight("Tb_Sys_OperationType", type).getBytes());

                } else if (fLag.equalsIgnoreCase("userRightQuery")) {
                    String type = request.getParameter("type");
                    String usercode = request.getParameter("usercode");
                    String assetgroup = request.getParameter("assetgroup");
                    String role = request.getParameter("role");
                    ou.write(rb.rightQuery(usercode, assetgroup, type, role).
                             getBytes());
                } else if (fLag.equalsIgnoreCase("userRightSave")) {
                    if (squest != null && squest.length() > 0) {
                        ou.write(rb.saveUserRight(squest).getBytes());
                    } else {
                        ou.write("true".getBytes());
                    }
                }
                //add by huangqirong 2011-06-25 story #937
                else if(fLag.equalsIgnoreCase("getportcodes")){
                	String portCode=request.getParameter("rightcode");
                	ou.write(rb.getUserPortCodes(portCode).getBytes());
                }
                //---end---
                
                //--------- 获取用户类型代码发送到前台 add by wangzuochun MS00010 2009.05.18
                //--------- QDV4赢时胜（上海）2009年02月01日10_A
                else if (fLag.equalsIgnoreCase("userTypeCode")) {
                    ou.write(rb.getUserTypecode().getBytes());
                }
                //----------
                // 如果当前没用用户，初始化一个系统权限用户，赋予系统权限
                // modify by caocheng MS00001:QDV4.1赢时胜上海2009年2月1日01_A
                else if (fLag.equalsIgnoreCase("userSysRightSave")) {
                    if (squest != null && squest.length() > 0) {
                        ou.write(rb.saveSysUserRight(squest).getBytes());
                    } else {
                        ou.write("true".getBytes());
                    }
                    if (pub.getUserCode() == null ||
                        pub.getUserCode().trim().length() == 0) {
                        return;
                    }
                }
                //===========================End MS00001============================
                else if (fLag.equalsIgnoreCase("rightIsNull")) {
                    ou.write(rb.getRightIsNull("Tb_Sys_UserList").getBytes());
                } else if (fLag.equalsIgnoreCase("AssetGroupIsNull")) {
                    ou.write(rb.getRightIsNull("Tb_Sys_AssetGroup").getBytes());
                }
            } else if (beanId.equalsIgnoreCase("userPass")) {
                UserRight rights = new UserRight(pub);

                if (fLag.equalsIgnoreCase("checkPass")) {
                    YssType expired = new YssType();
                    String user = request.getParameter("user");
                    String pass = request.getParameter("pass");
                    String set = request.getParameter("set");
                    if (rights.CheckPassword(user, set, pass, expired)) {
                        ou.write("yes".getBytes());
                    } else {
                        ou.write("no".getBytes());
                    }
                } else if (fLag.equalsIgnoreCase("amendPass")) {
                    String user = request.getParameter("user");
                    user=new String(user.getBytes("iso8859-1"),"GB2312");//by guyichuan 2011.08.05 BUG2370
                    String oldPass = request.getParameter("old");
                    String newPass = request.getParameter("new");
                    ou.write(rights.midPass(oldPass, newPass, user).getBytes());
                }
            }
        	//---------------------彭鹏    20110517    自动更新---------------------//
            else if(beanId.equalsIgnoreCase("version")){
            	String path = this.getServletContext().getRealPath("/") + "ClientUpdate"; //定义更新文件目录
        		File filePath = new File(path);
        		if(!filePath.exists()) return ;
        		File file = new File(path + "/UpdateList.xml");
        		if(file.exists()) file.delete();
        		CreateXML xml = new CreateXML();
        		xml.generateDocument(path, "/UpdateList.xml");
            	ou.write(this.version.getBytes()); //返回版本号
            }
        	//-------------------------------------------------------------------//
            //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
            userTable.put(pub.getUserCode(), new Boolean(true)); //当操作正确执行完成，则将正确的结果放入容器中
            //-------------------------------------------------
        } catch (Exception ye) {
            //----MS00003 QDV4.1-参数布局散乱不便操作 ------------
//            userTable.put(pub.getUserCode(), new Boolean(false)); //若操作失败，则将此结果放入容器中
            //-------------------------------------------------
            response.setHeader(YssCons.ERROR_TO_CLIENT, "error");
            //输出错误信息
            response.resetBuffer();
            ou.write(ye.toString().getBytes());
            ou.close();
        } finally {
            try {
                if (null!=null&& null != pub.getUserCode() && pub.getUserCode().trim().length() > 0) {
                    flow = new FlowBean();
                    flow.setYssPub(pub);
                    flow.ctlFlowStateAndLog( (Boolean) userTable.get(pub.
                        getUserCode())); //操作流程状态和日志数据
                    userTable.remove(pub.getUserCode()); //清除个用户的信息.
                    pub.setBrown(false);//add by guolongchao 20110916 STORY 1285 将pub中的isBrown属性设为false;
                    pub.setTabMainCode("");//add by guolongchao 20110916 STORY 1285 将pub中的tabMainCode属性清空;
                }
            } catch (YssException ex) {
                ou.write(ex.getLocalizedMessage().getBytes());
            }
           
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
