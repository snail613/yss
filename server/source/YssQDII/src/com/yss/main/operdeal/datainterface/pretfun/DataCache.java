package com.yss.main.operdeal.datainterface.pretfun;

//Programmer: Dranson
import java.util.*;

/**
 * 数据缓存类：存储临时变量，不用每次都向数据库提交查询请求，从而提高程序的运行效率
 */
public class DataCache {
    private java.util.Properties obj = null;
    public DataCache() {
        init();
    }

    protected void init() {
        obj = new java.util.Properties();
    }

    /**
     * 得到属性值（默认值为""）
     * @param key String 属性名称
     * @return String
     */
    public String getString(String key) {
        return obj.getProperty(key) == null ? "" : obj.getProperty(key);
    }

    /**
     * 得到属性值（默认值为0）
     * @param key String 属性名称
     * @return int
     */
    public int getInt(String key) {
        return obj.getProperty(key) == null ? 0 : Integer.parseInt(obj.getProperty(key));
    }

    /**
     * 得到属性值（默认值为0）
     * @param key java.util.Date 日期型属性名称
     * @return int
     */
    public int getInt(Date key) {
        return obj.getProperty(prepare(key)) == null ? 0 : Integer.parseInt(obj.getProperty(prepare(key)));
    }

    /**
     * 得到属性值（默认值为dou）
     * 默认不为0情况是
     * a=getDouble(C+B+D)
     * if(a==0)
     *  a=getDouble(C+F+D)
     * if(a==0)
     *  a=getDouble(C)
     * 对于上诉情况如果a表示某个费率此时需要的正确结果就是a=0,因为当前套账不需要计算此项费用
     * 而getDouble(C)是公用费率,这样无论如何a都会取到不等于0的费率(如果公用费率存在且不等于0)
     * 所以应改为
     * a=getDouble(C+B+D,-1)
     * if(a==-1)
     *  a=getDouble(C+F+D,-1)
     * if(a==-1)
     *  a=getDouble(C)
     * 此处的-1表示没有设置个性化费率
     * 如果getDouble(C+B+D,-1)=0或a=getDouble(C+F+D,-1)都不会再去取getDouble(C),getDouble(C)为公用费率,通常是大于0的,
     * 最的a的值是0
     * 如果没有个性化费率则最后会取到getDouble(C),getDouble(C)返回的结果是>=0的一个费率
     * @param key String 属性名称
     * @return double
     */
    public double getDouble(String key, double dou) {
        return obj.getProperty(key) == null ? dou : Double.parseDouble(obj.getProperty(key));
    }

    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    /**
     * 得到属性值（默认值为0）
     * @param key java.util.Date 日期型属性名称
     * @return double
     */
    public double getDouble(Date key) {
        return obj.getProperty(prepare(key)) == null ? 0 : Double.parseDouble(obj.getProperty(prepare(key)));
    }

    /**
     * 得到属性值（默认值为false）
     * @param key String 属性名称
     * @return boolean
     */
    public boolean getBoolean(String key) {
        return obj.getProperty(key) == null ? false : obj.getProperty(key).equalsIgnoreCase("true");
    }

    /**
     * 得到属性值（默认值为false）
     * @param key java.util.Date 日期型属性名称
     * @return boolean
     */
    public boolean getBoolean(Date key) {
        return obj.getProperty(prepare(key)) == null ? false : obj.getProperty(prepare(key)).equalsIgnoreCase("true");
    }

    /**
     * 得到属性值（默认值为当前日期）
     * @param key String 属性名称
     * @return java.util.Date
     */
    public Date getDate(String key) throws Exception {
        return obj.getProperty(key) == null ? new Date() : reply(obj.getProperty(key));
    }

    /**
     * 设置属性值
     * @param key String       属性名称
     * @param value String     属性值
     */
    public void setString(String key, String value) {
        obj.setProperty(key, value);
    }

    /**
     * 设置属性值
     * @param key String       属性名称
     * @param value int     属性值
     */
    public void setInt(String key, int value) {
        obj.setProperty(key, Integer.toString(value));
    }

    /**
     * 设置属性值
     * @param key java.util.Date   日期型属性名称
     * @param value int         属性值
     */
    public void setInt(Date key, int value) {
        obj.setProperty(prepare(key), Integer.toString(value));
    }

    /**
     * 设置属性值
     * @param key String       属性名称
     * @param value double     属性值
     */
    public void setDouble(String key, double value) {
        obj.setProperty(key, Double.toString(value));
    }

    /**
     * 设置属性值
     * @param key java.util.Date   日期型属性名称
     * @param value double         属性值
     */
    public void setDouble(Date key, double value) {
        obj.setProperty(prepare(key), Double.toString(value));
    }

    /**
     * 设置属性值
     * @param key String        属性名称
     * @param value boolean     属性值
     */
    public void setBoolean(String key, boolean value) {
        obj.setProperty(key, Boolean.toString(value));
    }

    /**
     * 设置属性值
     * @param key java.util.Date   日期型属性名称
     * @param value boolean         属性值
     */
    public void setBoolean(Date key, boolean value) {
        obj.setProperty(prepare(key), Boolean.toString(value));
    }

    /**
     * 设置属性值
     * @param key String               属性名称
     * @param value java.util.Date     属性值
     */
    public void setDate(String key, Date value) {
        obj.setProperty(key, prepare(value));
    }

    private final Date reply(String value) throws Exception {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(value);
    }

    private final String prepare(Date value) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(value);
    }

    /**
     * 清空缓存数据
     */
    public void clear() {
        obj.clear();
    }
}
