//package org.starcoin.starswap.api.service;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.novi.serde.DeserializationError;
//import org.elasticsearch.action.search.*;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.unit.TimeValue;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.Scroll;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.starcoin.base.AccountAddress;
//import org.starcoin.base.VoteChangedEvent;
//import org.starcoin.starswap.api.bean.Event;
//import org.starcoin.starswap.api.constant.Constant;
//import org.starcoin.utils.CommonUtils;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class TransactionService {
//
//    private final RestHighLevelClient client;
//
//    @Autowired
//    public TransactionService(RestHighLevelClient client) {
//        this.client = client;
//    }
//
//    public List<JSONObject> getEventsByProposalIdAndProposer(String network, Long proposalId, String proposer) throws IOException, DeserializationError {
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//        boolQuery.must(QueryBuilders.matchQuery("tag_name", "VoteChangedEvent"));
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchSourceBuilder.size(50);
//
//        SearchRequest searchRequest = new SearchRequest(ServiceUtils.getIndex(network, Constant.TRANSACTION_EVENT_INDEX));
//        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10L));
//        searchRequest.scroll(scroll).source(searchSourceBuilder);
//        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
//
//        List<JSONObject> result = new ArrayList<>();
//        String scrollId = searchResponse.getScrollId();
//        SearchHit[] searchHits = searchResponse.getHits().getHits();
//        while (searchHits != null && searchHits.length > 0) {
//            List<JSONObject> tmp = getSearchResultFilter(searchResponse, proposalId, proposer);
//            if (tmp.size() > 0) {
//                result.addAll(tmp);
//            }
//            SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
//            scrollRequest.scroll(scroll);
//            searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
//            scrollId = searchResponse.getScrollId();
//            searchHits = searchResponse.getHits().getHits();
//        }
//
//        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
//        clearScrollRequest.addScrollId(scrollId);
//        ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
//        clearScrollResponse.isSucceeded();
//        return result;
//    }
//
//    private List<JSONObject> getSearchResultFilter(SearchResponse searchResponse, Long proposalId, String proposerStr) throws DeserializationError {
//        SearchHit[] searchHit = searchResponse.getHits().getHits();
//        List<JSONObject> transactions = new ArrayList<>();
//        for (SearchHit hit : searchHit) {
//            Event event = JSON.parseObject(hit.getSourceAsString(), Event.class);
//
//            byte[] voteBytes = CommonUtils.hexToByteArray(event.getData());
//            VoteChangedEvent data = VoteChangedEvent.bcsDeserialize(voteBytes);
//
//            byte[] proposerBytes = CommonUtils.hexToByteArray(proposerStr);
//            AccountAddress proposer = AccountAddress.bcsDeserialize(proposerBytes);
//            if (!data.proposal_id.equals(proposalId) || !data.proposer.equals(proposer)) {
//                continue;
//            }
//            JSONObject item = new JSONObject();
//            item.put("event", event);
//            item.put("voteChangedEvent", data);
//            item.put("proposer", proposer);
//            transactions.add(item);
//        }
//        return transactions;
//    }
//}
