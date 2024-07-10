package com.neu.monitor_sys.statistics.repository;

import com.neu.monitor_sys.common.entity.StatisticsES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsStatisticsRepository extends ElasticsearchRepository<StatisticsES, Integer>{

}
