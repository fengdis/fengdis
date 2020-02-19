package com.fengdis.component.rpc.elasticsearch;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.cluster.node.tasks.get.GetTaskResponse;
import org.elasticsearch.action.admin.cluster.node.tasks.list.ListTasksResponse;
import org.elasticsearch.action.bulk.*;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldcaps.FieldCapabilities;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.ActiveShardCount;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.*;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.rankeval.*;
import org.elasticsearch.index.reindex.*;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.*;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.filter.*;
import org.elasticsearch.search.aggregations.bucket.geogrid.GeoHashGrid;
import org.elasticsearch.search.aggregations.bucket.global.Global;
import org.elasticsearch.search.aggregations.bucket.global.GlobalAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.missing.Missing;
import org.elasticsearch.search.aggregations.bucket.missing.MissingAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNested;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBounds;
import org.elasticsearch.search.aggregations.metrics.geobounds.GeoBoundsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.elasticsearch.search.aggregations.metrics.min.MinAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentile;
import org.elasticsearch.search.aggregations.metrics.percentiles.Percentiles;
import org.elasticsearch.search.aggregations.metrics.percentiles.PercentilesAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetric;
import org.elasticsearch.search.aggregations.metrics.scripted.ScriptedMetricAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.Stats;
import org.elasticsearch.search.aggregations.metrics.stats.StatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.stats.extended.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.tophits.TopHits;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCountAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.profile.ProfileShardResult;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.elasticsearch.tasks.TaskId;
import org.elasticsearch.tasks.TaskInfo;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.exponentialDecayFunction;
import static org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders.randomFunction;

/**
 * @version 1.0
 * @Descrittion: es工具类 transportClient将会在7.0版本上过时，并在8.0版本上移除掉，建议使用Java High Level REST Client
 * @author: fengdi
 * @since: 2019/08/28 17:26
 */
@Component
@ConditionalOnBean(ESClientConfig.class)
public class ESClientUtils {

    @Autowired
    private TransportClient transportClient;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private RestClient restClient;

   /* private static volatile RestClientUtils restClientUtils = null;

    *//**
     * 这里使用饿汉单例模式创建
     *//*
    public static RestClientUtils getInstance() {
        if (restClientUtils == null){
            synchronized (RestClientUtils.class){
                if (restClientUtils == null){
                    restClientUtils = new RestClientUtils();
                }
            }
        }
        return restClientUtils;
    }*/


    public void closeClient(){
        try {
            if (client != null){
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*------------------------------------------- Single Document start -----------------------------------------------*/

    /**
     * 增，插入记录
     * @throws Exception
     */
    public void index(String json) throws Exception{
        IndexResponse response = transportClient.prepareIndex("twitter", "_doc")
                .setSource(json, XContentType.JSON)
                .get();

        //Index name
        String _index = response.getIndex();
        //Type name
        String _type = response.getType();
        //Document ID (generated or not)
        String _id = response.getId();
        //Version
        long _version = response.getVersion();
        //status has stored current instance statement
        RestStatus status = response.status();

        System.out.println("index: " + _index);
        System.out.println("type: " + _type);
        System.out.println("id: " + _id);
        System.out.println("version: " + _version);
        System.out.println("status: " + status);
    }

    /**
     * 根据Id查询
     * @param id
     * @throws Exception
     */
    public void get(String id) throws Exception{
        GetResponse response = transportClient.prepareGet("twitter", "_doc", id).get();
        Map<String, Object> map = response.getSource();
        for (String key: map.keySet()){
            System.out.println(key + ": " + map.get(key).toString());
        }
    }

    /**
     * 根据Id删除
     * @param id
     * @throws Exception
     */
    public void delete(String id) throws Exception{
        DeleteResponse response = transportClient.prepareDelete("twitter", "_doc", id).get();
    }

    /**
     * 根据查询条件删除
     * @throws Exception
     */
    public void deleteByQueryAPI() throws Exception{
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
                .filter(QueryBuilders.matchQuery("user", "kimchy"))
                .source("twitter")
                .get();

        long deleted = response.getDeleted();
        System.out.println(deleted);
    }

    /**
     * 根据查询条件删除并返回结果数据 不起作用
     * @throws Exception
     */
    public void deleteByQueryAPIWithListener() throws Exception{
        DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
                .filter(QueryBuilders.matchQuery("user", "kimchy"))
                .source("twitter")
                .execute(new ActionListener<BulkByScrollResponse>() {
                    @Override
                    public void onResponse(BulkByScrollResponse response) {
                        long deleted = response.getDeleted();
                        if (deleted == 1){
                            System.out.println("删除成功");
                        }
                        System.out.println("删除中...");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        //Handle the exception
                        System.out.println("删除失败");
                    }
                });
        System.out.println("hahahahahah");
    }

    /**
     * 更新update
     * @throws Exception
     */
    public void update1() throws Exception{
        //Method One
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("twitter");
        updateRequest.type("_doc");
        updateRequest.id("NpEWCGcBi36MQkKOSdf3");
        updateRequest.doc(jsonBuilder()
                .startObject()
                .field("user", "tom")
                .endObject()
        );

        transportClient.update(updateRequest).get();

        //Method Two
        UpdateRequest updateRequest1 = new UpdateRequest("twitter", "_doc", "NpEWCGcBi36MQkKOSdf3")
                .doc(jsonBuilder()
                        .startObject()
                        .field("user", "tom")
                        .endObject()
                );

        transportClient.update(updateRequest1).get();

        //Method Three
        UpdateRequest updateRequest2 = new UpdateRequest("twitter", "_doc", "NpEWCGcBi36MQkKOSdf3")
                .script(new Script("ctx._source.user = \"tom1\""));
        transportClient.update(updateRequest2).get();
    }

    /**
     * 更新prepareUpdate
     * @throws Exception
     */
    public void prepareUpdate() throws Exception{
        transportClient.prepareUpdate("twitter", "_doc", "NpEWCGcBi36MQkKOSdf3")
                .setDoc(jsonBuilder()
                        .startObject()
                        .field("user", "tom")
                        .endObject())
                .get();
    }

    /**
     * 如果不存在则创建indexRequest，存在则更新updateRequest
     * @throws Exception
     */
    public void upsert() throws Exception{
        IndexRequest indexRequest = new IndexRequest("index", "type", "1")
                .source(jsonBuilder()
                        .startObject()
                        .field("name", "Joe Smith")
                        .field("gender", "male")
                        .endObject());

        UpdateRequest updateRequest = new UpdateRequest("index", "type", "1")
                .doc(jsonBuilder()
                        .startObject()
                        .field("gender", "male")
                        .endObject())
                .upsert(indexRequest);
        transportClient.update(updateRequest);
    }

    /*------------------------------------------- Single Document end -----------------------------------------------*/

    /*------------------------------------------- Multi Document start -----------------------------------------------*/

    /**
     * 指定单个Id获取，指定多个Id获取，从另外一个库表获取数据
     * @throws Exception
     */
    public void multiGet() throws Exception{
        MultiGetResponse multiGetItemResponses = transportClient.prepareMultiGet()
                .add("twitter", "_doc", "1")
                .add("twitter", "_doc", "2", "3", "4")
                .add("another", "_doc", "foo")
                .get();

        for (MultiGetItemResponse itemResponse: multiGetItemResponses){
            GetResponse response = itemResponse.getResponse();
            if (response.isExists()){
                String json = response.getSourceAsString();
                System.out.println(json);
            }
        }
    }

    /**
     * 批量处理
     * @throws Exception
     */
    public void bulkAPI() throws Exception{
        BulkRequestBuilder bulkRequest = transportClient.prepareBulk();

        //either use transportClient#prepare, or use Requests# to directly build index/delete requests
        bulkRequest.add(transportClient.prepareIndex("twitter", "_doc", "1")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                        .endObject()
                )
        );

        bulkRequest.add(transportClient.prepareIndex("twitter", "_doc", "2")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "another post")
                )
        );

        BulkResponse bulkResponses = bulkRequest.get();

        if (bulkResponses.hasFailures()){
            // process failures by iterating through each bulk response item
            System.out.println("报错");
        }
    }

    /**
     * 创建自定义的批处理器
     * @throws Exception
     */
    public void createBulkProcessor() throws Exception{
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                transportClient,
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long l, BulkRequest bulkRequest) {
                        System.out.println("beforeBulk...");
                    }

                    @Override
                    public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                        System.out.println("成功，afterBulk...");
                    }

                    @Override
                    public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                        System.out.println("失败，afterBulk...");
                    }
                })
                .setBulkActions(10000)
                .setBulkSize(new ByteSizeValue(5, ByteSizeUnit.MB))
                .setFlushInterval(TimeValue.timeValueSeconds(5))
                .setConcurrentRequests(1)
                .setBackoffPolicy(
                        BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3)
                ).build();

        //添加批量处理操作
        bulkProcessor.add(new IndexRequest("twitter", "_doc", "1")
                .source(jsonBuilder()
                        .startObject()
                        .field("user", "kimchy")
                        .field("postDate", new Date())
                        .field("message", "trying out Elasticsearch")
                        .endObject()));

        bulkProcessor.add(new DeleteRequest("twitter", "_doc", "2"));

        //flush any remaining requests
        bulkProcessor.flush();

        //Or close the bulkProcess if you don't need it anymore
        bulkProcessor.awaitClose(10, TimeUnit.MINUTES);
        //or
        //bulkProcessor.close();

        //Refresh your indices
        transportClient.admin().indices().prepareRefresh().get();

        //Now you can start searching
        transportClient.prepareSearch().get();
    }

    /**
     * 根据查询条件更新
     * @throws Exception
     */
    public void updateByQueryAPI() throws Exception{
        //Method One
        UpdateByQueryRequestBuilder updateByQuery = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient);
        updateByQuery.source("source_index").abortOnVersionConflict(false);
        BulkByScrollResponse response = updateByQuery.get();

        //Method Two
        UpdateByQueryRequestBuilder updateByQuery1 = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient);
        updateByQuery1.source("source_index")
                .filter(termQuery("level", "awesome"))
                .size(1000)
                .script(new Script(ScriptType.INLINE, "ctx._source.awesome = 'absolutely'", "painless", Collections.emptyMap()));
        BulkByScrollResponse response1 = updateByQuery1.get();

        //Method Three
        UpdateByQueryRequestBuilder updateByQuery2 = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient);
        updateByQuery2.source("source_index").size(100)
                .source().addSort("cat", SortOrder.DESC);
        BulkByScrollResponse response2 = updateByQuery2.get();

        //Method Four
        UpdateByQueryRequestBuilder updateByQuery3 = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient);
        updateByQuery3.source("source_index")
                .script(new Script(
                        ScriptType.INLINE,
                        "if (ctx._source.awesome == 'absolutely') {" +
                                " ctx.op = 'noop'" +
                                "} else if (ctx._source.awesome == 'lame') {" +
                                " ctx.op = 'delete'" +
                                "} else {" +
                                "ctx._source.awesome = 'absolutely'" +
                                "}",
                        "painless",
                        Collections.emptyMap()
                ));
        BulkByScrollResponse response3 = updateByQuery3.get();

        //Method Five
        UpdateByQueryRequestBuilder updateByQuery4 = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient);
        updateByQuery4.source("foo", "bar").source().setTypes("a", "b");
        BulkByScrollResponse response4 = updateByQuery4.get();

        //Method Six
        UpdateByQueryRequestBuilder updateByQuery5 = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient);
        updateByQuery5.source().setRouting("cat");
        BulkByScrollResponse response5 = updateByQuery5.get();

        //Method Seven
        UpdateByQueryRequestBuilder updateByQuery6 = UpdateByQueryAction.INSTANCE.newRequestBuilder(transportClient);
        updateByQuery6.setPipeline("hurray");
        BulkByScrollResponse response6 = updateByQuery6.get();

        //Method Eight works with the task api
        ListTasksResponse tasksList = transportClient.admin().cluster().prepareListTasks()
                .setActions(UpdateByQueryAction.NAME).setDetailed(true).get();
        for (TaskInfo info: tasksList.getTasks()){
            TaskId taskId = info.getTaskId();
            BulkByScrollTask.Status status = (BulkByScrollTask.Status) info.getStatus();
            //do stuff

            GetTaskResponse get = transportClient.admin().cluster().prepareGetTask(taskId).get();

            //works with the cancel task api
            // Cancel all update-by-query requests
            transportClient.admin().cluster().prepareCancelTasks().setActions(UpdateByQueryAction.NAME).get().getTasks();
            // Cancel a specific update-by-query request
            transportClient.admin().cluster().prepareCancelTasks().setTaskId(taskId).get().getTasks();

            //Rethrottling
            RethrottleAction.INSTANCE.newRequestBuilder(transportClient)
                    .setTaskId(taskId)
                    .setRequestsPerSecond(2.0f)
                    .get();
        }
    }

    public void Reindex() throws Exception{
        BulkByScrollResponse response = ReindexAction.INSTANCE.newRequestBuilder(transportClient)
                .destination("target_index")
                .filter(QueryBuilders.matchQuery("category", "xzy"))
                .get();
    }
    /*------------------------------------------- Multi Document start -----------------------------------------------*/

    /*------------------------------------------- search Api start -----------------------------------------------*/

    public void searchAPI() throws Exception{
        SearchResponse response = transportClient.prepareSearch("index1", "index2")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(termQuery("multi", "test"))
                .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))
                .setFrom(0).setSize(60).setExplain(true)
                .get();

        SearchResponse response1 = transportClient.prepareSearch().get();
    }

    /**
     * 卷轴
     * @throws Exception
     */
    public void scrolls() throws Exception{
        QueryBuilder qb = termQuery("multi", "test");

        SearchResponse scrollResp = transportClient.prepareSearch("test")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(qb)
                .setSize(100)
                .get();

        //Scroll until no hits are returned
        do {
            for (SearchHit hit: scrollResp.getHits().getHits()){
                //Handle the hit...
            }

            scrollResp = transportClient.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        }while (scrollResp.getHits().getHits().length != 0);
        //Zero hits mark the end of the scroll and the while loop.
    }

    /**
     * 多查询
     * @throws Exception
     */
    public void multiSearch() throws Exception{
        SearchRequestBuilder srb1 = transportClient.prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
        SearchRequestBuilder srb2 = transportClient.prepareSearch().setQuery(QueryBuilders.matchQuery("name", "kimchy")).setSize(1);

        MultiSearchResponse sr = transportClient.prepareMultiSearch()
                .add(srb1)
                .add(srb2)
                .get();

        //You will get all individual responses from MultiSearchResponse#getResponses()
        long nbHits = 0;
        for (MultiSearchResponse.Item item: sr.getResponses()){
            SearchResponse response = item.getResponse();
            nbHits += response.getHits().getTotalHits();
        }
    }

    /*------------------------------------------- search Api end -----------------------------------------------*/

    /*------------------------------------------- Aggregations start -----------------------------------------------*/

    public void aggregations() throws Exception{
        SearchResponse sr = transportClient.prepareSearch()
                .setQuery(QueryBuilders.matchAllQuery())
                .addAggregation(
                        AggregationBuilders.terms("agg1").field("field")
                )
                .addAggregation(
                        AggregationBuilders.dateHistogram("agg2")
                                .field("birth")
                                .dateHistogramInterval(DateHistogramInterval.YEAR)
                )
                .get();

        //Get your facet results
        Terms agg1 = sr.getAggregations().get("agg1");
        Histogram agg2 = sr.getAggregations().get("agg2");
    }

    public void terminateAfter() throws Exception{
        SearchResponse sr = transportClient.prepareSearch("")
                .setTerminateAfter(1000)
                .get();

        if (sr.isTerminatedEarly()){
            // We finished early
        }
    }

    /**
     * 查询模版
     * @throws Exception
     */
    public void searchTemplate() throws Exception{
        Map<String, Object> template_params = new HashMap<>();
        template_params.put("param_gender", "male");

        //create your search template request
        SearchResponse sr = new SearchTemplateRequestBuilder(transportClient)
                .setScript("template_gender")
                .setScriptType(ScriptType.STORED)
                .setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();

        SearchResponse sr1 = new SearchTemplateRequestBuilder(transportClient)
                .setScript("template_gender")
                .setScriptType(ScriptType.STORED)
                .setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();

        SearchResponse sr2 = new SearchTemplateRequestBuilder(transportClient)
                .setScript("{\n" +
                        "       \"query\" : {\n " +
                        "             \"match\" : {\n " +
                        "                   \"gender\" : \"{{param_gender}}\"\n" +
                        "               }\n" +
                        "         }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();
    }

    /**
     * 构造聚合
     * @throws Exception
     */
    public void structuringAggregations() throws Exception{
        SearchResponse sr = transportClient.prepareSearch()
                .addAggregation(
                        AggregationBuilders.terms("by_country").field("country")
                                .subAggregation(AggregationBuilders.dateHistogram("by_year")
                                        .field("dateOfBirth")
                                        .dateHistogramInterval(DateHistogramInterval.YEAR)
                                        .subAggregation(AggregationBuilders.avg("avg_children")
                                                .field("children"))
                                )
                )
                .execute()
                .actionGet();
    }

    public void metricsAggregations() throws Exception{
        //Min Aggregation
        MinAggregationBuilder aggregation = AggregationBuilders
                .min("agg")
                .field("height");
        SearchResponse sr = transportClient.prepareSearch().addAggregation(aggregation).execute().actionGet();
        Min agg = sr.getAggregations().get("agg");
        double value = agg.getValue();

        //Max Aggregation
        MaxAggregationBuilder aggregation1 = AggregationBuilders
                .max("agg")
                .field("height");
        SearchResponse sr1 = transportClient.prepareSearch().addAggregation(aggregation1).execute().actionGet();
        Max agg1 = sr1.getAggregations().get("agg");
        double value1 = agg1.getValue();

        //Sum Aggregation
        SumAggregationBuilder aggregation2 = AggregationBuilders
                .sum("agg")
                .field("height");
        SearchResponse sr2 = transportClient.prepareSearch().addAggregation(aggregation2).execute().actionGet();
        Sum agg2 = sr2.getAggregations().get("agg");
        double value2 = agg2.getValue();

        //Avg Aggregation
        AvgAggregationBuilder aggregation3 = AggregationBuilders
                .avg("agg")
                .field("height");
        SearchResponse sr3 = transportClient.prepareSearch().addAggregation(aggregation3).execute().actionGet();
        Avg agg3 = sr3.getAggregations().get("agg");
        double value3 = agg3.getValue();

        //Stats Aggregation
        StatsAggregationBuilder aggregation4 = AggregationBuilders
                .stats("agg")
                .field("height");
        SearchResponse sr4 = transportClient.prepareSearch().addAggregation(aggregation4).execute().actionGet();
        Stats agg4 = sr4.getAggregations().get("agg");
        double min = agg4.getMin();
        double max = agg4.getMax();
        double avg = agg4.getAvg();
        double sum = agg4.getSum();
        long count = agg4.getCount();

        //Extended Stats Aggregation
        ExtendedStatsAggregationBuilder aggregation5 = AggregationBuilders
                .extendedStats("agg")
                .field("height");
        SearchResponse sr5 = transportClient.prepareSearch().addAggregation(aggregation5).execute().actionGet();
        ExtendedStats agg5 = sr5.getAggregations().get("agg");
        double min1 = agg5.getMin();
        double max1 = agg5.getMax();
        double avg1 = agg5.getAvg();
        double sum1 = agg5.getSum();
        long count1 = agg5.getCount();
        double stdDeviation = agg5.getStdDeviation();
        double sumOfSquares = agg5.getSumOfSquares();
        double variance = agg5.getVariance();

        //Value Count Aggregation
        ValueCountAggregationBuilder aggregation6 = AggregationBuilders
                .count("agg")
                .field("height");
        SearchResponse sr6 = transportClient.prepareSearch().addAggregation(aggregation6).execute().actionGet();
        ValueCount agg6 = sr6.getAggregations().get("agg");
        double value4 = agg6.getValue();

        //Prepare Aggregation
        PercentilesAggregationBuilder aggregation7 = AggregationBuilders
                .percentiles("agg")
                .field("height");
        //.percentiles(1.0, 5.0, 10.0, 20.0, 30.0, 75.0, 95.0, 99.0);
        SearchResponse sr7 = transportClient.prepareSearch().addAggregation(aggregation7).execute().actionGet();
        Percentiles agg7 = sr7.getAggregations().get("agg");
        for (Percentile entry: agg7){
            // Percent
            double percent = entry.getPercent();
            // Value
            double value7 = entry.getValue();

            System.out.println("percent [{ " + percent + " }], value [{ " + value7 + " }]");
        }

        //Cardinality Aggregation
        CardinalityAggregationBuilder aggregation8 = AggregationBuilders
                .cardinality("agg")
                .field("tags");
        //.percentiles(1.0, 5.0, 10.0, 20.0, 30.0, 75.0, 95.0, 99.0);
        SearchResponse sr8 = transportClient.prepareSearch().addAggregation(aggregation8).execute().actionGet();
        Cardinality agg8 = sr8.getAggregations().get("agg");
        long value8 = agg8.getValue();

        //Geo Bounds Aggregation
        GeoBoundsAggregationBuilder aggregation9 = AggregationBuilders
                .geoBounds("agg")
                .field("address.location");
        //.percentiles(1.0, 5.0, 10.0, 20.0, 30.0, 75.0, 95.0, 99.0);
        SearchResponse sr9 = transportClient.prepareSearch().addAggregation(aggregation9).execute().actionGet();
        GeoBounds agg9 = sr9.getAggregations().get("agg");
        GeoPoint bottomRight = agg9.bottomRight();
        GeoPoint topLeft = agg9.topLeft();
        System.out.println("bottomRight { " + bottomRight + " }, topLeft { " + topLeft + " }");

        //Top Hits Aggregation
        AggregationBuilder aggregation10 = AggregationBuilders
                .terms("agg")
                .field("gender")
                .subAggregation(
                        AggregationBuilders.topHits("top")
                        /*AggregationBuilders.topHits("top")
                                .explain(true)
                                .size(1)
                                .from(10)*/
                );
        //.percentiles(1.0, 5.0, 10.0, 20.0, 30.0, 75.0, 95.0, 99.0);
        SearchResponse sr10 = transportClient.prepareSearch().addAggregation(aggregation10).execute().actionGet();
        Terms agg10 = sr10.getAggregations().get("agg");
        for (Terms.Bucket entry: agg10.getBuckets()){
            String key = entry.getKey().toString();
            long docCount = entry.getDocCount();
            System.out.println("key [{ " + key + " }], doc_count [{ " + docCount + " }]");

            TopHits topHits = entry.getAggregations().get("top");
            for (SearchHit hit: topHits.getHits().getHits()){
                System.out.println(" -> id [{ " + hit.getId() + " }], _source [{ " + hit.getSourceAsString() + " }]");
            }
        }

        //Scripted Metric Aggregation
        ScriptedMetricAggregationBuilder aggregation11 = AggregationBuilders
                .scriptedMetric("agg")
                .initScript(new Script("state.heights = []"))
                .mapScript(new Script("state.heights.add(doc.gender.value == 'male' ? doc.height.value : -1.0 * doc.height.value)"))
                .combineScript(new Script("double heights_sum = 0.0; for (t in state.heights) { heights_sum += t } return heights_sum"));
        //.reduceScript(new Script("double heights_sum = 0.0; for (a in states) { heights_sum += a } return heights_sum"));
        SearchResponse sr11 = transportClient.prepareSearch().addAggregation(aggregation11).execute().actionGet();
        ScriptedMetric agg11 = sr11.getAggregations().get("agg");
        Object scriptedResult = agg11.aggregation();
        System.out.println("scriptedResult [{ " + scriptedResult + " }]");
    }

    /**
     * 批量聚合
     * @throws Exception
     */
    public void bucketAggregations() throws Exception{
        //Global Aggregation
        GlobalAggregationBuilder aggregation = AggregationBuilders.global("agg")
                .subAggregation(
                        AggregationBuilders.terms("genders").field("gender")
                );
        SearchResponse sr = transportClient.prepareSearch().addAggregation(aggregation).execute().actionGet();
        Global agg = sr.getAggregations().get("agg");
        agg.getDocCount();

        //Filter Aggregation
        FilterAggregationBuilder aggregation1 = AggregationBuilders.filter("agg",
                QueryBuilders.termQuery("gender", "male"));
        SearchResponse sr1 = transportClient.prepareSearch().addAggregation(aggregation1).execute().actionGet();
        Filter agg1 = sr1.getAggregations().get("agg");
        agg1.getDocCount();

        //Filters Aggregation
        FiltersAggregationBuilder aggregation2 = AggregationBuilders.filters("agg",
                new FiltersAggregator.KeyedFilter("men", QueryBuilders.termQuery("gender", "male")),
                new FiltersAggregator.KeyedFilter("women", QueryBuilders.termQuery("gender", "female")));
        SearchResponse sr2 = transportClient.prepareSearch().addAggregation(aggregation2).execute().actionGet();
        Filters agg2 = sr2.getAggregations().get("agg");
        for (Filters.Bucket entry: agg2.getBuckets()){
            String key = entry.getKeyAsString();
            long docCount = entry.getDocCount();
            System.out.println("key [{ " + key + " }], doc_count [{ " + docCount + " }]");
        }

        //Missing Aggregation
        MissingAggregationBuilder aggregation3 = AggregationBuilders.missing("agg").field("gender");
        SearchResponse sr3 = transportClient.prepareSearch().addAggregation(aggregation3).execute().actionGet();
        Missing agg3 = sr3.getAggregations().get("agg");
        agg3.getDocCount();

        //Nested Aggregation
        NestedAggregationBuilder aggregation4 = AggregationBuilders.nested("agg", "resellers");
        SearchResponse sr4 = transportClient.prepareSearch().addAggregation(aggregation4).execute().actionGet();
        Nested agg4 = sr4.getAggregations().get("agg");
        agg4.getDocCount();

        //Reverse Nested Aggregation
        NestedAggregationBuilder aggregation5 = AggregationBuilders
                .nested("agg", "resellers")
                .subAggregation(
                        AggregationBuilders
                                .terms("name")
                                .field("resellers.name")
                                .subAggregation(
                                        AggregationBuilders.reverseNested("reseller_to_product")
                                )
                );
        SearchResponse sr5 = transportClient.prepareSearch().addAggregation(aggregation5).execute().actionGet();
        Nested agg5 = sr5.getAggregations().get("agg");
        Terms name = agg5.getAggregations().get("name");
        for (Terms.Bucket bucket: name.getBuckets()){
            ReverseNested resellerToProduct = bucket.getAggregations().get("reseller_to_product");
            resellerToProduct.getDocCount();
        }

        //Children Aggregation
        /*AggregationBuilder aggregation6 = AggregationBuilders.children("agg", "reseller");
        SearchResponse sr6 = transportClient.prepareSearch().addAggregation(aggregation6).execute().actionGet();
        Children agg6 = sr6.getAggregations().get("agg");
        agg6.getDocCount();*/

        //Terms Aggregation
        AggregationBuilder aggregation7 = AggregationBuilders
                .terms("genders")
                .field("gender");
        SearchResponse sr7 = transportClient.prepareSearch().addAggregation(aggregation7).execute().actionGet();
        Terms genders = sr7.getAggregations().get("genders");
        for (Terms.Bucket entry: genders.getBuckets()){
            entry.getKey();
            entry.getDocCount();
        }

        //Order
        AggregationBuilders.terms("genders")
                .field("gender")
                .order(BucketOrder.count(true));
        AggregationBuilders.terms("genders")
                .field("gender")
                .order(BucketOrder.key(true));
        AggregationBuilders.terms("genders")
                .field("gender")
                .order(BucketOrder.aggregation("avg_height", false))
                .subAggregation(
                        AggregationBuilders.avg("avg_height").field("height")
                );
        AggregationBuilders.terms("genders")
                .field("gender")
                .order(BucketOrder.compound(
                        BucketOrder.aggregation("avg_height", false),
                        BucketOrder.count(true)
                ))
                .subAggregation(
                        AggregationBuilders.avg("avg_height").field("height")
                );

        //Significant Terms Aggregation
        AggregationBuilder aggregation9 = AggregationBuilders
                .significantTerms("significant_countries")
                .field("address.country");
        SearchResponse sr9 = transportClient.prepareSearch()
                .setQuery(QueryBuilders.termQuery("gender", "male"))
                .addAggregation(aggregation9)
                .get();
        SignificantTerms agg9 = sr9.getAggregations().get("significant_countries");
        for (SignificantTerms.Bucket entry: agg9.getBuckets()){
            entry.getKey();
            entry.getDocCount();
        }

        //Range Aggregation
        AggregationBuilder aggregation10 = AggregationBuilders
                .range("agg")
                .field("height")
                .addUnboundedTo(1.0f)
                .addRange(1.0f, 1.5f)
                .addUnboundedFrom(1.5f);
        SearchResponse sr10 = transportClient.prepareSearch().addAggregation(aggregation10).execute().actionGet();
        Range agg10 = sr10.getAggregations().get("agg");
        for (Range.Bucket entry: agg10.getBuckets()){
            String key = entry.getKeyAsString();
            Number from = (Number) entry.getFrom();
            Number to = (Number) entry.getTo();
            long docCount = entry.getDocCount();
            System.out.println("key [{ " + key + " }], from [{ " + from + " }], to [{ " + to + " }], doc_count [{ " + docCount + " }]");
        }

        //Date Range Aggregation
        AggregationBuilder aggregation11 = AggregationBuilders
                .dateRange("agg")
                .field("dateOfBirth")
                .format("yyyy")
                .addUnboundedTo("1950")
                .addRange("1950", "1960")
                .addUnboundedFrom("1960");
        SearchResponse sr11 = transportClient.prepareSearch().addAggregation(aggregation11).execute().actionGet();
        Range agg11 = sr11.getAggregations().get("agg");
        for (Range.Bucket entry: agg11.getBuckets()){
            String key = entry.getKeyAsString();
            DateTime fromAsDate = (DateTime) entry.getFrom();
            DateTime toAsDate = (DateTime) entry.getTo();
            long docCount = entry.getDocCount();
            System.out.println("key [{ " + key + " }], from [{ " + fromAsDate + " }], to [{ " + toAsDate + " }], doc_count [{ " + docCount + " }]");
        }

        //In Range Aggregation
        AggregationBuilder aggregation12 = AggregationBuilders
                .ipRange("agg")
                .field("ip")
                .addUnboundedTo("192.168.1.0")
                .addRange("192.168.1.0", "192.168.2.0")
                .addUnboundedFrom("192.168.2.0");
        /*AggregatorBuilder<?> aggregation =
                AggregationBuilders
                        .ipRange("agg")
                        .field("ip")
                        .addMaskRange("192.168.0.0/32")
                        .addMaskRange("192.168.0.0/24")
                        .addMaskRange("192.168.0.0/16");*/
        SearchResponse sr12 = transportClient.prepareSearch().addAggregation(aggregation12).execute().actionGet();
        Range agg12 = sr12.getAggregations().get("agg");
        for (Range.Bucket entry: agg12.getBuckets()){
            String key = entry.getKeyAsString();
            String fromAsString = (String) entry.getFromAsString();
            String toAsString = (String) entry.getTo();
            long docCount = entry.getDocCount();
            System.out.println("key [{ " + key + " }], from [{ " + fromAsString + " }], to [{ " + toAsString + " }], doc_count [{ " + docCount + " }]");
        }

        //Histogram Aggregation
        AggregationBuilder aggregation13 = AggregationBuilders
                .histogram("agg")
                .field("height")
                .interval(1);
        SearchResponse sr13 = transportClient.prepareSearch().addAggregation(aggregation13).execute().actionGet();
        Histogram agg13 = sr13.getAggregations().get("agg");

        for (Histogram.Bucket entry: agg13.getBuckets()){
            Number key = (Number) entry.getKey();
            long docCount = entry.getDocCount();

            System.out.println("key [{ " + key + " }], doc_count [{ " + docCount + " }]");
        }

        //Date Histogram Aggregation
        AggregationBuilder aggregation14 = AggregationBuilders
                .dateHistogram("agg")
                .field("dateOfBirth")
                .dateHistogramInterval(DateHistogramInterval.YEAR);
        /*AggregationBuilder aggregation =
                AggregationBuilders
                        .dateHistogram("agg")
                        .field("dateOfBirth")
                        .dateHistogramInterval(DateHistogramInterval.days(10));*/
        SearchResponse sr14 = transportClient.prepareSearch().addAggregation(aggregation14).execute().actionGet();
        Histogram agg14 = sr14.getAggregations().get("agg");
        for (Histogram.Bucket entry: agg14.getBuckets()){
            DateTime key = (DateTime) entry.getKey();
            String keyAsString = entry.getKeyAsString();
            long docCount = entry.getDocCount();

            System.out.println("key [{ " + keyAsString + " }], date [{ " + key.getYear() + " }], doc_count [{ " + docCount + " }]");
        }

        //Geo Distance Aggregation
        AggregationBuilder aggregation15 = AggregationBuilders
                .geoDistance("agg", new GeoPoint(48.84237171118314,2.33320027692004))
                .field("address.location")
                .unit(DistanceUnit.KILOMETERS)
                .addUnboundedTo(3.0)
                .addRange(3.0, 10.0)
                .addRange(10.0, 500.0);
        SearchResponse sr15 = transportClient.prepareSearch().addAggregation(aggregation15).execute().actionGet();
        Range agg15 = sr15.getAggregations().get("agg");
        for (Range.Bucket entry: agg15.getBuckets()){
            String key = entry.getKeyAsString();
            Number from = (Number) entry.getFrom();
            Number to = (Number) entry.getTo();
            long docCount = entry.getDocCount();

            System.out.println("key [{ " + key + " }], from [{ " + from + " }], to [{ " + to + " }], doc_count [{ " + docCount + " }]");
        }

        //Geo Hash Grid Aggregation
        AggregationBuilder aggregation16 = AggregationBuilders
                .geohashGrid("agg")
                .field("address.location")
                .precision(4);
        SearchResponse sr16 = transportClient.prepareSearch().addAggregation(aggregation16).execute().actionGet();
        GeoHashGrid agg16 = sr16.getAggregations().get("agg");
        for (GeoHashGrid.Bucket entry: agg16.getBuckets()){
            String keyAsString = entry.getKeyAsString();
            GeoPoint key = (GeoPoint) entry.getKey();
            long docCount = entry.getDocCount();

            System.out.println("key [{ " + keyAsString + " }], point { " + key + " }, doc_count [{ " + docCount + " }]");
        }
    }

    /**
     * query和search结合使用
     * @throws Exception
     */
    public void Query() throws Exception{
        //Match All Query
        matchAllQuery();

        //Full text queries
        matchQuery("name", "kimchy elasticsearch");

        multiMatchQuery("kimchy elasticsearch", "user", "message");

        commonTermsQuery("name", "kimchy");

        queryStringQuery("+kimchy -elasticsearch");

        simpleQueryStringQuery("+kimchy -elasticsearch");

        //Term level queries
        termQuery("name", "kimchy");

        termsQuery("tags", "blue", "pill");

        rangeQuery("price")
                .from(5)
                .to(10)
                .includeLower(true)
                .includeUpper(true);

        rangeQuery("age")
                .gte("10")
                .lt("20");

        existsQuery("name");

        prefixQuery("brand", "heine");

        wildcardQuery("user", "k?mch*");

        regexpQuery("name.first", "s.*y");

        fuzzyQuery("name", "kimchy");

        typeQuery("my_type");

        idsQuery("my_type", "type2")
                .addIds("1", "4", "100");

        idsQuery()
                .addIds("1", "4", "100");

        //Compound queries
        constantScoreQuery(termQuery("name", "kimchy")).boost(2.0f);

        boolQuery().must(termQuery("content", "test1"))
                .must(termQuery("content", "test4"))
                .mustNot(termQuery("content", "test2"))
                .should(termQuery("content", "test3"))
                .filter(termQuery("content", "test5"));

        disMaxQuery()
                .add(termQuery("name", "kimchy"))
                .add(termQuery("name", "elasticsearch"))
                .boost(1.2f)
                .tieBreaker(0.7f);

        FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        matchQuery("name", "kimchy"),
                        randomFunction()),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        exponentialDecayFunction("age", 0L, 1L)
                )
        };
        functionScoreQuery(functions);

        boostingQuery(
                termQuery("name", "kimchy"),
                termQuery("name", "dadoonet")
        ).negativeBoost(0.2f);

        //Joining queries
        nestedQuery(
                "obj1",
                boolQuery().must(matchQuery("obj1.name", "blue"))
                        .must(rangeQuery("obj1.count").gt(5)),
                ScoreMode.Avg
        );

        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        TransportClient transportClient = new PreBuiltTransportClient(settings);
        transportClient.addTransportAddress(new TransportAddress(new InetSocketAddress(InetAddresses.forString("127.0.0.1"), 9300)));
        JoinQueryBuilders.hasChildQuery(
                "blog_tag",
                termQuery("tag", "something"),
                ScoreMode.None
        );

        JoinQueryBuilders.hasParentQuery(
                "blog",
                termQuery("tag", "something"),
                false
        );

        //Geo queries 暂时略

        //Specialized queries
        String[] fields = {"name.first", "name.last"};
        String[] texts = {"text like this one"};
        moreLikeThisQuery(fields, texts, null)
                .minTermFreq(1)
                .maxQueryTerms(12);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("param1", 5);
        scriptQuery(new Script(
                ScriptType.STORED,
                null,
                "myscript",
                Collections.singletonMap("param1", 5)
        ));
        scriptQuery(new Script("doc['num1'].value > 1"));

        transportClient.admin().indices().prepareCreate("myIndexName")
                .addMapping("_doc", "query", "type=percolator", "content", "type=text")
                .get();
        QueryBuilder qb = termQuery("content", "amazing");

        transportClient.prepareIndex("myIndexName", "_doc", "myDesignatedQueryName")
                .setSource(jsonBuilder()
                        .startObject()
                        .field("query", qb)
                        .endObject()
                )
                .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                .get();

        XContentBuilder docBuilder = XContentFactory.jsonBuilder().startObject();
        docBuilder.field("content", "This is amazing!");
        docBuilder.endObject();

        //PercolateQueryBuilder percolateQuery = new PercolateQueryBuilder("query", "_doc", BytesReference.toBytes(docBuilder.bytes()));

        String query = "{\"term\": {\"user\":\"kimchy\"}}";
        wrapperQuery(query);

        //Span Term Query
        spanTermQuery("user", "kimchy");

        spanMultiTermQueryBuilder(prefixQuery("user", "ki"));

        spanFirstQuery(spanTermQuery("user", "kimchy"), 3);

        spanNearQuery(spanTermQuery("field", "value1"),12)
                .addClause(spanTermQuery("field", "value2"))
                .addClause(spanTermQuery("field", "value3"))
                .inOrder(false);

        spanOrQuery(spanTermQuery("field", "value1"))
                .addClause(spanTermQuery("field", "valu2"))
                .addClause(spanTermQuery("field", "value3"));

        spanNotQuery(spanTermQuery("field", "value1"),spanTermQuery("field", "value2"));

        spanContainingQuery(
                spanNearQuery(spanTermQuery("field1", "bar"), 5)
                        .addClause(spanTermQuery("field1", "baz"))
                        .inOrder(true),
                spanTermQuery("field1", "foo")
        );

        spanWithinQuery(
                spanNearQuery(spanTermQuery("field1", "bar"), 5)
                        .addClause(spanTermQuery("field1", "baz"))
                        .inOrder(true),
                spanTermQuery("field1", "foo")
        );
    }








    /*------------------------------------------------ document Api start --------------------------------------------*/

    /**
     * 增，插入记录
     * @throws Exception
     */
    public void index() throws Exception{
        //String
        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                "1"
        );
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        //Map
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest indexRequest = new IndexRequest("posts", "doc", "1").source(jsonMap);

        //XContentBuilder automatically converted to JSON
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.timeField("postDate" , new Date());
            builder.field("message", "trying out Elasticsearch");
        }
        builder.endObject();
        IndexRequest indexRequest1 = new IndexRequest("posts", "doc", "1")
                .source(builder);

        //source -> key-pairs
        IndexRequest indexRequest2 = new IndexRequest("posts", "doc", "1")
                .source("user", "kimchy",
                        "postDate", new Date(),
                        "message", "trying out Elasticsearch"
                );

        //Optional arguments
        request.routing("routing");

        request.parent("parent");

        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        request.setRefreshPolicy("wait_for");

        request.version(2);

        request.versionType(VersionType.EXTERNAL);

        request.opType(DocWriteRequest.OpType.CREATE);
        request.opType("create");

        request.setPipeline("pipeline");

        //Synchronous Execution
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<IndexResponse> listener = new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.indexAsync(request, RequestOptions.DEFAULT, listener);

        //Index Response
        String index = indexResponse.getIndex();
        String type = indexResponse.getType();
        String id = indexResponse.getId();
        long version = indexResponse.getVersion();
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {

        } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {

        }
        ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();
            }
        }

        //throw Exception
        IndexRequest request1 = new IndexRequest("posts", "doc", "1")
                .source("field", "value")
                .version(1);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT){

            }
        }

        //in case opType throw Exception
        IndexRequest request2 = new IndexRequest("posts", "doc", "1")
                .source("field", "value")
                .opType(DocWriteRequest.OpType.CREATE);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT){

            }
        }
    }

    /**
     * 根据 id 获取数据
     * @throws Exception
     */
    public void get() throws Exception{
        GetRequest request = new GetRequest("posts", "doc", "1");

        //optional arguments
        request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        String[] includes = new String[]{"message", "*Date"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        request.fetchSourceContext(fetchSourceContext);

        //specific fields
        String[] includes1 = new String[]{"message", "*Date"};
        String[] excludes1 = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext1 = new FetchSourceContext(true, includes1, excludes1);
        request.fetchSourceContext(fetchSourceContext1);

        //source exclusion for specific fields
        request.storedFields("message");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String message = response.getField("message").getValue();

        request.routing("routing");
        request.parent("parent");
        request.preference("preference");
        request.realtime(false);
        request.refresh(true);
        request.version(2);
        request.versionType(VersionType.EXTERNAL);

        //Synchronous Execution
        GetResponse getResponse = client.get(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<GetResponse> listener = new ActionListener<GetResponse>() {
            @Override
            public void onResponse(GetResponse getResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.getAsync(request, RequestOptions.DEFAULT, listener);

        //Get Response
        String index = getResponse.getIndex();
        String type = getResponse.getType();
        String id = getResponse.getId();
        if (getResponse.isExists()) {
            long version = getResponse.getVersion();
            String sourceAsString = getResponse.getSourceAsString();
            Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
            byte[] sourceAsBytes = getResponse.getSourceAsBytes();
        } else {

        }

        //throw Exception
        GetRequest request1 = new GetRequest("does_not_exist", "doc", "1");
        try {
            GetResponse getResponse1 = client.get(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {

            }
        }

        //version
        try {
            GetRequest request2 = new GetRequest("posts", "doc", "1").version(2);
            GetResponse getResponse2 = client.get(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.CONFLICT) {

            }
        }
    }

    /**
     * 存在
     * @throws Exception
     */
    public void exists() throws Exception{
        GetRequest getRequest = new GetRequest("posts", "doc", "1");
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        //Synchronous Execution
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<Boolean> listener = new ActionListener<Boolean>() {
            @Override
            public void onResponse(Boolean exists) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.existsAsync(getRequest, RequestOptions.DEFAULT, listener);
    }

    public void delete() throws Exception{
        DeleteRequest request = new DeleteRequest("posts", "doc", "1");

        //optional arguments
        request.routing("routing");
        request.parent("parent");
        request.timeout(TimeValue.timeValueMinutes(2));
        request.timeout("2m");
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        request.setRefreshPolicy("wait_for");
        request.version(2);
        request.versionType(VersionType.EXTERNAL);

        //Synchronous Execution
        DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<DeleteResponse> listener = new ActionListener<DeleteResponse>() {
            @Override
            public void onResponse(DeleteResponse deleteResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.deleteAsync(request, RequestOptions.DEFAULT, listener);

        //Delete Response
        String index = deleteResponse.getIndex();
        String type = deleteResponse.getType();
        String id = deleteResponse.getId();
        long version = deleteResponse.getVersion();
        ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();
            }
        }

        // document was not found
        DeleteRequest request1 = new DeleteRequest("posts", "doc", "does_not_exist");
        DeleteResponse deleteResponse1 = client.delete(request1, RequestOptions.DEFAULT);
        if (deleteResponse1.getResult() == DocWriteResponse.Result.NOT_FOUND) {

        }

        //throw Exception
        try {
            DeleteRequest request2 = new DeleteRequest("posts", "doc", "1").version(2);
            DeleteResponse deleteResponse2 = client.delete(request2, RequestOptions.DEFAULT);
        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.CONFLICT) {

            }
        }
    }

    public void update() throws Exception{
        UpdateRequest request = new UpdateRequest("posts", "doc", "1");
        Map<String, Object> parameters = Collections.singletonMap("count", 4);

        //inline script
        Script inline = new Script(ScriptType.INLINE, "painless", "ctx._source.field += params.count", parameters);
        request.script(inline);

        //stored script
        Script stored = new Script(ScriptType.STORED, null, "increment-field", parameters);
        request.script(stored);

        //partial document String
        String jsonString = "{" +
                "\"updated\":\"2017-01-01\"," +
                "\"reason\":\"daily update\"" +
                "}";
        request.doc(jsonString, XContentType.JSON);

        //partial document Map
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("updated", new Date());
        jsonMap.put("reason", "daily update");
        request.doc(jsonMap);

        //partial document XContentBuilder
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.timeField("updated", new Date());
            builder.field("reason", "daily update");
        }
        builder.endObject();
        request.doc(builder);

        //partial document Object key-pairs
        request.doc("updated", new Date(),
                "reason", "daily update");

        //upserts
        String jsonString1 = "{\"created\":\"2017-01-01\"}";
        request.upsert(jsonString1, XContentType.JSON);

        //optional arguments
        request.routing("routing");
        request.parent("parent");
        request.timeout(TimeValue.timeValueMinutes(2));
        request.timeout("2m");
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        request.setRefreshPolicy("wait_for");
        request.retryOnConflict(3);
        request.fetchSource(true);

        //Enable source retrieval
        String[] includes = new String[]{"updated", "r*"};
        String[] excludes = Strings.EMPTY_ARRAY;
        request.fetchSource(new FetchSourceContext(true, includes, excludes));

        //specific fields
        String[] includes1 = Strings.EMPTY_ARRAY;
        String[] excludes1 = new String[]{"updated"};
        request.fetchSource(new FetchSourceContext(true, includes1, excludes1));

        request.version(2);
        request.detectNoop(false);

        request.scriptedUpsert(true);
        request.docAsUpsert(true);

        request.waitForActiveShards(2);
        request.waitForActiveShards(ActiveShardCount.ALL);

        //Synchronous Execution
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<UpdateResponse> listener = new ActionListener<UpdateResponse>() {
            @Override
            public void onResponse(UpdateResponse updateResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.updateAsync(request, RequestOptions.DEFAULT, listener);

        //update Response
        String index = updateResponse.getIndex();
        String type = updateResponse.getType();
        String id = updateResponse.getId();
        long version = updateResponse.getVersion();
        if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {

        } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {

        } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {

        } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {

        }

        GetResult result = updateResponse.getGetResult();
        if (result.isExists()) {
            String sourceAsString = result.sourceAsString();
            Map<String, Object> sourceAsMap = result.sourceAsMap();
            byte[] sourceAsBytes = result.source();
        } else {

        }

        //check for shard failures
        ReplicationResponse.ShardInfo shardInfo = updateResponse.getShardInfo();
        if (shardInfo.getTotal() != shardInfo.getSuccessful()) {

        }
        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                String reason = failure.reason();
            }
        }

        //throw Exception
        UpdateRequest request1 = new UpdateRequest("posts", "type", "does_not_exist")
                .doc("field", "value");
        try {
            UpdateResponse updateResponse1 = client.update(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {

            }
        }

        //throw Exception version conflict
        UpdateRequest request2 = new UpdateRequest("posts", "doc", "1")
                .doc("field", "value")
                .version(1);
        try {
            UpdateResponse updateResponse2 = client.update(request, RequestOptions.DEFAULT);
        } catch(ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {

            }
        }
    }

    public void bulk() throws Exception{
        BulkRequest request = new BulkRequest();
        //Index
        request.add(new IndexRequest("posts", "doc", "1")
                .source(XContentType.JSON, "field", "foo"));
        request.add(new IndexRequest("posts", "doc", "2")
                .source(XContentType.JSON, "field", "bar"));
        request.add(new IndexRequest("posts", "doc", "3")
                .source(XContentType.JSON, "field", "baz"));

        //Other
        request.add(new DeleteRequest("posts", "doc", "3"));
        request.add(new UpdateRequest("posts", "doc", "2")
                .doc(XContentType.JSON, "other", "test"));
        request.add(new IndexRequest("posts", "doc", "4")
                .source(XContentType.JSON, "field", "baz"));

        //optional arguments
        request.timeout(TimeValue.timeValueMinutes(2));
        request.timeout("2m");
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        request.setRefreshPolicy("wait_for");
        request.waitForActiveShards(2);
        request.waitForActiveShards(ActiveShardCount.ALL);

        //Synchronous Execution
        BulkResponse bulkResponses = client.bulk(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.bulkAsync(request, RequestOptions.DEFAULT, listener);

        //Bulk Response
        for (BulkItemResponse bulkItemResponse: bulkResponses){
            DocWriteResponse itemResponse = bulkItemResponse.getResponse();

            if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                    || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                IndexResponse indexResponse = (IndexResponse) itemResponse;

            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                UpdateResponse updateResponse = (UpdateResponse) itemResponse;

            } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
            }
        }

        //failures
        if (bulkResponses.hasFailures()){

        }

        for (BulkItemResponse bulkItemResponse : bulkResponses) {
            if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();

            }
        }

        //Bulk Processor
        BulkProcessor.Listener listener1 = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long l, BulkRequest bulkRequest) {

            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {

            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {

            }
        };

        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer =
                (request1, bulkListener) -> client.bulkAsync(request1, RequestOptions.DEFAULT, bulkListener);
        BulkProcessor bulkProcessor = BulkProcessor.builder(bulkConsumer, listener1).build();

        BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer1 =
                (request2, bulkListener) -> client.bulkAsync(request2, RequestOptions.DEFAULT, bulkListener);
        BulkProcessor.Builder builder = BulkProcessor.builder(bulkConsumer1, listener1);
        builder.setBulkActions(500);
        builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
        builder.setConcurrentRequests(0);
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));
        builder.setBackoffPolicy(BackoffPolicy
                .constantBackoff(TimeValue.timeValueSeconds(1L), 3));

        //Once the BulkProcessor is created requests can be added to it:
        IndexRequest one = new IndexRequest("posts", "doc", "1").
                source(XContentType.JSON, "title",
                        "In which order are my Elasticsearch queries executed?");
        IndexRequest two = new IndexRequest("posts", "doc", "2")
                .source(XContentType.JSON, "title",
                        "Current status and upcoming changes in Elasticsearch");
        IndexRequest three = new IndexRequest("posts", "doc", "3")
                .source(XContentType.JSON, "title",
                        "The Future of Federated Search in Elasticsearch");
        bulkProcessor.add(one);
        bulkProcessor.add(two);
        bulkProcessor.add(three);

        //the listener provides methods to access to the BulkRequest and the BulkResponse:
        BulkProcessor.Listener listener2 = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                int numberOfActions = request.numberOfActions();
                String s = String.format("Executing bulk [{}] with {} requests",
                        executionId, numberOfActions);
                System.out.println(s);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request,
                                  BulkResponse response) {
                if (response.hasFailures()) {
                    String s = String.format("Bulk [{}] executed with failures", executionId);
                    System.out.println(s);
                } else {
                    String s = String.format("Bulk [{}] completed in {} milliseconds",
                            executionId, response.getTook().getMillis());
                    System.out.println(s);
                }
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                String s = String.format("Failed to execute bulk", failure);
                System.out.println(s);
            }
        };

        boolean terminated = bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);

    }

    /**
     * 根据id批量获取数据
     * @throws Exception
     */
    public void multiGet1() throws Exception{
        MultiGetRequest request = new MultiGetRequest();
        request.add(new MultiGetRequest.Item(
                "index",
                "type",
                "example_id"
        ));
        request.add(new MultiGetRequest.Item("index", "type", "another_id"));

        //optional arguments
        request.add(new MultiGetRequest.Item("index", "type", "example_id")
                .fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE));

        String[] includes = new String[] {"foo", "*r"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext =
                new FetchSourceContext(true, includes, excludes);
        request.add(new MultiGetRequest.Item("index", "type", "example_id")
                .fetchSourceContext(fetchSourceContext));

        String[] includes1 = Strings.EMPTY_ARRAY;
        String[] excludes1 = new String[] {"foo", "*r"};
        FetchSourceContext fetchSourceContext1 =
                new FetchSourceContext(true, includes1, excludes1);
        request.add(new MultiGetRequest.Item("index", "type", "example_id")
                .fetchSourceContext(fetchSourceContext));

        request.add(new MultiGetRequest.Item("index", "type", "example_id")
                .storedFields("foo"));
        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
        MultiGetItemResponse item = response.getResponses()[0];
        String value = item.getResponse().getField("foo").getValue();

        request.add(new MultiGetRequest.Item("index", "type", "with_routing")
                .routing("some_routing"));
        request.add(new MultiGetRequest.Item("index", "type", "with_parent")
                .parent("some_parent"));
        request.add(new MultiGetRequest.Item("index", "type", "with_version")
                .versionType(VersionType.EXTERNAL)
                .version(10123L));

        request.preference("some_preference");
        request.realtime(false);
        request.refresh(true);

        //Synchronous Execution
        MultiGetResponse responses = client.mget(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<MultiGetResponse> listener = new ActionListener<MultiGetResponse>() {
            @Override
            public void onResponse(MultiGetResponse response) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.mgetAsync(request, RequestOptions.DEFAULT, listener);

        //Multi Get Response
        MultiGetItemResponse firstItem = response.getResponses()[0];
        GetResponse firstGet = firstItem.getResponse();
        String index = firstItem.getIndex();
        String type = firstItem.getType();
        String id = firstItem.getId();
        if (firstGet.isExists()) {
            long version = firstGet.getVersion();
            String sourceAsString = firstGet.getSourceAsString();
            Map<String, Object> sourceAsMap = firstGet.getSourceAsMap();
            byte[] sourceAsBytes = firstGet.getSourceAsBytes();
        } else {

        }

        MultiGetRequest request2 = new MultiGetRequest();
        request.add(new MultiGetRequest.Item("index", "type", "example_id")
                .version(1000L));
        MultiGetResponse response2 = client.mget(request, RequestOptions.DEFAULT);
        MultiGetItemResponse item2 = response.getResponses()[0];
        Exception e = item.getFailure().getFailure();
        ElasticsearchException ee = (ElasticsearchException) e;
    }

    /*------------------------------------------------ document Api end ----------------------------------------------*/

    /*------------------------------------------------ search Api 多条件查询 start ----------------------------------------------*/

    public void search() throws Exception{
        //match all query
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        //optional arguments
        SearchRequest searchRequest1 = new SearchRequest("posts");
        searchRequest1.types("doc");
        searchRequest1.routing("routing");
        searchRequest.indicesOptions(IndicesOptions.lenientExpandOpen());
        searchRequest.preference("_local");

        //using the SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        SearchRequest searchRequest2 = new SearchRequest();
        //index 数据库
        searchRequest2.indices("posts");
        searchRequest2.source(sourceBuilder);

        //Building queries
        //One way, QueryBuilder can be created using its constructor 使用QueryBuilder的构造函数
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("user", "kimchy");
        matchQueryBuilder.fuzziness(Fuzziness.AUTO);
        matchQueryBuilder.prefixLength(3);
        matchQueryBuilder.maxExpansions(10);
        //Two way, QueryBuilder objects can also be created using the QueryBuilders utility class. 直接使用matchQuery
        QueryBuilder matchQueryBuilder1 = matchQuery("user", "kimchy")
                .fuzziness(Fuzziness.AUTO)
                .prefixLength(3)
                .maxExpansions(10);

        searchSourceBuilder.query(matchQueryBuilder1);

        //Specifying Sorting 指定排序
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        sourceBuilder.sort(new FieldSortBuilder("_uid").order(SortOrder.ASC));

        //Source filtering, turn off _source retrieval completely
        sourceBuilder.fetchSource(false);
        //an array of one or more wildcard patterns to control which fields get included or excluded in a more fine grained way
        String[] includeFields = new String[] {"title", "user", "innerObject.*"};
        String[] excludeFields = new String[] {"_type"};
        sourceBuilder.fetchSource(includeFields, excludeFields);

        //Requesting Highlighting
        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitile = new HighlightBuilder.Field("title");
        highlightTitile.highlighterType("unified");
        highlightBuilder.field(highlightTitile);

        HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("user");
        highlightBuilder.field(highlightUser);
        searchSourceBuilder1.highlighter(highlightBuilder);

        //Requesting Aggregations
        SearchSourceBuilder searchSourceBuilder2 = new SearchSourceBuilder();
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("by_company")
                .field("company.keyword");
        aggregation.subAggregation(AggregationBuilders.avg("average_age")
                .field("age"));
        searchSourceBuilder2.aggregation(aggregation);

        //Requesting Suggestions
        SearchSourceBuilder searchSourceBuilder3 = new SearchSourceBuilder();
        SuggestionBuilder termSuggestionBuilder = SuggestBuilders.termSuggestion("user").text("kmichy");
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder);
        searchSourceBuilder3.suggest(suggestBuilder);

        //Profiling Queries and Aggregations
        SearchSourceBuilder searchSourceBuilder4 = new SearchSourceBuilder();
        searchSourceBuilder4.profile(true);

        //Synchronous Execution
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<SearchResponse> listener = new ActionListener<SearchResponse>() {
            @Override
            public void onResponse(SearchResponse searchResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener);

        //SearchResponse
        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();
        int totalShards = searchResponse.getTotalShards();
        int successfulShards = searchResponse.getSuccessfulShards();
        int failedShards = searchResponse.getFailedShards();
        for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
            // failures should be handled here
        }

        //Retrieving SearchHits 获取结果数据
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        float maxScore = hits.getMaxScore();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            // do something with the SearchHit
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();

            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String documentTitle = (String) sourceAsMap.get("title");
            List<Object> users = (List<Object>) sourceAsMap.get("user");
            Map<String, Object> innerObject =
                    (Map<String, Object>) sourceAsMap.get("innerObject");
        }

        //Retrieving Highlighting
        SearchHits hits1 = searchResponse.getHits();
        for (SearchHit hit : hits1.getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlight = highlightFields.get("title");
            Text[] fragments = highlight.fragments();
            String fragmentString = fragments[0].string();
        }

        //Retrieving Aggregations
        Aggregations aggregations = searchResponse.getAggregations();
        Terms byCompanyAggregation = aggregations.get("by_company");
        Terms.Bucket elasticBucket = byCompanyAggregation.getBucketByKey("Elastic");
        Avg averageAge = elasticBucket.getAggregations().get("average_age");
        double avg = averageAge.getValue();

        Range range = aggregations.get("by_company");
        Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
        Terms companyAggregation = (Terms) aggregationMap.get("by_company");

        List<Aggregation> aggregationList = aggregations.asList();
        for (Aggregation agg : aggregations) {
            String type = agg.getType();
            if (type.equals(TermsAggregationBuilder.NAME)) {
                Terms.Bucket elasticBucket1 = ((Terms) agg).getBucketByKey("Elastic");
                long numberOfDocs = elasticBucket1.getDocCount();
            }
        }

        //Retrieving Suggestions
        Suggest suggest = searchResponse.getSuggest();
        TermSuggestion termSuggestion = suggest.getSuggestion("suggest_user");
        for (TermSuggestion.Entry entry : termSuggestion.getEntries()) {
            for (TermSuggestion.Entry.Option option : entry) {
                String suggestText = option.getText().string();
            }
        }

        //Retrieving Profiling Results
        Map<String, ProfileShardResult> profilingResults =
                searchResponse.getProfileResults();
        for (Map.Entry<String, ProfileShardResult> profilingResult : profilingResults.entrySet()) {
            String key = profilingResult.getKey();
            ProfileShardResult profileShardResult = profilingResult.getValue();
        }
    }

    public void searchScroll() throws Exception{
        //Initialize the search scroll context
        SearchRequest searchRequest = new SearchRequest("posts");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchQuery("title", "Elasticsearch"));
        searchSourceBuilder.size(10);
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1L));
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        String scrollId = searchResponse.getScrollId();
        SearchHits hits = searchResponse.getHits();

        //Retrieve all the relevant documents
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueSeconds(30));
        SearchResponse searchResponse1 = client.scroll(scrollRequest, RequestOptions.DEFAULT);
        scrollId = searchResponse1.getScrollId();
        hits = searchResponse1.getHits();

        //optional arguments
        scrollRequest.scroll(TimeValue.timeValueSeconds(60L));
        scrollRequest.scroll("60s");

        //Synchronous Execution
        SearchResponse searchResponse2 = client.scroll(scrollRequest, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<SearchResponse> scrollListener =
                new ActionListener<SearchResponse>() {
                    @Override
                    public void onResponse(SearchResponse searchResponse) {

                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                };
        client.scrollAsync(scrollRequest, RequestOptions.DEFAULT, scrollListener);


        //Full example
        final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1L));
        SearchRequest searchRequest1 = new SearchRequest("posts");
        searchRequest1.scroll(scroll);
        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.query(matchQuery("title", "Elasticsearch"));
        searchRequest.source(searchSourceBuilder1);

        SearchResponse searchResponse3 = client.search(searchRequest1, RequestOptions.DEFAULT);
        String scrollId1 = searchResponse3.getScrollId();
        SearchHit[] searchHits = searchResponse3.getHits().getHits();

        while (searchHits != null && searchHits.length > 0) {
            SearchScrollRequest scrollRequest2 = new SearchScrollRequest(scrollId);
            scrollRequest2.scroll(scroll);
            searchResponse = client.scroll(scrollRequest2, RequestOptions.DEFAULT);
            scrollId = searchResponse.getScrollId();
            searchHits = searchResponse.getHits().getHits();

        }

        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.addScrollId(scrollId);
        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        boolean succeeded = clearScrollResponse.isSucceeded();
    }

    public void multiSearch1() throws Exception{
        MultiSearchRequest request = new MultiSearchRequest();
        SearchRequest firstSearchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));
        firstSearchRequest.source(searchSourceBuilder);
        request.add(firstSearchRequest);

        SearchRequest secondSearchRequest = new SearchRequest();
        searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("user", "luca"));
        secondSearchRequest.source(searchSourceBuilder);
        request.add(secondSearchRequest);

        //optional arguments
        SearchRequest searchRequest = new SearchRequest("posts");
        searchRequest.types("doc");

        //Synchronous Execution
        MultiSearchResponse response = client.msearch(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<MultiSearchResponse> listener = new ActionListener<MultiSearchResponse>() {
            @Override
            public void onResponse(MultiSearchResponse response) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.msearchAsync(request, RequestOptions.DEFAULT, listener);

        //MultiSearchResponse
        MultiSearchResponse.Item firstResponse = response.getResponses()[0];
        SearchResponse searchResponse = firstResponse.getResponse();
        MultiSearchResponse.Item secondResponse = response.getResponses()[1];
        searchResponse = secondResponse.getResponse();
    }

    /**
     * 查询模板
     * @throws Exception
     */
    public void searchTemplate1() throws Exception{
        //Inline Templates
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest("posts"));
        request.setScriptType(ScriptType.INLINE);
        request.setScript(
                "{" +
                        "  \"query\": { \"match\": { \"{{ field }}\": \"{{ value }}\" } }," +
                        "  \"size\": \"{{ size }}\"" +
                        "}");
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field", "title");
        scriptParams.put("value", "elasticsearch");
        scriptParams.put("size", 5);
        request.setScriptParams(scriptParams);

        //Registered Templates
        Request scriptRequest = new Request("POST", "_scripts/title_search");
        scriptRequest.setJsonEntity(
                "{" +
                        "  \"script\": {" +
                        "        \"lang\": \"mustache\"," +
                        "        \"source\": {" +
                        "             \"query\": { \"match\": { \"{{field}}\": \"{{value}}\" } }," +
                        "             \"size\": \"{{size}}\"" +
                        "          }" +
                        "     }" +
                        "}"
        );
        Response scriptResponse = restClient.performRequest(scriptRequest);

        //instead of providing an inline script
        request.setRequest(new SearchRequest("posts"));
        request.setScriptType(ScriptType.STORED);
        request.setScript("title_search");
        Map<String, Object> params = new HashMap<>();
        params.put("field", "title");
        params.put("value", "elasticsearch");
        params.put("size", 5);
        request.setScriptParams(params);

        //Rendering Templates
        request.setSimulate(true);

        //Optional Arguments
        request.setExplain(true);
        request.setProfile(true);

        //Synchronous Execution
        SearchTemplateResponse response = client.searchTemplate(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<SearchTemplateResponse> listener = new ActionListener<SearchTemplateResponse>() {
            @Override
            public void onResponse(SearchTemplateResponse response) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.searchTemplateAsync(request, RequestOptions.DEFAULT, listener);

        //SearchTemplate Response
        SearchTemplateResponse response1 = client.searchTemplate(request, RequestOptions.DEFAULT);
        SearchResponse searchResponse = response1.getResponse();
        BytesReference source = response1.getSource();
    }

    /**
     * 多个查询模板执行
     * @throws Exception
     */
    public void MultiSearchTemplate() throws Exception{
        String[] searchTerms = {"elasticsearch", "logstash", "kibana"};
        MultiSearchTemplateRequest multiRequest = new MultiSearchTemplateRequest();
        for (String searchTerm: searchTerms) {
            SearchTemplateRequest request = new SearchTemplateRequest();
            request.setRequest(new SearchRequest("posts"));

            request.setScriptType(ScriptType.INLINE);
            request.setScript(
                    "{" +
                            " \"query\": { \"match\": { \"{{field}}\": \"{{value}}\" }}," +
                            " \"size\": \"{{size}}\"" +
                            "}"
            );

            Map<String, Object> scriptParams = new HashMap<>();
            scriptParams.put("field", "title");
            scriptParams.put("value", searchTerm);
            scriptParams.put("size", 5);
            request.setScriptParams(scriptParams);

            multiRequest.add(request);
        }

        //同步执行
        MultiSearchTemplateResponse multiResponse = client.msearchTemplate(multiRequest, RequestOptions.DEFAULT);

        //异步执行
        ActionListener<MultiSearchTemplateResponse> listener = new ActionListener<MultiSearchTemplateResponse>() {
            @Override
            public void onResponse(MultiSearchTemplateResponse response) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.msearchTemplateAsync(multiRequest, RequestOptions.DEFAULT, listener);

        //MultiSearchTemplateResponse
        for (MultiSearchTemplateResponse.Item item : multiResponse.getResponses()) {
            if (item.isFailure()) {
                String error = item.getFailureMessage();
            } else {
                SearchTemplateResponse searchTemplateResponse = item.getResponse();
                SearchResponse searchResponse = searchTemplateResponse.getResponse();
                searchResponse.getHits();
            }
        }
    }

    public void FieldCapabilities() throws Exception{
        FieldCapabilitiesRequest request = new FieldCapabilitiesRequest()
                .fields("user")
                .indices("posts", "authors", "contributors");

        request.indicesOptions(IndicesOptions.lenientExpandOpen());

        //Synchronous Execution
        FieldCapabilitiesResponse response = client.fieldCaps(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<FieldCapabilitiesResponse> listener = new ActionListener<FieldCapabilitiesResponse>() {
            @Override
            public void onResponse(FieldCapabilitiesResponse response) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.fieldCapsAsync(request, RequestOptions.DEFAULT, listener);

        //FieldCapabilitiesResponse
        Map<String, FieldCapabilities> userResponse = response.getField("user");
        FieldCapabilities textCapabilities = userResponse.get("keyword");

        boolean isSearchable = textCapabilities.isSearchable();
        boolean isAggregatable = textCapabilities.isAggregatable();

        String[] indices = textCapabilities.indices();
        String[] nonSearchableIndices = textCapabilities.nonSearchableIndices();
        String[] nonAggregatableIndices = textCapabilities.nonAggregatableIndices();//


    }

    public void RankingEvaluation() throws Exception{
        EvaluationMetric metric = new PrecisionAtK();
        List<RatedDocument> rateDocs = new ArrayList<>();
        rateDocs.add(new RatedDocument("posts", "1", 1));
        SearchSourceBuilder searchQuery = new SearchSourceBuilder();
        searchQuery.query(QueryBuilders.matchQuery("user", "kimchy"));
        RatedRequest ratedRequest = new RatedRequest("kimchy_query", rateDocs, searchQuery);
        List<RatedRequest> ratedRequests = Arrays.asList(ratedRequest);
        RankEvalSpec specification = new RankEvalSpec(ratedRequests, metric);
        RankEvalRequest request = new RankEvalRequest(specification, new String[]{ "posts" });

        //Synchronous Execution
        RankEvalResponse response = client.rankEval(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<RankEvalResponse> listener = new ActionListener<RankEvalResponse>() {
            @Override
            public void onResponse(RankEvalResponse response) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.rankEvalAsync(request, RequestOptions.DEFAULT, listener);

        double evaluationResult = response.getMetricScore();
        Map<String, EvalQueryQuality> partialResults =
                response.getPartialResults();
        EvalQueryQuality evalQuality =
                partialResults.get("kimchy_query");
        double qualityLevel = evalQuality.metricScore();
        List<RatedSearchHit> hitsAndRatings = evalQuality.getHitsAndRatings();
        RatedSearchHit ratedSearchHit = hitsAndRatings.get(0);
        MetricDetail metricDetails = evalQuality.getMetricDetails();
        String metricName = metricDetails.getMetricName();
        PrecisionAtK.Detail detail = (PrecisionAtK.Detail) metricDetails;
    }

    public void Explain() throws Exception{
        ExplainRequest request = new ExplainRequest("contributors", "doc", "1");
        request.query(QueryBuilders.termQuery("user", "tanguy"));

        //optional arguments
        request.routing("routing");
        request.preference("_local");
        request.fetchSourceContext(new FetchSourceContext(true, new String[]{"user"}, null));

        request.storedFields(new String[]{"user"});

        //Synchronous Execution
        ExplainResponse response = client.explain(request, RequestOptions.DEFAULT);

        //Asynchronous Execution
        ActionListener<ExplainResponse> listener = new ActionListener<ExplainResponse>() {
            @Override
            public void onResponse(ExplainResponse explainResponse) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        };
        client.explainAsync(request, RequestOptions.DEFAULT, listener);

        //ExplainResponse
        String index = response.getIndex();
        String type = response.getType();
        String id = response.getId();
        boolean exists = response.isExists();
        boolean match = response.isMatch();
        boolean hasExplanation = response.hasExplanation();
        Explanation explanation = response.getExplanation();
        GetResult getResult = response.getGetResult();

        Map<String, Object> source = getResult.getSource();
        Map<String, DocumentField> fields = getResult.getFields();
    }
    /*------------------------------------------------ search Api 多条件查询 end ----------------------------------------------*/
}
