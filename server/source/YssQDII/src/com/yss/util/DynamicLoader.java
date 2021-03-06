/*package com.yss.util;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import com.sun.tools.javac.Main;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.net.URL;

public class DynamicLoader
    extends ClassLoader {
    private FileWriter fw = null;
    private BufferedWriter bw = null;
    private File fileDir = null;
    private File file = null;

    private String realName = "";
    private String fileType = ".class";
    private String path = "";
    private String tempPath = "";
    java.net.URL url = null;

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void dynamicCompile(String complieCode, String fileName) throws
        YssException {
        int status = 0;
        try {
            Main javac = new Main();
            this.path = Thread.currentThread().getContextClassLoader().getResource(
                "").getPath().substring(1);
            this.tempPath = path.substring(0, path.length() - 16);
            System.out.print(path);
            System.out.print(tempPath);
            fileDir = new File(tempPath.replaceAll("%20", " ") +
                               "\\tempFile");
            System.out.println(fileDir.getCanonicalFile() + "--------------------------------------------------------------");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
//         file=new File(fileDir.getAbsolutePath()+"\\Hel.java");
            file = new File(fileDir.getAbsolutePath() + "\\" + fileName + ".java");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(complieCode);
            bw.close();
            fw.close();
            String[] args = new String[] {
                "-d", path.replaceAll("%20", " "), "-classpath",
                path.replaceAll("%20", " "), file.getAbsolutePath()};
            status = javac.compile(args);
            if (status != 0) {
                throw new YssException("�������!");
            }
        } catch (Exception e) {
            throw new YssException(e.getMessage());
        }
    }

    public Class findClass(String name) {
        Class c = null;
        try {
            if (name.indexOf(".") > -1) {
                c = DynamicLoader.class.forName(name);
            } else {
                System.out.println(
                    "************************************************");
                System.out.println("before LoadClassData");
                System.out.println(
                    "*************************************************");

                byte[] data = loadClassData(name);
                c = defineClass(realName, data, 0, data.length);
                System.out.println(
                    "************************************************");
                if (c == null) {
                    System.out.println("nulllllllllllllllllllllllllllllllllllll");
                } else {
                    System.out.println(c.toString());
                }
                System.out.println(
                    "*************************************************");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public byte[] loadClassData(String name) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        File file = null;
        byte[] data = null;
        String fullPath = "";
        try {
            System.out.println(
                "*************************************************");
            System.out.println(path + name + fileType);
            System.out.println(
                "*************************************************");
            fullPath = path + name + fileType;
            fullPath = fullPath.replaceAll("%20", " ");
            file = new File(fullPath);
            fis = new FileInputStream(file);
            System.out.println(
                "*************************************************");
            System.out.println(path + name + fileType);
            System.out.println(file.getName());
            System.out.println(
                "*************************************************");
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while ( (ch = fis.read()) != -1) {
                baos.write(ch);
            }
            baos.close();
            fis.close();
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
*/