package com.yss.util;

import java.sql.*;

/**
 * <p>Title: 基本类型处理类 </p>
 * <p>Description: 该类型可以随意提取各种基本类型<br>
 * <b>可方便地用作通过参数返回值</b>，支持所有基本类型和String、Date<br>
 * 访问的方法名称为get/set＋首字母大写的类型名称</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Ysstech </p>
 * @author alex
 * @version 1.0
 */
public class YssType {
    public YssType() {
    }

    public YssType(boolean p) {
        setBoolean(p);
    }

    public YssType(char p) {
        setChar(p);
    }

    public YssType(byte p) {
        setByte(p);
    }

    public YssType(short p) {
        setShort(p);
    }

    public YssType(int p) {
        setInt(p);
    }

    public YssType(long p) {
        setLong(p);
    }

    public YssType(float p) {
        setFloat(p);
    }

    public YssType(double p) {
        setDouble(p);
    }

    public YssType(String p) {
        setString(p);
    }

    public YssType(java.util.Date p) {
        setDate(p);
    }

    private String value = null;
    private int type; //当前数据类型

    public int getType() {
        return type;
    }

    //boolean
    public void setBoolean(boolean b) {
        value = String.valueOf(b);
        type = Types.BOOLEAN;
    }

    public boolean getBoolean() {
        if (type == Types.BOOLEAN) {
            return Boolean.valueOf(value).booleanValue();
        } else {
            return false;
        }
    }

    //char
    public void setChar(char c) {
        value = String.valueOf(c);
        type = Types.CHAR;
    }

    public char getChar() {
        if (type == Types.CHAR && value.length() > 0) {
            return value.charAt(0);
        } else {
            return 0;
        }
    }

    //byte
    public void setByte(byte b) {
        value = String.valueOf(b);
        type = Types.TINYINT;
    }

    public byte getByte() {
        if (type == Types.TINYINT) {
            return Byte.parseByte(value);
        } else {
            return 0;
        }
    }

    //short
    public void setShort(short sh) {
        value = String.valueOf(sh);
        type = Types.SMALLINT;
    }

    public short getShort() {
        if (type == Types.SMALLINT || type == Types.CHAR || type == Types.TINYINT) {
            return Short.parseShort(value);
        } else if (type == Types.INTEGER) {
            int i = Integer.parseInt(value);
            return (i <= Short.MAX_VALUE && i >= Short.MIN_VALUE ? (short) i : 0);
        } else {
            return 0;
        }
    }

    //int
    public void setInt(int i) {
        value = String.valueOf(i);
        type = Types.INTEGER;
    }

    public int getInt() {
        if (type == Types.INTEGER || type == Types.SMALLINT || type == Types.CHAR ||
            type == Types.TINYINT) {
            return Integer.parseInt(value);
        } else {
            return 0;
        }
    }

    //long
    public void setLong(long l) {
        value = String.valueOf(l);
        type = Types.BIGINT;
    }

    public long getLong() {
        if (type == Types.BIGINT || type == Types.INTEGER ||
            type == Types.SMALLINT || type == Types.CHAR || type == Types.TINYINT) {
            return Long.parseLong(value);
        } else {
            return 0;
        }
    }

    //float
    public void setFloat(float f) {
        value = String.valueOf(f);
        type = Types.FLOAT;
    }

    public float getFloat() {
        if (type == Types.FLOAT || type == Types.SMALLINT || type == Types.CHAR ||
            type == Types.TINYINT) {
            return Float.parseFloat(value);
        } else {
            return 0;
        }
    }

    //double
    public void setDouble(double db) {
        value = String.valueOf(db);
        type = Types.DOUBLE;
    }

    public double getDouble() {
        if (type == Types.DOUBLE || type == Types.INTEGER ||
            type == Types.SMALLINT || type == Types.CHAR || type == Types.TINYINT) {
            return Double.parseDouble(value);
        } else {
            return 0;
        }
    }

    //string
    public void setString(String s) {
        value = s;
        type = 0; //0本来是sql.types中的null，借用
    }

    public String getString() { //日期和字符，转换的时候还其本来面目
        if (type == Types.DATE) {
            return getDate().toString();
        } else if (type == Types.CHAR) {
            return String.valueOf(getChar());
        } else {
            return value;
        }
    }

    //date
    public void setDate(java.util.Date dt) {
        value = String.valueOf(dt.getTime());
        type = Types.DATE;
    }

    public java.util.Date getDate() {
        if (type == Types.DATE) {
            return new java.util.Date(Long.parseLong(value));
        } else {
            return null;
        }
    }
}
