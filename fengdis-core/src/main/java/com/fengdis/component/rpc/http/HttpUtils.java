package com.fengdis.component.rpc.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * @version 1.0
 * @Descrittion: Http工具类 java.net.HttpURLConnection原生实现
 * @author: fengdi
 * @since: 2018/9/3 0003 22:51
 */
public class HttpUtils {
	
	//默认超时时间(单位为毫秒)
	private int connectTimeout = 10000;
	private int readTimeout = 30000;
	
	//是否开启cookie
	private boolean openCookie = false;
	
	//存储cookie的map, baidu.com={username=likaihao,password=111111}
	private Map<String,Map<String,String>> cookieMap = new HashMap<String,Map<String,String>>();
	
	
	public HttpUtils(){
		
	}
	
	public HttpUtils(boolean openCookie){
		this.openCookie = openCookie;
	}

	public HttpUtils(int connectTimeout, int readTimeout, boolean openCookie){
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.openCookie = openCookie;
	}
	
	
	
	/**
	 * 发送请求
	 * @param method 请求方式
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public void send(String method,String url,Map<String,String> paramMap,Map<String,String> headerMap,boolean isUrlEncoding,OutputStream destout){
		method = method.toUpperCase();
		try {
			//get请求组织参数
			if(method.equalsIgnoreCase("GET") || method.equalsIgnoreCase("DELETE")){
				String paramStr = getParamStr(paramMap, isUrlEncoding);
				if(paramStr!=null && paramStr.length()>0){
					if(url.contains("?")){
						url = url + "&" + paramStr;
					}else{
						url = url + "?" + paramStr;
					}
				}
			}
			
			//创建连接对象
			URL urlPath = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlPath.openConnection();
			
			//忽略https的证书
			//ignoreSSLCer(conn);
			
			//设置请求方式
			conn.setRequestMethod(method);
			
			//设置请求头
			if(headerMap!=null && headerMap.size()>0){
				for(String key : headerMap.keySet()){
					conn.setRequestProperty(key, headerMap.get(key));
				}
			}
			
			//设置超时时间(30秒)
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
			
			//取消自动重定向(否则获取不到重定向请求中的cookie)
			conn.setInstanceFollowRedirects(false);
			
			//设置cookie
			if(openCookie){
				String lastCookie = null;
				if(headerMap!=null){
					lastCookie = headerMap.get("Cookie");
				}
				setCookie(conn,lastCookie);
			}
			
			//post请求组织参数
			if(method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")){
				String paramStr = getParamStr(paramMap,isUrlEncoding);
				if(paramStr!=null && paramStr.length()>0){
					conn.setDoOutput(true);//开启输出流,发送参数
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
					out.write(paramStr);
					out.close();
				}
			}
			
			//保存cookie
			if(openCookie){
				saveCookie(conn);
			}
			
			//判断如果是重定向,则再发送一次get请求
			int status = conn.getResponseCode();
			if(status == 302){
				String location = conn.getHeaderField("Location");
				if(location.length()>0){
					send("GET", location, null, null, true, destout);
					return;
				}else{
					throw new RuntimeException("重定向没有发现location");
				}
			}
			
			//接收返回值
			InputStream in = null;
			//判断是否是500或404,如果是则读取error流,否则认为是正常,读取input流
			if(status/100!=5 && status/100!=4){
				in = conn.getInputStream();
				//是否是压缩的
				String enc = conn.getHeaderField("Content-Encoding");
				if(enc!=null && enc.equals("gzip")){
					in = new GZIPInputStream(in);
				}
			}else{
				in = conn.getErrorStream();
				if(in==null){
					in = conn.getInputStream();
				}
			}
			//读取流,并写入目标流
			byte[] b = new byte[1024*200];
			int len = -1;
			while( (len = in.read(b))!=-1){
				destout.write(b,0,len);
			}
			conn.disconnect();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 发送请求
	 * @param method 请求方式
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public byte[] sendReturnByteArr(String method,String url,Map<String,String> paramMap,Map<String,String> headerMap,boolean isUrlEncoding){
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		send(method,url,paramMap,headerMap,isUrlEncoding,byteOut);
		return byteOut.toByteArray();
	}
	
	/**
	 * 发送get请求
	 * @param url 请求路径
	 * @return
	 */
	public String sendGet(String url){
		return parseHttpByteToString(sendReturnByteArr("GET",url,null,null,false),"utf-8");
	}
	
	/**
	 * 发送get请求
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public String sendGet(String url, Map<String,String> paramMap,Map<String,String> headerMap){
		return parseHttpByteToString(sendReturnByteArr("GET",url,paramMap,headerMap,true),"utf-8");
	}
	
	/**
	 * 发送get请求,可指定是否进行url编码
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public String sendGet(String url, Map<String,String> paramMap,Map<String,String> headerMap,boolean isUrlEncoding){
		return parseHttpByteToString(sendReturnByteArr("GET",url,paramMap,headerMap,isUrlEncoding),"utf-8");
	}
	
	/**
	 * 发送get请求返回字节数组
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public byte[] sendGetReturnByteArray(String url, Map<String,String> paramMap,Map<String,String> headerMap){
		return sendReturnByteArr("GET",url,paramMap,headerMap,false);
	}
	
	/**
	 * 发送get请求并保存到文件
	 * @param url 请求路径
	 * @return
	 */
	public void sendGetSaveFile(String url,Map<String,String> paramMap,Map<String,String> headerMap,String path){
		File file = new File(path);
		if(file.exists()){
			throw new RuntimeException("文件已存在:"+path);
		}
		try {
			OutputStream out = new FileOutputStream(file);
			new HttpUtils().send("get",url,paramMap,headerMap,true,out);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 发送get请求并保存到文件,如果文件存在则直接替换
	 * @param url 请求路径
	 * @return
	 */
	public void sendGetSaveFileReplace(String url,Map<String,String> paramMap,Map<String,String> headerMap,String path){
		File file = new File(path);
		if(file.exists()){
			file.delete();
		}
		sendGetSaveFile(url,paramMap,headerMap,path);
	}
	
	/**
	 * 发送post请求获取字符串
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return
	 */
	public String sendPost(String url,Map<String,String> paramMap,Map<String,String> headerMap){
		return parseHttpByteToString(sendReturnByteArr("POST",url,paramMap,headerMap,false),"utf-8");
	}
	
	/**
	 * 发送post请求获取字符串
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return
	 */
	public String sendPost(String url,Map<String,String> paramMap,Map<String,String> headerMap, boolean isUrlEncoding){
		return parseHttpByteToString(sendReturnByteArr("POST",url,paramMap,headerMap,isUrlEncoding),"utf-8");
	}
	
	/**
	 * 发送post请求获取流
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return
	 */
	public byte[] sendPostReturnByteArray(String url,Map<String,String> paramMap,Map<String,String> headerMap){
		return sendReturnByteArr("POST",url,paramMap,headerMap,false);
	}
	
	/**
	 * 发送get请求,便捷方法
	 * @param url 请求路径
	 * @return
	 */
	public static String sendHttpGet(String url){
		return new HttpUtils().sendGet(url);
	}
	
	/**
	 * 发送get请求,便捷方法
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public static String sendHttpGet(String url, Map<String,String> paramMap,Map<String,String> headerMap){
		return new HttpUtils().sendGet(url,paramMap,headerMap);
	}
	
	/**
	 * 发送get请求,可指定是否进行url编码,便捷方法
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public static String sendHttpGet(String url, Map<String,String> paramMap,Map<String,String> headerMap,boolean isUrlEncoding){
		return new HttpUtils().sendGet(url,paramMap,headerMap,isUrlEncoding);
	}
	
	/**
	 * 发送get请求返回字节数组,便捷方法
	 * @param url 请求路径
	 * @param paramMap 参数
	 * @param headerMap 请求头
	 * @return
	 */
	public static byte[] sendHttpGetReturnByteArray(String url, Map<String,String> paramMap,Map<String,String> headerMap){
		return new HttpUtils().sendGetReturnByteArray(url,paramMap,headerMap);
	}
	
	/**
	 * 发送post请求获取字符串,便捷方法
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return
	 */
	public static String sendHttpPost(String url,Map<String,String> paramMap,Map<String,String> headerMap){
		return new HttpUtils().sendPost(url,paramMap,headerMap);
	}
	
	/**
	 * 发送post请求获取字符串,便捷方法
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return
	 */
	public static String sendHttpPost(String url,Map<String,String> paramMap,Map<String,String> headerMap, boolean isUrlEncoding){
		return new HttpUtils().sendPost(url,paramMap,headerMap,isUrlEncoding);
	}
	
	/**
	 * 发送post请求获取流,便捷方法
	 * @param url
	 * @param paramMap
	 * @param headerMap
	 * @return
	 */
	public static byte[] sendHttpPostReturnByteArray(String url,Map<String,String> paramMap,Map<String,String> headerMap){
		return new HttpUtils().sendPostReturnByteArray(url,paramMap,headerMap);
	}
	
	/**
	 * 获取参数字符串
	 * @param paramMap
	 * @param isUrlEncoding
	 * @return
	 */
	public static String getParamStr(Map<String,String> paramMap,boolean isUrlEncoding){
		try {
			StringBuilder builder = new StringBuilder();
			if(paramMap!=null && paramMap.size()>0){
				//如果只有一个参数,且key为空,则当做请求体发送过去,没有参数名称
				if(paramMap.size()==1){
					String key = paramMap.keySet().iterator().next();
					if(key==null || key.length()==0){
						String value = paramMap.get(key);
						if(value==null){
							return "";
						}
						if(isUrlEncoding){
							//value = URLEncoder.encode(value, "utf-8");
						}
						return value;
					}
				}
				
				for(String key : paramMap.keySet()){
					String value = paramMap.get(key);
					if(value==null){
						continue;
					}
					if(isUrlEncoding){
						value = URLEncoder.encode(value, "utf-8");
					}
					builder.append(key+"="+value+"&");
				}
				if(builder.length()>0){
					builder.deleteCharAt(builder.length()-1);
				}
			}
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 将字节数组解析为字符串
	 * @param bytes
	 * @return
	 */
	public static String parseHttpByteToString(byte[] bytes,String defaultEncoding){
		//先默认按utf-8转码
		try {
			String encoding = defaultEncoding;
			if(defaultEncoding == null || defaultEncoding.length()==0){
				encoding = "utf-8";
			}
			String str = new String(bytes,encoding);
			/*List<String> list = RegexUtils.getSubstrByRegexReturnList(str, " charset=(.*?)\"");
			if(list.size()>0 && list.get(0).length()<10){
				encoding = list.get(0);
			}*/
			if(encoding.length()>0 && !encoding.equalsIgnoreCase("utf-8")){
				str = new String(bytes,encoding);
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 清除cookie
	 * @author likaihao
	 * @date 2016年7月20日 上午10:02:34
	 */
	public void cleanCookie(){
		cookieMap.clear();
	}
	
	
	/**
	 * 保存每次响应中的cookie
	 * @param conn
	 */
	private void saveCookie(HttpURLConnection conn){
		//获取响应中的cookie
		Map<String, List<String>> headerMap = conn.getHeaderFields();
		List<String> cookieList = headerMap.get("Set-Cookie");
		if(cookieList==null || cookieList.size()==0){
			return;
		}
		
		//获取domain,一级域名cookie共享, 如:baidu.com
		String domain = getCookieDomain(conn);
		
		//添加cookie
		Map<String,String> map = null;
		if(cookieMap.containsKey(domain)){
			map = cookieMap.get(domain);
		}else{
			map = new HashMap<String,String>();
			cookieMap.put(domain, map);
		}
		for(String cookie : cookieList){
			String keyValue = cookie.split(";")[0];
			String[] arr = keyValue.split("=",2);
			map.put(arr[0], arr[1]);
		}
		//System.out.println("保存cookie:"+cookieList);
	}
	
	/**
	 * 为请求设置cookie
	 * @param conn
	 */
	private void setCookie(HttpURLConnection conn,String lastCookie){
		if(cookieMap.size()==0){
			return;
		}
		//获取domain, 如baidu.com
		String domain = getCookieDomain(conn);
		if(!cookieMap.containsKey(domain)){
			return;
		}
		//解析用户设置的cookie
		Map<String,String> lastCookieMap = new HashMap<String,String>();
		if(lastCookie!=null){
			String[] arr = lastCookie.split(";");
			for(String str : arr){
				String[] arr2 = str.split("=");
				lastCookieMap.put(arr2[0], arr2[1]);
			}
		}
		//添加自动记录的cookie
		Map<String,String> map = cookieMap.get(domain);
		StringBuilder builder = new StringBuilder();
		for(String cookieName : map.keySet()){
			String cookieValue = map.get(cookieName);
			if(lastCookieMap.containsKey(cookieName)){
				cookieValue = lastCookieMap.get(cookieName);
				lastCookieMap.remove(cookieName);
			}
			builder.append(cookieName);
			builder.append("=");
			builder.append(cookieValue);
			builder.append("; ");
		}
		
		//添加用户设置的cookie
		for(String cookieName : lastCookieMap.keySet()){
			String cookieValue = lastCookieMap.get(cookieName);
			builder.append(cookieName);
			builder.append("=");
			builder.append(cookieValue);
			builder.append("; ");
		}
		String cookie = builder.toString();
		
		//重新设置请求头
		conn.setRequestProperty("Cookie", cookie);
	}
	
	/**
	 * 获取cookie的domain,即生效路径
	 * @author likaihao
	 * @date 2016年7月20日 上午9:41:31
	 * @param conn
	 * @return
	 */
	private String getCookieDomain(HttpURLConnection conn){
		//获取domain,一级域名cookie共享, 如:baidu.com
		String domain = conn.getURL().getAuthority();
		String[] domainArr = domain.split("\\.");
		if(domainArr.length==3){
			domain =domainArr[1]+"."+domainArr[2];
		}
		return domain;
	}

}