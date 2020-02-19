package com.fengdis.component.rpc.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @Descrittion: Http工具类 HttpClient实现
 * @author: fengdi
 * @since: 2018/8/8 0008 21:21
 */
public enum HttpClientUtils {
	INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
	private static final String CONTENT_TYPE_PLAIN = "application/plain;charset=UTF-8";
	private static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded;charset=UTF-8";
	private static final String CONTENT_TYPE_FORM = "multipart/form-data";

	private static final String ENCODING_UTF8 = "UTF-8";
	private static final String PROTOCOL = "http";

	public String GET(String ip,int port,String url, Map<String, String> parameters) {
		List<NameValuePair> nvps = createUriParameters(parameters);
		CloseableHttpClient httpClient = HttpClients.createDefault();

		URI uri;

		try {
			uri = new URIBuilder().setScheme(PROTOCOL).setHost(ip).setPort(port).setPath(url).setParameters(nvps).build();

			HttpGet httpGet = new HttpGet(uri);

			logger.info("Sending Http GET to url: " + url.toString());

			return parseResponse(httpClient.execute(httpGet).getEntity());

		} catch (Exception e) {
			logger.error("Http GET to url: " + url.toString() + "发生异常");
			return null;
		}
	}

	public String POST1(String url, Map<String,Object> params) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpPost httpPost = new HttpPost(url);

			StringEntity stringEntity = new StringEntity(JSON.toJSONString(params), Charset.forName(ENCODING_UTF8));

			httpPost.addHeader("Content-Type", CONTENT_TYPE_JSON);
			httpPost.setEntity(stringEntity);

			logger.info("Sending Http POST to url." + url);

			return parseResponse(httpClient.execute(httpPost).getEntity());

		} catch (Exception e) {
			logger.error("Http POST to url: " + url + "发生异常");
			return null;
		}
	}

	public String POST2(String url, Map<String,Object> params) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpPost httpPost = new HttpPost(url);

			StringEntity stringEntity = new StringEntity(JSON.toJSONString(params), Charset.forName(ENCODING_UTF8));

			httpPost.addHeader("Content-Type", CONTENT_TYPE_FORM);
			httpPost.setEntity(stringEntity);

			logger.info("Sending Http POST to url." + url);

			return parseResponse(httpClient.execute(httpPost).getEntity());

		} catch (Exception e) {
			logger.error("Http POST to url: " + url + "发生异常");
			return null;
		}
	}

	public String POST3(String url, Map<String,Object> params) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpPost httpPost = new HttpPost(url);

			StringEntity stringEntity = new StringEntity(JSON.toJSONString(params), Charset.forName(ENCODING_UTF8));


			httpPost.addHeader("Content-Type", CONTENT_TYPE_PLAIN);
			httpPost.setEntity(stringEntity);

			logger.info("Sending Http POST to url." + url);

			return parseResponse(httpClient.execute(httpPost).getEntity());

		} catch (Exception e) {
			logger.error("Http POST to url: " + url + "发生异常");
			return null;
		}
	}

	public String POST4(String url,  Map<String,Object> params) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			HttpPost httpPost = new HttpPost(url);

			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

			for (String key : params.keySet()) {
				nameValuePairList.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
			formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
			httpPost.setEntity(formEntity);

			logger.info("Sending Http POST to url." + url);

			return parseResponse(httpClient.execute(httpPost).getEntity());

		} catch (Exception e) {
			logger.error("Http POST to url: " + url + "发生异常");
			return null;
		}
	}

	private String parseResponse(HttpEntity responseEntity) throws IOException, UnsupportedEncodingException {
		String response = IOUtils.toString(new InputStreamReader(responseEntity.getContent(), ENCODING_UTF8));
		EntityUtils.consume(responseEntity);
		return response;
	}

	private List<NameValuePair> createUriParameters(Map<String, String> parameters) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String name : parameters.keySet()) {
			nvps.add(new BasicNameValuePair(name, parameters.get(name)));
		}
		return nvps;
	}

}
