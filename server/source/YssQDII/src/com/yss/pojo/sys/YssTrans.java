package com.yss.pojo.sys;

/**
 * <p>Title: 用来传递参数的对象 </p>
 * <p>Description: 该对象用来在方法之间传递参数，这样可以统一参数传递的类型，还可以用来作为out方式的参数<br>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Ysstech</p>
 * @author 胡昆
 * @version 1.0
 */

public class YssTrans {

    public YssTrans() {
    }

    private boolean m_bool;
    private char m_chr;
    private byte m_byte;
    private short m_short;
    private int m_int;
    private long m_long;
    private float m_float;
    private double m_double;
    private String m_str;
    private Object m_obj;

    private boolean[] m_boolAry;
    private char[] m_chrAry;
    private byte[] m_byteAry;
    private short[] m_shortAry;
    private int[] m_intAry;
    private float[] m_floatAry;
    private double[] m_doubleAry;
    private String[] m_strAry;
    private Object m_objAry;

    public boolean[] getBoolAry() {
        return m_boolAry;
    }

    public byte getByte() {
        return m_byte;
    }

    public long getLong() {
        return m_long;
    }

    public byte[] getByteAry() {
        return m_byteAry;
    }

    public String[] getStrAry() {
        return m_strAry;
    }

    public Object getObj() {
        return m_obj;
    }

    public String getStr() {
        return m_str;
    }

    public char[] getChrAry() {
        return m_chrAry;
    }

    public float[] getFloatAry() {
        return m_floatAry;
    }

    public double[] getDoubleAry() {
        return m_doubleAry;
    }

    public int[] getIntAry() {
        return m_intAry;
    }

    public double getDouble() {
        return m_double;
    }

    public short[] getShortAry() {
        return m_shortAry;
    }

    public float getFloat() {
        return m_float;
    }

    public int getInt() {
        return m_int;
    }

    public short getShort() {
        return m_short;
    }

    public Object getObjAry() {
        return m_objAry;
    }

    public boolean getBool() {
        return m_bool;
    }

    public char getChr() {
        return m_chr;
    }

    public void setInt(int i) {
        this.m_int = i;
    }

    public void setBoolAry(boolean[] bAry) {
        this.m_boolAry = bAry;
    }

    public void setByte(byte byt) {
        this.m_byte = byt;
    }

    public void setLong(long l) {
        this.m_long = l;
    }

    public void setByteAry(byte[] bytAry) {
        this.m_byteAry = bytAry;
    }

    public void setStrAry(String[] strAry) {
        this.m_strAry = strAry;
    }

    public void setObj(Object obj) {
        this.m_obj = obj;
    }

    public void setStr(String str) {
        this.m_str = str;
    }

    public void setChrAry(char[] chrAry) {
        this.m_chrAry = chrAry;
    }

    public void setFloatAry(float[] fltAry) {
        this.m_floatAry = fltAry;
    }

    public void setDoubleAry(double[] dleAry) {
        this.m_doubleAry = dleAry;
    }

    public void setIntAry(int[] iAry) {
        this.m_intAry = iAry;
    }

    public void setDouble(double dle) {
        this.m_double = dle;
    }

    public void setShortAry(short[] shtAry) {
        this.m_shortAry = shtAry;
    }

    public void setBool(boolean bool) {
        this.m_bool = bool;
    }

    public void setFloat(float flt) {
        this.m_float = flt;
    }

    public void setObjAry(Object objAry) {
        this.m_objAry = objAry;
    }

    public void setShort(short sht) {
        this.m_short = sht;
    }

    public void setChr(char chr) {
        this.m_chr = chr;
    }
}
