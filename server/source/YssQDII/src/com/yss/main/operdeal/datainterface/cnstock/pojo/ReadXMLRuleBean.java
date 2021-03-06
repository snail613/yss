package com.yss.main.operdeal.datainterface.cnstock.pojo;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import java.util.*;
import com.yss.dsub.BaseBean;
import com.yss.util.YssCons;
import com.yss.util.YssException;
import com.yss.util.YssUtil;

/**
 *
 * <p>Title: 读取国内接口的XML配置信息的类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: ysstech</p>
 *
 * @author by leeyu 200907814
 * @version 1.0
 */
public class ReadXMLRuleBean
    extends BaseBean{
    public ReadXMLRuleBean() {
    }
    private Document document = null;
    //private String fileName =""; //文件名与路径

    private String convertedSecCode = "";//内部代码(转换后)
    private String businessSign = "";    //业务标志
    private String secSign = "";         //证券标志
    private String securityCode="";      //证券代码(原始)
    private String resultType="";        //结果值类型,由证券标志+空格+业务标志组成

    /**
     * 读取上海过户库XML配置
     * @param XML的Rule的属性securityCode
     * @param XML的Result的属性resultType
     * @throws YssException
     */
    public void setSHGH(String securityCode,String resultType) throws YssException{
        this.securityCode = securityCode;
        this.resultType = resultType;
        set("SHGHconvertRule");
    }

    /**
     * 读取深圳回报库XML配置
     * @param XML的Rule的属性securityCode
     * @param XML的Result的属性resultType
     * @throws YssException
     */
    public void setSZHB(String securityCode,String resultType) throws YssException{
        this.securityCode = securityCode;
        this.resultType = resultType;
        set("SZHBconvertRule");
    }

    /**
     * 读取上海证券变动库的XML配置
     * @param XML的Rule的属性securityCode
     * @param XML的Result的属性resultType
     * @throws YssException
     */
    public void setSHZQBD(String securityCode,String resultType) throws YssException{
        this.securityCode = securityCode;
        this.resultType = resultType;
        set("SHZQBDconvertRule");
    }

    /**
     * 读取深圳股份库的XML配置
     * @param XML的Rule的属性securityCode
     * @param XML的Result的属性resultType
     * @throws YssException
     */
    public void setSZGF(String securityCode,String resultType) throws YssException{
        this.securityCode = securityCode;
        this.resultType = resultType;
        set("SZGFconvertRule");
    }

    /**
     * 读取深圳发行库的XML配置
     * @param XML的Rule的属性securityCode
     * @param XML的Result的属性resultType
     * @throws YssException
     */
    public void setSZFX(String securityCode,String resultType) throws YssException{
        this.securityCode = securityCode;
        this.resultType = resultType;
        set("SZFXconvertRule");
    }

    /**
     * 给成员变量赋值
     * @param sRoot 为XML的主根结点
     * @throws YssException
     */
    private void set(String sRoot) throws YssException{
        if (resultType != null ) {
            if(securityCode==null||securityCode.trim().length()==0){
                //获取证券代码对应的转换代码
                convertedSecCode = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"\"]/result[@resultType=\"" + resultType +"\"]/convertedSecCode");
                //获取证券代码对应的业务标志
                businessSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"\"]/result[@resultType=\"" + resultType + "\"]/businessSign");
                //获取证券代码对应的证券标志
                secSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"\"]/result[@resultType=\"" + resultType + "\"]/secSign");
            }else{
                //获取证券代码对应的转换代码
                convertedSecCode = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"" + securityCode + "\"]/result[@resultType=\"" + resultType +"\"]/convertedSecCode");
                //获取证券代码对应的业务标志
                businessSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"" + securityCode + "\"]/result[@resultType=\"" + resultType + "\"]/businessSign");
                //获取证券代码对应的证券标志
                secSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"" + securityCode + "\"]/result[@resultType=\"" + resultType + "\"]/secSign");
            }

        } else {
            if(securityCode==null || securityCode.trim().length()==0){
                //获取证券代码对应的转换代码
                convertedSecCode = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"\"]/convertedSecCode");
                //获取证券代码对应的业务标志
                businessSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"\"]/businessSign");
                //获取证券代码对应的证券标志
                secSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"\"]/secSign");
            }else{
                //获取证券代码对应的转换代码
                convertedSecCode = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"" + securityCode + "\"]/convertedSecCode");
                //获取证券代码对应的业务标志
                businessSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"" + securityCode + "\"]/businessSign");
                //获取证券代码对应的证券标志
                secSign = ReadRuleResult("/convertRule/"+sRoot+"/Rule[@securityCode=\"" + securityCode + "\"]/secSign");
            }
        }
    }
    /**
     * 读XML文件
     * @throws YssException
     */
    private void loadFile() throws YssException{
        try{
            if(document ==null){
                SAXReader read = new SAXReader();
                //调整spring配置文件的加载方式，由Request URL相对路径改为web应用所在服务器的绝对路径
                //BUG7549 panjunfang modify 20130428					
                String fileSeparator = System.getProperty("file.separator").equalsIgnoreCase("/")?"/":"\\";//文件分隔符 
                document = read.read(YssUtil.getAppConContextPath(YssCons.YSS_WebRealPath,"convertRule.xml"));
            }
        }catch(Exception ex){
            throw new YssException("读国内接口配置转换XML文件出错！\r\n"+
                                   ex.getMessage(),ex);
        }
    }

    /**
     * 根据URL 返回指定的值
     * @param ruleURL String
     * @return String
     * @throws YssException
     */
    private String ReadRuleResult(String ruleURL) throws YssException{
        Element element =null;
        List list = null;
        Iterator iter = null;
        try{
            loadFile();//读一次文件
            list =document.selectNodes(ruleURL);
            if(list !=null){
               iter= list.iterator();
               while(iter.hasNext()){
                   element =(Element)iter.next();
                   if(element!=null){
                       return element.getText();
                   }
               }
            }
        }catch(Exception ex){
            throw new YssException(ex.getMessage(),ex);
        }
        return "";
    }

    public String getSecSign() {
        return secSign;
    }

    public String getConvertedSecCode() {
        return convertedSecCode;
    }

    public String getBusinessSign() {
        return businessSign;
    }
}
