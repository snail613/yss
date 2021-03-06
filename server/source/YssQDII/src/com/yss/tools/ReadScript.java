package com.yss.tools;

import java.io.*;
import com.yss.util.YssException;
import com.yss.dsub.BaseBean;
import java.net.URLDecoder;

/**
 * <p>Title: ReadScript 类</p>
 *
 * <p>Description:处理外部脚本文件 </p>
 *
 * author: wangzuochun
 * date: 20090415
 * BugNo: MS00376 QDV4交银施罗德2009年4月13日01_AB
 */
public class ReadScript
    extends BaseBean {
    public ReadScript() {
    }

    private String fileName = "createtable"; //建表脚本文件
    private InputStreamReader isr = null; //用来读取文件的流
    private BufferedReader bufReader = null; //缓冲器，用户提高读取效率
    private String sPath = Thread.currentThread(). //获取class文件所在路径
        getContextClassLoader().getResource("").getPath();

    /**
     * 读取sql脚本文件，并执行文件sql语句
     * @throws YssException
     */
    public void readSql() throws YssException {
        StringBuffer bufSql = null; //用于存储解析的SQl语句

        //获取创建表的脚本文件详细路径
        sPath = sPath.substring(1, sPath.length() - 16) + "dbset//" +
            fileName;
        try {
            //对路径进行转码，防止出现乱码
            sPath = URLDecoder.decode(sPath, "utf-8");
            //读取文件，为了避免出现乱码，使用流来进行对文件处理
            isr = new InputStreamReader(new FileInputStream(sPath), "UTF-8");
            bufReader = new BufferedReader(isr);
            bufSql = new StringBuffer();

            //文件中读入一整行字符,并返回结果字符串
            String line = bufReader.readLine();
            while (line != null) {
                //将读取的每句sql语句拼装到StringBuffer中
                bufSql.append(line);
                //判断line是否以“；”结尾
                if (line.endsWith(";")) {
                    //删除sql脚本最后的";"
                    bufSql.delete(bufSql.length() - 1, bufSql.length());
                    //执行SQL语句
                    dbl.executeSql(bufSql.toString());
                    //清空bufSql
                    bufSql.delete(0, bufSql.length());
                }
                //读取下一行
                line = bufReader.readLine();
            }
        } catch (Exception e) {
            throw new YssException("读取SQL脚本出错！", e);
        } finally {
            this.closeReaderFinally(bufReader, isr);
        }

    }

    /**
     * 关闭阅读器的方法
     * 注意事项: 关闭时根据参数的顺序进行关闭
     * author : sunkey
     * date   : 20090419
     * bugNO  : MS00376 QDV4交银施罗德2009年4月13日01_AB
     * @param reader1 Reader 阅读器1 所有继承java.io.Reader类的对象
     * @param reader2 Reader 阅读器2 所有继承java.io.Reader类的对象
     * @throws YssException
     */
    public void closeReaderFinally(Reader reader1, Reader reader2) throws
        YssException {
        try {
            //关闭时，现对阅读器的状态进行判断，如果不为null，才进行关闭
            if (reader1 != null) {
                reader1.close();
            }
            if (reader2 != null) {
                reader2.close();
            }
        } catch (IOException ex) {
            throw new YssException("关闭读取器时出现异常！", ex);
        }
    }
}
