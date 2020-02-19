package com.fengdis.component.rpc.elasticsearch.es;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @version 1.0
 * @Descrittion: es工具类旧版
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Component
public class ElasticSearchUtils {

	@Value("${elasticsearch.cluster-name}")
	private String cluster_name;// 实例名称

	@Value("${elasticsearch.cluster-ip}")
	private String cluster_ip;// elasticSearch服务器ip

	private String indexName = "";

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 返回一个到ElasticSearch的连接客户端
	 * transportClient将会在7.0版本上过时，并在8.0版本上移除掉，建议使用Java High Level REST Client
	 * @return
	 */
	public TransportClient getClient() {
		Settings settings = Settings.builder()
				.put("cluster.name", cluster_name)
				.put("client.transport.sniff", false)
				.build();// 设置集群名称和集群自动嗅探
		TransportClient client = new PreBuiltTransportClient(settings);// 创建client
		try {
			client.addTransportAddress(new TransportAddress(InetAddress.getByName(cluster_ip), 9300));// 增加地址和端口
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.error("ElasticSearch连接失败！");
		}

		return client;
	}

	/**
	 * 获取ElasticSearch连接（设置ElasticSearch集群服务器IP地址时，可传一个获多个ip地址逗号隔开，也可以只传主节点的IP地址，自动嗅探）
	 * @return
	 * @throws IOException
	 */
	public TransportClient getTransportClient() throws IOException {
		Settings settings = Settings.builder()
				.put("cluster.name", cluster_name)
				.put("client.transport.sniff",true)
				.build();
		TransportClient transportClient = new PreBuiltTransportClient(settings);
		String[] ips = cluster_ip.split(",");
		for (String ip : ips) {
			try {
				TransportAddress ist = new TransportAddress(InetAddress.getByName(ip),9300);
				transportClient.addTransportAddress(ist);
			}catch (Exception e){
			}
		}
		return transportClient;
	}

	/**
	 * 将Map转换成builder
	 * 
	 * @param mapParam
	 * @return
	 * @throws Exception
	 */
	private XContentBuilder createMapJson(Map<String, String> mapParam) throws Exception {
		XContentBuilder source = XContentFactory.jsonBuilder().startObject();

		for (Map.Entry<String, String> entry : mapParam.entrySet()) {
			source.field(entry.getKey(), entry.getValue());
		}
		source.endObject();
		return source;
	}

	/**
	 * 首字母转小写
	 *
	 * @param s 待转换的字符串
	 * @return
	 */
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	/**
	 * 将实体转换成XContentBuilder
	 * 
	 * @param entity
	 *            实体
	 * @param methodNameParam
	 *            实体中待转换成json的字段
	 * @return 返回json
	 * @throws Exception
	 */
	private XContentBuilder createEntityJson(Object entity, String... methodNameParam) throws Exception {
		// 创建json对象, 其中一个创建json的方式
		XContentBuilder source = XContentFactory.jsonBuilder().startObject();
		for (String methodName : methodNameParam) {
			if (!methodName.startsWith("get")) {
				throw new Exception("不是有效的属性！");
			}
			Method method = entity.getClass().getMethod(methodName, null);
			String fieldValue = (String) method.invoke(entity, null);
			String fieldName = this.toLowerCaseFirstOne(methodName.replace("get", ""));// 去掉“get”，并将首字母小写
			// 避免和elasticSearch中id字段重复
			if (fieldName == "_id") {
				fieldName = "id";
			}
			source.field(fieldName, fieldValue);
		}
		source.endObject();
		return source;
	}

	/**
	 * 将一个Map格式的数据（key,value）插入索引 （私有方法）
	 * 
	 * @param type
	 *            类型（对应数据库表）
	 * @param docId
	 *            id，对应elasticSearch中的_id字段
	 * @param mapParam
	 *            Map格式的数据
	 * @return
	 */
	public boolean addMapDocToIndex(String type, String docId, Map<String, String> mapParam) {
		boolean result = false;

		TransportClient client = getClient();
		XContentBuilder source = null;
		try {
			source = createMapJson(mapParam);
		} catch (Exception e) {
			return false;
		}

		// 存json入索引中
		IndexResponse response = null;
		if (docId == null) {
			// 使用默认的id
			response = client.prepareIndex(indexName, type).setSource(source).get();
		} else {
			response = client.prepareIndex(indexName, type, docId).setSource(source).get();
		}

		// 插入结果获取
		String index = response.getIndex();
		String gettype = response.getType();
		String id = response.getId();
		// long version = response.getVersion();
		RestStatus status = response.status();
		if (status.getStatus() == 201) {
			logger.info("新增文档成功：index:" + index + ",type: " + gettype + ",id: " + id);
			result = true;
		} else {
			logger.error("新增文档失败," + response.getResult().toString());
		}

		// 关闭client
		client.close();

		return result;
	}

	/**
	 * 将一个实体存入到默认索引的类型中（指定_id，一般是业务数据的id，及elasticSearch和关系型数据使用同一个id，方便同关系型数据库互动）
	 * （私有方法）
	 * 
	 * @param type
	 *            类型（对应数据库表）
	 * @param docId
	 *            id，对应elasticSearch中的_id字段
	 * @param entity
	 *            要插入的实体
	 * @param methodNameParm
	 *            需要将实体中哪些属性作为字段
	 * @return
	 */
	public boolean addEntityDoc(String type, String docId, Object entity, String... methodNameParm) {
		boolean result = false;

		TransportClient client = getClient();
		XContentBuilder source = null;
		try {
			source = createEntityJson(entity, methodNameParm);
		} catch (Exception e) {
			return false;
		}
		if (source == null) {
			return false;
		}
		// 存json入索引中
		IndexResponse response = null;
		if (docId == null) {
			// 使用默认的id
			response = client.prepareIndex(indexName, type).setSource(source).get();
		} else {
			response = client.prepareIndex(indexName, type, docId).setSource(source).get();
		}

		// 插入结果获取
		String index = response.getIndex();
		String gettype = response.getType();
		String id = response.getId();
		//long version = response.getVersion();
		RestStatus status = response.status();
		if (status.getStatus() == 201) {
			logger.info("新增文档成功 index：" + index + " type: " + gettype + " id: " + id);
			result = true;
		}else {
			logger.error(response.getResult().toString());
		}
		// 关闭client
		client.close();

		return result;
	}

	/**
	 * 删除文档
	 * 
	 * @param type
	 *            类型（对应数据库表）
	 * @param docId
	 *            类型中id
	 * @return
	 */
	public boolean deleteDoc(String type, String docId) {
		boolean result = false;

		TransportClient client = getClient();
		DeleteResponse deleteresponse = client.prepareDelete(indexName, type, docId).get();

		logger.info("删除结果：" + deleteresponse.getResult().toString());
		if (deleteresponse.getResult().toString() == "DELETED") {
			result = true;
		}
		// 关闭client
		client.close();

		return result;
	}

	/**
	 * 修改文档
	 * 
	 * @param type
	 *            类型
	 * @param docId
	 *            文档id
	 * @param updateParam
	 *            需要修改的字段和值
	 * @return
	 */
	public boolean updateDoc(String type, String docId, Map<String, String> updateParam) {
		String strResult = "";
		boolean result = false;

		TransportClient client = getClient();

		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(indexName);
		updateRequest.type(type);
		updateRequest.id(docId);
		try {
			updateRequest.doc(createMapJson(updateParam));
		} catch (Exception e) {
			return false;
		}
		try {
			strResult = client.update(updateRequest).get().getResult().toString();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		logger.info(strResult);

		if (strResult == "UPDATED") {
			result = true;
		}

		return result;
	}

	/**
	 * TODO or查询命中条数
	 * 
	 * @param type
	 *            类型
	 * @param shouldMap
	 *            查询条件
	 * @return
	 */
	public int multiOrSearchDocCount(String type, Map<String, String> shouldMap) {
		// TransportClient client = getClient();

		return 0;
	}

	/**
	 * 
	 * Date:2017年9月22日上午10:00:02
	 * 
	 * @author likaile
	 * @desc 根据 index type id获取对应的属性
	 */
	public Map<String, Object> getResponseDate(String type, String id) {
		TransportClient client = getClient();
		GetResponse response = client.prepareGet(indexName, type, id).execute().actionGet();
		Map<String, Object> map = response.getSource();
		logger.info("api searchDoc ：index:" + indexName + ",type: " + type + ",id: " + id+ map ==null?" 没有对应数据 !":" ");
		return map;
	}

	/**
	 * 高亮搜索
	 * 
	 * @param type
	 *            类型
	 * @param fieldName
	 *            段
	 * @param keyword
	 *            关键词
	 * @param from
	 *            开始行数
	 * @param size
	 *            每页大小
	 * @return
	 */
	public Map<String, Object> searchDocHighlight(String type, String fieldName, String keyword, int from, int size) {
		TransportClient client = getClient();

		// 高亮
		HighlightBuilder hiBuilder = new HighlightBuilder();
		hiBuilder.preTags("<span style=\"color:red\">");
		hiBuilder.postTags("</span>");
		hiBuilder.field(fieldName);

		QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery(fieldName, keyword);

		SearchRequestBuilder responsebuilder = client.prepareSearch(indexName).setTypes(type);
		responsebuilder.setQuery(queryBuilder);
		responsebuilder.highlighter(hiBuilder);
		responsebuilder.setFrom(from);
		responsebuilder.setSize(size);
		responsebuilder.setExplain(true);

		SearchResponse myresponse = responsebuilder.execute().actionGet();
		SearchHits searchHits = myresponse.getHits();

		// 总命中数
		long total = searchHits.getTotalHits();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", total);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < searchHits.getHits().length; i++) {
			Map<String, HighlightField> highlightFields = searchHits.getHits()[i].getHighlightFields();

			// 段高亮
			HighlightField titleField = highlightFields.get(fieldName);
			Map<String, Object> source = searchHits.getHits()[i].getSourceAsMap();
			if (titleField != null) {
				Text[] fragments = titleField.fragments();
				String name = "";
				for (Text text : fragments) {
					name += text;
				}
				source.put(fieldName, name);
			}

			list.add(source);
		}
		map.put("rows", list);

		return map;
	}

	/**
	 * or条件查询高亮
	 * 
	 * @param type
	 *            类型
	 * @param shouldMap
	 *            or条件和值
	 * @param from
	 *            开始行数
	 * @param size
	 *            每页大小
	 * @return
	 */
	public Map<String, Object> multiOrSearchDocHigh(String type, Map<String, String> shouldMap, int from, int size) {
		TransportClient client = getClient();

		SearchRequestBuilder responsebuilder = client.prepareSearch(indexName).setTypes(type);
		responsebuilder.setFrom(from);
		responsebuilder.setSize(size);
		responsebuilder.setExplain(true);

		// 高亮
		HighlightBuilder hiBuilder = new HighlightBuilder();
		hiBuilder.preTags("<span style=\"color:red\">");
		hiBuilder.postTags("</span>");

		// 高亮每个字段
		for (String key : shouldMap.keySet()) {
			hiBuilder.field(key);
		}

		responsebuilder.highlighter(hiBuilder);

		if (null != shouldMap && shouldMap.size() > 0) {
			// 创建一个查询
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 这里查询的条件用map传递
			for (String key : shouldMap.keySet()) {
				queryBuilder.should(QueryBuilders.matchPhraseQuery(key, shouldMap.get(key)));// or连接条件
			}
			// 查询
			responsebuilder.setQuery(queryBuilder);
		}

		SearchResponse myresponse = responsebuilder.execute().actionGet();
		SearchHits searchHits = myresponse.getHits();

		// 总命中数
		long total = searchHits.getTotalHits();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", total);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < searchHits.getHits().length; i++) {
			Map<String, HighlightField> highlightFields = searchHits.getHits()[i].getHighlightFields();
			Map<String, Object> source = searchHits.getHits()[i].getSourceAsMap();

			for (String key : shouldMap.keySet()) {
				// 各个段进行高亮
				HighlightField titleField = highlightFields.get(key);
				if (titleField != null) {
					Text[] fragments = titleField.fragments();
					String name = "";
					for (Text text : fragments) {
						name += text;
					}
					source.put(key, name);
				}
			}

			list.add(source);
		}
		map.put("rows", list);

		return map;
	}

	/**
	 * 搜索
	 * 
	 * @param type
	 *            类型
	 * @param fieldName
	 *            待搜索的字段
	 * @param keyword
	 *            待搜索的关键词
	 * @param from
	 *            开始行数
	 * @param size
	 *            每页大小
	 * @return
	 */
	public Map<String, Object> searchDoc(String type, String fieldName, String keyword, int from, int size) {
		List<String> hitResult = new ArrayList<String>();

		TransportClient client = getClient();
		QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery(fieldName, keyword);

		SearchRequestBuilder responsebuilder = client.prepareSearch(indexName).setTypes(type);
		responsebuilder.setQuery(queryBuilder);
		responsebuilder.setFrom(from);
		responsebuilder.setSize(size);
		responsebuilder.setExplain(true);

		SearchResponse myresponse = responsebuilder.execute().actionGet();
		SearchHits hits = myresponse.getHits();
		for (int i = 0; i < hits.getHits().length; i++) {
			hitResult.add(hits.getHits()[i].getSourceAsString());
		}

		// 将命中结果转换成Map输出
		Map<String, Object> modelMap = new HashMap<String, Object>(2);
		modelMap.put("total", hitResult.size());
		modelMap.put("rows", hitResult);

		return modelMap;
	}

	/**
	 * 多个条件进行or查询
	 * 
	 * @param type
	 *            类型
	 * @param shouldMap
	 *            进行or查询的段和值
	 * @param from
	 *            开始行数
	 * @param size
	 *            每页大小
	 * @return
	 */
	public Map<String, Object> multiOrSearchDoc(String type, Map<String, String> shouldMap, int from, int size) {
		List<String> hitResult = new ArrayList<String>();

		TransportClient client = getClient();

		SearchRequestBuilder responsebuilder = client.prepareSearch(indexName).setTypes(type);
		responsebuilder.setFrom(from);
		responsebuilder.setSize(size);
		responsebuilder.setExplain(true);

		if (null != shouldMap && shouldMap.size() > 0) {
			// 创建一个查询
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 这里查询的条件用map传递
			for (String key : shouldMap.keySet()) {
				queryBuilder.should(QueryBuilders.matchPhraseQuery(key, shouldMap.get(key)));// or连接条件
			}
			// 查询
			responsebuilder.setQuery(queryBuilder);
		}

		SearchResponse myresponse = responsebuilder.execute().actionGet();
		SearchHits hits = myresponse.getHits();
		for (int i = 0; i < hits.getHits().length; i++) {
			hitResult.add(hits.getHits()[i].getSourceAsString());
		}

		// 将命中结果转换成Map输出
		Map<String, Object> modelMap = new HashMap<String, Object>(2);
		modelMap.put("total", hitResult.size());
		modelMap.put("rows", hitResult);

		return modelMap;
	}

	/**
	 * 多个条件进行并和and 查询 
	 * 
	 * @param type
	 *            类型
	 * @param mustMap
	 *            进行and查询的段和值
	 * @param from
	 *            开始行数
	 * @param size
	 *            每页大小
	 * @param contain
	 *            true 包含所有的and条件 
	 *            false 不包含所有的and条件
	 * @return
	 */
	public Map<String, Object> multiAndSearchDoc(String type, Map<String, String> mustMap, boolean contain,int from, int size) {
		List<String> hitResult = new ArrayList<String>();

		TransportClient client = getClient();

		SearchRequestBuilder responsebuilder = client.prepareSearch(indexName).setTypes(type);
		responsebuilder.setFrom(from);
		responsebuilder.setSize(size);
		responsebuilder.setExplain(true);

		if (null != mustMap && mustMap.size() > 0) {
			// 创建一个查询
			BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

			// 这里查询的条件用map传递
			for (String key : mustMap.keySet()) {
				if(contain) {
					queryBuilder.must(QueryBuilders.matchPhraseQuery(key, mustMap.get(key)));// and查询
				}else {
					queryBuilder.mustNot(QueryBuilders.matchPhraseQuery(key, mustMap.get(key)));// 所有不包含and条件的结果查询
				}
			}
			// 查询
			responsebuilder.setQuery(queryBuilder);
		}

		SearchResponse myresponse = responsebuilder.execute().actionGet();
		SearchHits hits = myresponse.getHits();
		for (int i = 0; i < hits.getHits().length; i++) {
			hitResult.add(hits.getHits()[i].getSourceAsString());
		}

		// 将命中结果转换成Map输出
		Map<String, Object> modelMap = new HashMap<String, Object>(2);
		modelMap.put("total", hitResult.size());
		modelMap.put("rows", hitResult);

		return modelMap;
	}

}
