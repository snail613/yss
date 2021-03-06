package com.yss.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import com.yss.util.YssException;

public class CreateXML {
	public void generateDocument(String path, String fileName) throws YssException {
		try {
			// 使用DocumentHelper类创建一个文档实例。Document是生成XML文档节点的dom4jAPI工厂类。
			Document document = DocumentHelper.createDocument();
			// 使用addElement()方法创建根元素document.addElement()用于向XML文档中增加元素。
			Element rootElement = document.addElement("AutoUpdater");
			// 向AutoUpdater元素中使用addElement()方法增加Files元素。
			Element filesElement = rootElement.addElement("Files");
			File[] files = new File(path).listFiles();
			getFile(files, filesElement,path);
			XMLWriter output = new XMLWriter(new FileWriter(new File(path + fileName)));
			output.write(document);
			output.close();
		} catch (IOException e) {
			throw new YssException("创建更新配置文件出错！", e);
		}
	}
	
	public void getFile(File[] files,Element filesElement,String path){
		for(int i = 0; i < files.length; i++){
			if(files[i].isDirectory()){
				File[] filess = files[i].listFiles();
				getFile(filess, filesElement, path);
			}else{
				// 向Files元素中添加File元素。
				Element fileElement = filesElement.addElement("File");
				// 使用addAttribute()方法向File元素添加Name属性。
				fileElement.addAttribute("Name", files[i].getName());
				String folder = files[i].getParent().substring(path.length());
				// 使用addAttribute()方法向File元素添加Folder属性。
				fileElement.addAttribute("Folder", folder);
			}
		}
	}
}
