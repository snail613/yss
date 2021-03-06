package com.yss.main.operdeal.platform.pfsystem.operfunextend;

import java.io.*;

public class FileClassLoader
    extends ClassLoader {
    private String drive = "";
    private String realName = "";
    private String fileType = ".class";
    public FileClassLoader(String drive, String realName) {
        super();
        this.drive = drive;
        this.realName = realName;
    }

    public Class findClass(String name) {
        byte[] data = loadClassData(name);
        return defineClass(realName, data, 0, data.length);
    }

    public byte[] loadClassData(String name) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        File file = null;
        byte[] data = null;
        try {
            if (name.indexOf(".") > -1) {
                name = name.replaceAll("[.]", "\\\\");
            }

            file = new File(drive + name + fileType);
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();
            int ch = 0;
            while ( (ch = fis.read()) != -1) {
                baos.write(ch);
            }
            baos.close();
            fis.close();
            data = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
