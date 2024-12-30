package test.soal2.employeeMetric.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.HistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.min.Min;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeeService {

    private final RestHighLevelClient client;

    public EmployeeService(RestHighLevelClient client) {
        this.client = client;
    }

        public long getCountOfEmployees() throws IOException {
        SearchRequest searchRequest = new SearchRequest("companydatabase");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits().getTotalHits();
    }

    public Map<String, Object> getAverageSalary() throws IOException {
        SearchRequest searchRequest = new SearchRequest("companydatabase");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.avg("average_salary").field("Salary"));
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Avg avg = response.getAggregations().get("average_salary");
        return Map.of("average_salary", avg.getValue());
    }

    public Map<String, Object> getMinMaxSalary() throws IOException {
        SearchRequest searchRequest = new SearchRequest("companydatabase");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.min("min_salary").field("Salary"));
        sourceBuilder.aggregation(AggregationBuilders.max("max_salary").field("Salary"));
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        Min min = response.getAggregations().get("min_salary");
        Max max = response.getAggregations().get("max_salary");

        Map<String, Object> result = new HashMap<>();
        result.put("min_salary", min.getValue());
        result.put("max_salary", max.getValue());
        return result;
    }

    public Map<String, Long> getGenderDistribution() throws IOException {
        return getTermsAggregation("gender_distribution", "Gender.keyword");
    }

    public Map<String, Long> getMaritalStatusDistribution() throws IOException {
        return getTermsAggregation("marital_status_distribution", "MaritalStatus.keyword");
    }

    public Map<String, Long> getDesignationDistribution() throws IOException {
        return getTermsAggregation("designation_distribution", "Designation.keyword");
    }

    public Map<String, Object> getAgeDistribution() throws IOException {
        HistogramAggregationBuilder aggregation = AggregationBuilders
                .histogram("age_distribution")
                .field("Age")
                .interval(5); // Interval untuk usia (kelompok per 5 tahun)

        return getHistogramAggregation("age_distribution", aggregation);
    }

    public Map<String, Object> getDateOfJoiningHistogram() throws IOException {
        DateHistogramAggregationBuilder aggregation = AggregationBuilders
                .dateHistogram("date_of_joining_histogram")
                .field("DateOfJoining")
                .interval(365 * 24 * 60 * 60 * 1000L)// Interval tahunan dalam milidetik
                .minDocCount(1);

        return getDateHistogramAggregation("date_of_joining_histogram", aggregation);
    }

//    public Map<String, Long> getTopInterests() throws IOException {
//        return getTermsAggregation("top_interests", "Interests.keyword");
//
//    }

    public Map<String, Long> getTopInterests() throws IOException {
        SearchRequest searchRequest = new SearchRequest("companydatabase");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.terms("top_interests")
                .field("Interests.keyword")
                .order(BucketOrder.count(false))
                .size(10)); // Top 10 interests
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("top_interests");

        Map<String, Long> result = new HashMap<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return result;
    }

    private Map<String, Long> getTermsAggregation(String aggregationName, String fieldName) throws IOException {
        SearchRequest searchRequest = new SearchRequest("companydatabase");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(AggregationBuilders.terms(aggregationName).field(fieldName));
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get(aggregationName);

        Map<String, Long> result = new HashMap<>();
        for (Terms.Bucket bucket : terms.getBuckets()) {
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return result;
    }

    private Map<String, Object> getHistogramAggregation(String aggregationName, HistogramAggregationBuilder aggregation) throws IOException {
        SearchRequest searchRequest = new SearchRequest("companydatabase");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(aggregation);
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Histogram histogram = response.getAggregations().get(aggregationName);

        Map<String, Object> result = new HashMap<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            long lowerBound = ((Double) bucket.getKey()).longValue();
            long upperBound = lowerBound + 5;
            String range = lowerBound + "-" + upperBound;
            result.put(range, bucket.getDocCount());
        }
        return result;
    }

    private Map<String, Object> getDateHistogramAggregation(String aggregationName, DateHistogramAggregationBuilder aggregation) throws IOException {
        SearchRequest searchRequest = new SearchRequest("companydatabase");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.aggregation(aggregation);
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Histogram histogram = response.getAggregations().get(aggregationName);

        Map<String, Object> result = new HashMap<>();
        for (Histogram.Bucket bucket : histogram.getBuckets()) {
            String fullDate = bucket.getKeyAsString();
            String year = fullDate.split("-")[0];
            result.put(year, bucket.getDocCount());
        }
        return result;
    }

}