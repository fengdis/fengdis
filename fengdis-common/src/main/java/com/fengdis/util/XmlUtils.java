package com.fengdis.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @version 1.0
 * @Descrittion: xml文件读取解析工具类
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class XmlUtils {

	private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

	private static final String FILE_SUFFIX = ".xml";

	/**
	 * 读取磁盘文件
	 * @param filePath
	 * @return Document
	 */
	public static Document getDocument(String filePath) {

		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(filePath);
			logger.info(String.format("read file success path:%s",filePath));
		} catch (DocumentException e) {
			logger.error("read file error");
		}
		return doc;

	}

	public static Document getDocument(InputStream inputStream) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(inputStream);
			logger.info("read file success");
		} catch (DocumentException e) {
			logger.error("read file error");
		}
		return doc;
	}

	public static Document getDocument(File file) throws FileNotFoundException, DocumentException {
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(file);
			logger.info("read file success");
		} catch (DocumentException e) {
			logger.error("read file error");
		}
		return doc;
	}

	public static File[] getXmlFrom(URL url) throws URISyntaxException {
		File f = new File(url.toURI());
		return f.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String name) {
				return file.isDirectory() && name.endsWith(FILE_SUFFIX);
			}
		});
	}

}
