package com.fengdis.util;

import org.dom4j.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @version 1.0
 * @Descrittion:
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class ResourceUtils {

	public static Resource[] getPathResource(String path) {
		ResourcePatternResolver RESOLVER = new PathMatchingResourcePatternResolver();
		Resource[] resources = null;
		try {
			resources = RESOLVER.getResources(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resources;
	}

	public static Properties getResourceProperties(String path) {
		Properties property = new Properties();
		Resource[] resources = getPathResource(path);
		if (resources != null) {
			for (Resource resource : resources) {
				InputStream is = null;
				try {
					is = resource.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (is != null) {
					try {
						property.load(is);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return property;
	}

	public static Properties putProperties(String path, Properties property) {
		Resource[] resources = getPathResource(path);
		if (resources != null) {
			for (Resource resource : resources) {
				InputStream is = null;
				try {
					is = resource.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (is != null) {
					try {
						property.load(is);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return property;
	}

	public static List<Document> getResourceXml(String path) {
		Resource[] resources = getPathResource(path);
		if (resources != null) {
			List<Document> list = new ArrayList<>();
			for (Resource resource : resources) {
				InputStream is = null;
				try {
					is = resource.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (is != null) {
					Document doc = null;
					try {
						doc = XmlUtils.getDocument(is);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (doc != null) {
						list.add(doc);
					}
				}
			}
			return list;
		} else {
			return null;
		}
	}

}
