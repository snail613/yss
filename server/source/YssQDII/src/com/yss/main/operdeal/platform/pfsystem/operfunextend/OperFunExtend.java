/*package com.yss.main.operdeal.platform.pfsystem.operfunextend;

import com.yss.dsub.*;
import com.yss.util.YssException;
import com.yss.main.operdeal.platform.pfsystem.operfunextend.pojo.OperFunExtendPojo;
import com.yss.base.BaseCalcFormula;
import com.yss.util.YssFun;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.lang.reflect.*;
import java.io.FileWriter;
import java.security.SecureClassLoader;
import java.io.BufferedWriter;

*//**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 *//*
public class OperFunExtend
    extends BaseCalcFormula {

    private StringBuffer bufTemplate = null;
    private String sLinkModule = "";
    private String sScriptParams = "";
    private String sObjParams = "";
    private String sBaseClass = "";
    private java.util.Date beginDate = null;
    private java.util.Date endDate = null;
    private String sPortCode = "";
    private String[] arrScriptParams = null;
    private String[] arrObjParams = null;

    public void setBeginDate(java.util.Date beginDate) {
        this.beginDate = beginDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    public void setPortCode(String sPortCode) {
        this.sPortCode = sPortCode;
    }

    public void setLinkModule(String sLinkModule) {
        this.sLinkModule = sLinkModule;
    }

    public void setScriptParams(String sScriptParams) {
        this.sScriptParams = sScriptParams;
    }

    public void setObjParams(String sObjParams) {
        this.sObjParams = sObjParams;
    }

    public void setBaseClass(String sBaseClass) {
        this.sBaseClass = sBaseClass;
    }

    public String getLinkModule() {
        return this.sLinkModule;
    }

    public String getScriptParams() {
        return this.sScriptParams;
    }

    public String getObjParams() {
        return this.sObjParams;
    }

    public String getBaseClass() {
        return this.sBaseClass;
    }

    public OperFunExtend() {
        bufTemplate = new StringBuffer(10000);
        bufTemplate.append("package com;").append("\n");
        bufTemplate.append("import java.sql.ResultSet;").append("\n");
        bufTemplate.append("import com.yss.util.*;").append("\n");
        bufTemplate.append("import com.yss.vsub.*;").append("\n");
        bufTemplate.append("import com.yss.dsub.*;").append("\n");
        bufTemplate.append("import com.yss.main.operdeal.*;").append("\n");
        bufTemplate.append("//import com.yss.pojo.sys.*;;").append("\n");
        bufTemplate.append("//import com.yss.main.operdeal.income.stat.StatAccInterest;").append("\n");
        bufTemplate.append("import com.yss.util.YssException;").append("\n");
        bufTemplate.append("import com.yss.util.YssD;").append("\n");
        bufTemplate.append("import com.yss.util.YssOperCons;").append("\n");
        bufTemplate.append("import com.yss.dsub.BaseBean;").append("\n");
        bufTemplate.append("import java.sql.Connection;").append("\n");
        bufTemplate.append("import com.yss.manager.CashPayRecAdmin;").append("\n");
        bufTemplate.append("import com.yss.util.YssFun;").append("\n");
        bufTemplate.append("import com.yss.main.operdata.CashPecPayBean;").append("\n");
        bufTemplate.append("import com.yss.main.operdeal.*;").append("\n");
        bufTemplate.append("public class DynamicExtendBean {").append("\n");
        bufTemplate.append("   private java.util.Date beginDate = null;").append("\n");
        bufTemplate.append("   protected YssPub pub = null; //全局变量").append("\n");
        bufTemplate.append("   protected DbBase dbl = null; //数据连接已经处理").append("\n");
        bufTemplate.append("   private BaseOperDeal settingOper;").append("\n");
        bufTemplate.append("   private java.util.Date endDate = null;").append("\n");
        bufTemplate.append("   private String portCodes = \"\";").append("\n");
        bufTemplate.append("   public void setBeginDate(java.util.Date beginDate){").append("\n");
        bufTemplate.append("      this.beginDate = beginDate;").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("   public void setSettingOper(BaseOperDeal settingOper) {").append("\n");
        bufTemplate.append("      this.settingOper = settingOper;").append("\n");
        bufTemplate.append("      settingOper.setYssPub(pub);").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("   public void setEndDate(java.util.Date endDate){").append("\n");
        bufTemplate.append("      this.endDate = endDate;").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("   public void setPortCodes(String portCodes){").append("\n");
        bufTemplate.append("      this.portCodes = portCodes;").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("   public void setYssPub(YssPub ysspub) {").append("\n");
        bufTemplate.append("    pub = ysspub;").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("   public void setYssDbl(DbBase dbl) {").append("\n");
        bufTemplate.append("    this.dbl = dbl;").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("   //public BaseOperDeal getSettingOper() {").append("\n");
        bufTemplate.append("   //   if (settingOper == null) {").append("\n");
        bufTemplate.append("   //      settingOper = (BaseOperDeal) pub.getOperDealCtx().getBean(").append("\n");
        bufTemplate.append("   //            \"baseoper\");").append("\n");
        bufTemplate.append("    //     if (pub != null) {").append("\n");
        bufTemplate.append("   //         settingOper.setYssPub(pub);").append("\n");
        bufTemplate.append("   //      }").append("\n");
        bufTemplate.append("   //   }").append("\n");
        bufTemplate.append("   // return settingOper;").append("\n");
        bufTemplate.append("   //}").append("\n");
        bufTemplate.append("   public DynamicExtendBean() {").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("   public void doDynamicFun() throws YssException{").append("\n");
        bufTemplate.append("      ResultSet rs1 = null;").append("\n");
        bufTemplate.append("      ResultSet rs2 = null;").append("\n");
        bufTemplate.append("      ResultSet rs3 = null;").append("\n");
        bufTemplate.append("      ResultSet rs4 = null;").append("\n");
        bufTemplate.append("      Connection conn = dbl.loadConnection();").append("\n");
        bufTemplate.append("      boolean bTrans = false;").append("\n");
        bufTemplate.append("      try{").append("\n");
        bufTemplate.append("         bTrans = true;").append("\n");
        bufTemplate.append("         conn.setAutoCommit(false);").append("\n");
        bufTemplate.append("         <@@@@@>").append("\n");
        bufTemplate.append("         conn.commit();").append("\n");
        bufTemplate.append("         bTrans = false;").append("\n");
        bufTemplate.append("         conn.setAutoCommit(true);").append("\n");
        bufTemplate.append("      }").append("\n");
        bufTemplate.append("      catch(Exception e){").append("\n");
        bufTemplate.append("         throw new YssException(e.getMessage());").append("\n");
        bufTemplate.append("      }").append("\n");
        bufTemplate.append("      finally{").append("\n");
        bufTemplate.append("         dbl.closeResultSetFinal(rs1);").append("\n");
        bufTemplate.append("         dbl.closeResultSetFinal(rs2);").append("\n");
        bufTemplate.append("         dbl.closeResultSetFinal(rs3);").append("\n");
        bufTemplate.append("         dbl.closeResultSetFinal(rs4);").append("\n");
        bufTemplate.append("         dbl.endTransFinal(conn, bTrans);").append("\n");
        bufTemplate.append("      }").append("\n");
        bufTemplate.append("   }").append("\n");
        bufTemplate.append("}").append("\n");
    }

    public void parseScriptParams() throws YssException {
        try {
            arrScriptParams = this.sScriptParams.split("\t");
            arrObjParams = this.sObjParams.split("\t");
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public void doDynamicArithmetic() throws YssException {
        ArrayList alExtendInfo = null;
        String sJavaCode = ""; //解析后的 Java 代码
        String sBeanCode = ""; //解析后完整的 Bean 代码
        File goldFile = null;
        File beanFile = null;
        Object[] obj = new Object[0];
        Object[] objPub = new Object[] {
            pub};
        Class[] cls = new Class[0];
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            //获取所有关联的业务扩展配置信息
            alExtendInfo = getExtendInfo(this.sLinkModule);
            //循环执行所有扩展脚本
            int iInfoLen = alExtendInfo.size();
            for (int i = 0; i < iInfoLen; i++) {
                OperFunExtendPojo funPojo = (OperFunExtendPojo) alExtendInfo.get(i);
                //解析脚本参数
                parseScriptParams();
                //构建 Java 代码
                sJavaCode = buildJavaCode(funPojo.getExtScript(), funPojo.getPubParaCode());
                //使用解析脚本后生成的 Java 代码替换模版中的占位符
                sBeanCode = this.bufTemplate.toString().replaceAll("<@@@@@>", sJavaCode);
                String path = Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1);
                String tempPath = path.substring(0, path.length() - 24);
                goldFile = new File(tempPath.replaceAll("%20", " ") +
                                    //"src\\com\\yss\\main\\operdeal\\platform\\pfsystem\\operfunextend");
                                    "src\\com");
//            String tempPath=path.substring(0,path.length() - 16);
//            goldFile = new File(tempPath.replaceAll("%20", " ") +
//                                "//tempFile");

                if (!goldFile.exists()) {
                    goldFile.mkdirs();
                }
                //--------------写文件---------------//
                beanFile = new File(goldFile.getAbsolutePath() + "\\DynamicExtendBean.java");
                if (beanFile.exists()) {
                    beanFile.delete();
                }
                beanFile.createNewFile();
                fw = new FileWriter(beanFile);
                bw = new BufferedWriter(fw);
                bw.write(sBeanCode);
                bw.close();
                fw.close();

                //   fw.write(sBeanCode);
                //   fw.flush();
                //-----------------------------------//
                //--------------编译-----------------//
                String[] args = new String[] {
                    "-d", path.replaceAll("%20", " "), "-classpath",
                    path.replaceAll("%20", " "), beanFile.getAbsolutePath()};
                Main javac = new Main();
                javac.compile(args);
                //------------------------------------//
                //---------------调用------------------//
                //ClassLoader loader = ClassLoader.getSystemClassLoader();
                FileClassLoader loader = new FileClassLoader(path.replaceAll("%20", " "),
                    "com.DynamicExtendBean");
                Class c = loader.findClass("com\\DynamicExtendBean");
                //Class c=Class.forName("com.yss.main.operdeal.platform.operfunextend.DynamicExtendBean");
                //c = loader.loadClass("DynamicExtendBean.Class");
                //  Class c = Class.forName("com.yss.main.operdeal.platform.operfunextend.DynamicExtendBean");
//            clsLoader = c.getClassLoader();

                Object o = c.newInstance();
                Class clsPub = Class.forName("com.yss.dsub.YssPub");
                Class[] arrClsPub = new Class[] {
                    clsPub};
                Method m = c.getMethod("setYssPub", arrClsPub);
                m.invoke(o, objPub);
                m = c.getMethod("setYssDbl", new Class[] {Class.forName("com.yss.dsub.DbBase")});
                m.invoke(o, new Object[] {dbl});
                m = c.getMethod("setSettingOper", new Class[] {Class.forName("com.yss.main.operdeal.BaseOperDeal")});
                m.invoke(o, new Object[] {this.getSettingOper()});
                if (this.beginDate != null) {
                    Object[] objBegDate = new Object[] {
                        this.beginDate};
                    Class clsBegDate = Class.forName("java.util.Date");
                    Class[] arrClsBegDate = new Class[] {
                        clsBegDate};
                    m = c.getMethod("setBeginDate", arrClsBegDate);
                    m.invoke(o, objBegDate);
                }
                if (this.endDate != null) {
                    Object[] objEndDate = new Object[] {
                        this.endDate};
                    Class clsEndDate = Class.forName("java.util.Date");
                    Class[] arrClsEndDate = new Class[] {
                        clsEndDate};
                    m = c.getMethod("setEndDate", arrClsEndDate);
                    m.invoke(o, objEndDate);
                }
                if (this.sPortCode.length() != 0) {
                    Object[] objPort = new Object[] {
                        this.sPortCode};
                    Class clsPort = Class.forName("java.lang.String");
                    Class[] arrClsPort = new Class[] {
                        clsPort};
                    m = c.getMethod("setPortCodes", arrClsPort);
                    m.invoke(o, objPort);
                }
                m = c.getMethod("doDynamicFun", cls);
                m.invoke(o, obj);
                int j = 0;
                //------------------------------------//
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public ArrayList getExtendInfo(String sLinkModule) throws YssException {
        ArrayList alInfo = new ArrayList();
        OperFunExtendPojo extendPojo = null;
        String strSql = "";
        ResultSet rs = null;
        try {
            if (sLinkModule.length() == 0) {
                throw new YssException("调用方没有输入关联模块代码！");
            }
            strSql = "SELECT * FROM TB_PFSys_OperFunExtend " +
                " WHERE FLinkModule = " + dbl.sqlString(sLinkModule) +
                " AND FEnable = 1" +
                " AND FCheckState = 1";
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                extendPojo = new OperFunExtendPojo();
                extendPojo.setExtCode(rs.getString("FExtCode"));
                extendPojo.setExtName(rs.getString("FExtName"));
                extendPojo.setPubParaCode(rs.getString("FPubParaCode"));
                extendPojo.setLinkModule(rs.getString("FLinkModule"));
                extendPojo.setExtScript(rs.getString("FExtScript"));
                extendPojo.setEnable(rs.getInt("FEnable"));
                extendPojo.setDesc(rs.getString("FDesc"));
                alInfo.add(extendPojo);
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return alInfo;
    }

    public String replaceParams(String sScript, boolean bIsSql) throws YssException {
        try {
            String sInd = "", sInd2 = ""; //参数的标识
            String sDataType = ""; //数据类型的标识 S:字符型,I:数字型,D:日期型
            int iPos = 0;
            String sSqlValue = "";
            String sObjName = "";
            if (bIsSql) {
                for (int i = 0; i < this.arrScriptParams.length; i++) {
                    sInd = "<" + String.valueOf(i + 1) + ">";
                    iPos = sScript.indexOf(sInd);
                    if (iPos <= 0) {
                        sInd = " < " + String.valueOf(i + 1) + " >";
                        iPos = sScript.indexOf(sInd);
                    }
                    if (iPos > 1) {
                        sDataType = sScript.substring(iPos - 1, iPos);
                        if (sDataType.equalsIgnoreCase("S")) {
                            sSqlValue = dbl.sqlString(this.arrScriptParams[i]);
                        } else if (sDataType.equalsIgnoreCase("I")) {
                            sSqlValue = this.arrScriptParams[i];
                        } else if (sDataType.equalsIgnoreCase("D")) {
                            //转换成日期
                            sSqlValue = dbl.sqlDate(YssFun.formatDate(this.
                                arrScriptParams[i]));
                        } else if (sDataType.equalsIgnoreCase("N")) {
                            //转换代码，例如 001,002转换成'001','002'
                            sSqlValue = operSql.sqlCodes(this.arrScriptParams[i]);
                        }
                        sScript = sScript.replaceAll(sDataType + sInd, sSqlValue);
                    }
                }
                if (sScript.indexOf("<U>") > 0) {
                    sScript = sScript.replaceAll("<U>", pub.getUserCode());
                } else if (sScript.indexOf("< U >") > 0) {
                    sScript = sScript.replaceAll("< U >", pub.getUserCode());
                }
            } else {
                for (int i = 0; i < this.arrObjParams.length; i++) {
                    sInd = "<" + String.valueOf(i + 1) + ">";
                    iPos = sScript.indexOf(sInd);
                    if (iPos <= 0) {
                        sInd = " < " + String.valueOf(i + 1) + " >";
                        iPos = sScript.indexOf(sInd);
                    }
                    if (iPos > 1) {
                        sDataType = sScript.substring(iPos - 1, iPos);
                        sObjName = arrObjParams[i];
                        sScript = sScript.replaceAll(sDataType + sInd, sObjName);
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("脚本参数替换出错！", e);
        }
        return sScript;
    }

    public String replacePubParam(String sScript, String sPubParaCode, boolean bIsSql) throws YssException {
        ResultSet rs = null;
        String strSql = "";
        try {
            if (sPubParaCode == null || sPubParaCode.length() == 0) {
                return sScript;
            }
            strSql = "SELECT a.FCtlValue, b.FCtlType, b.FCtlInd" +
                " FROM " + pub.yssGetTableName("TB_PFOper_PUBPARA") + " a" +
                " JOIN Tb_PFSys_FaceCfgInfo b ON a.FCtlGrpCode = b.FCtlGrpCode" +
                " AND a.FCtlCode = b.FCtlCode" +
                " WHERE a.FPubParaCode = " + dbl.sqlString(sPubParaCode);
            rs = dbl.openResultSet(strSql);
            while (rs.next()) {
                int iCtlType = rs.getInt("FCtlType");
                String sCtlValue = rs.getString("FCtlValue").trim();
                String sCtlInd = rs.getString("FCtlInd").trim();
                if (sCtlInd == null && sCtlInd.length() == 0) {
                    continue;
                }
                if (bIsSql) {
                    if (iCtlType == 1) {
                        if (sCtlInd.substring(0, 1).equalsIgnoreCase("s")) {
                            sScript = sScript.replaceAll(sCtlInd, dbl.sqlString(sCtlValue));
                        } else {
                            sScript = sScript.replaceAll(sCtlInd, sCtlValue);
                        }
                    } else if (iCtlType == 3) {
                        sScript = sScript.replaceAll(sCtlInd, dbl.sqlDate(sCtlValue));
                    } else if (iCtlType == 2) {
                        if (sCtlInd.substring(0, 1).equalsIgnoreCase("s")) {
                            sScript = sScript.replaceAll(sCtlInd, dbl.sqlString(sCtlValue.split(",")[0]));
                        } else {
                            sScript = sScript.replaceAll(sCtlInd, sCtlValue.split(",")[0]);
                        }
                    } else if (iCtlType == 4) {
                        if (sCtlInd.substring(0, 1).equalsIgnoreCase("s")) {
                            sScript = sScript.replaceAll(sCtlInd, dbl.sqlString(sCtlValue.split("[|]")[0]));
                        } else {
                            sScript = sScript.replaceAll(sCtlInd, sCtlValue.split("[|]")[0]);
                        }
                    }
                } else {
                    if (iCtlType == 1) {
                        if (sCtlInd.substring(0, 1).equalsIgnoreCase("s")) {
                            sScript = sScript.replaceAll(sCtlInd, "\"" + sCtlValue + "\"");
                        } else {
                            sScript = sScript.replaceAll(sCtlInd, sCtlValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new YssException("替换通用业务参数出错！", e);
        } finally {
            dbl.closeResultSetFinal(rs);
        }
        return sScript;
    }

    public String buildJavaCode(String sScript, String sPubParaCode) throws YssException {
        String sForDate = "";
        String sFunStr = "";
        String sJavaCode = "";
        try {
            //(?<=\\s{1}|^)\\S+\\[.+?\\](?=\\s{1}|$|\\}|\\))
            sScript = sScript.replaceAll("[\\r\\n]", " ");
            //---------------------处理 for_date[] 函数----------------------//
            Pattern pForDate = Pattern.compile("for_date\\[.+?\\]\\s?\\{.+\\}", Pattern.CASE_INSENSITIVE);
            boolean bIsFind = true;
            while (bIsFind) {
                Matcher m = pForDate.matcher(sScript);
                if (!m.find()) {
                    bIsFind = false;
                    break;
                }
                sForDate = m.group();
                int iStart = m.start();
                int iEnd = m.end();
                sScript = sScript.substring(0, iStart) + "<@>" + sScript.substring(iEnd);
                String sDateCode = getForDateFunctionString(sForDate);
                sScript = sScript.replaceAll("<@>", sDateCode);
            }
            //-------------------------------------------------------------//
            Pattern p = Pattern.compile("(?<=\\s{1}|^)\\S+\\[.+?\\]");
            bIsFind = true;
            while (bIsFind) {
                Matcher m = p.matcher(sScript);
                if (!m.find()) {
                    bIsFind = false;
                    break;
                }
                sFunStr = m.group();
                //替换调用方传递参数
                sFunStr = replaceParams(sFunStr, true);
                //替换通用业务参数中的参数
                sFunStr = replacePubParam(sFunStr, sPubParaCode, true);
                int iStart = m.start();
                int iEnd = m.end();
                sScript = sScript.substring(0, iStart) + "<@>" + sScript.substring(iEnd);
                sJavaCode = (String) getFormulaValue(sFunStr);
                sScript = sScript.replaceAll("<@>", sJavaCode);
                sScript = sScript.replaceAll(";", ";\n");
                sScript = sScript.replaceAll("[{]", "{\n");
                sScript = sScript.replaceAll("[}]", "\n}\n");
            }
            //替换 Java 代码中的调用方传递参数
            sScript = replaceParams(sScript, false);
            //替换 Java 代码中的通用业务参数
            sScript = replacePubParam(sScript, sPubParaCode, false);
        } catch (Exception e) {
            throw new YssException("创建 Java 代码出错！", e);
        }
        return sScript;
    }

    public Object getExpressValue(String sExpress, ArrayList alParams) throws YssException {
        StringBuffer buf = new StringBuffer();
        String sParams = (String) alParams.get(0);
        try {
            if (sExpress.toLowerCase().indexOf("openresult") > -1) {
                int i = sExpress.indexOf("=");
                if (i > -1) {
                    buf.append(sExpress.substring(0, i) + "=dbl.openResultSet(\"" + (String) alParams.get(0) + "\");");
                }
                return buf.toString();
            } else if (sExpress.equalsIgnoreCase("save")) {
                Pattern p = Pattern.compile("VALUES( *)\\(.+?\\)", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(sParams);
                if (m.find()) {
                    String sReplace = m.group();
                    sParams = sParams.substring(0, m.start()) + "<@>" +
                        sParams.substring(m.end());
                    sReplace = sReplace.replaceAll("\\(", "(\" + ");
                    sReplace = sReplace.replaceAll("\\)", " + \")");
                    sReplace = sReplace.replaceAll(",", "+ \",\" +");
                    sParams = sParams.replaceAll("<@>", sReplace);
                }
                buf.append("dbl.executeSql(\"" + sParams + "\");");
                return buf.toString();
            } else if (sExpress.equalsIgnoreCase("for_date")) {
                return alParams;
            }
            return null;
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public Object getKeywordValue(String sKeyword) throws YssException {
        Object objResult = sKeyword;
        return objResult;
    }

    public String getForDateFunctionString(String sFunStr) throws YssException {
        ArrayList alDateParams = null;
        String sFunHead = "";
        String sDateCode = "";
        try {
            Pattern pHead = Pattern.compile("\\S+\\[.+?\\]");
            Matcher mHead = pHead.matcher(sFunStr);
            if (mHead.find()) {
                sFunHead = mHead.group();
            } else {
                throw new YssException("for_date[] 函数语法错误！");
            }
            sFunHead = this.replaceParams(sFunHead, false);
            alDateParams = (ArrayList) getFormulaValue(sFunHead);
            String sBeginDate = (String) alDateParams.get(0);
            String sEndDate = (String) alDateParams.get(1);
            String sVar = (String) alDateParams.get(2);
            sDateCode = "java.util.Date " + sVar + " = null; ";
            sDateCode += "int iDay = YssFun.dateDiff(" + sBeginDate + ", " +
                sEndDate + "); ";
            sDateCode += sVar + " = YssFun.addDay(" + sBeginDate + ", " + "-1); ";
            sDateCode += "for(int iDays = 0; iDays <= iDay; iDays++){";
            sDateCode += sVar + " = YssFun.addDay(" + sVar + ", " + "1);";
            sFunStr = sFunStr.replaceAll("for_date\\[.+?\\]\\s?\\{{1}", sDateCode);
        } catch (Exception e) {
            throw new YssException("替换日期循环函数出错！", e);
        }
        return sFunStr;
    }

}
*/