package com.neu.monitor_sys.statistics.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

@Configuration
public class ElasticsearchConfig {

   @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
            .connectedTo("192.168.244.128:9200")
            .build();
        return RestClients.create(clientConfiguration).rest();
    }
    @Bean(name = { "elasticsearchTemplate", "elasticsearchRestTemplate" })
    public ElasticsearchRestTemplate elasticsearchRestTemplate(RestHighLevelClient client, ElasticsearchConverter converter) {
        return new ElasticsearchRestTemplate(client, converter);
    }
     @Bean
    public ElasticsearchConverter elasticsearchConverter() {
        SimpleElasticsearchMappingContext mappingContext = new SimpleElasticsearchMappingContext();
        return new MappingElasticsearchConverter(mappingContext);
    }
}
