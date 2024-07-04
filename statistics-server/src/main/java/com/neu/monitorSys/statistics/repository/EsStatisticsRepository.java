package com.neu.monitorSys.statistics.repository;

import com.neu.monitorSys.common.entity.StatisticsES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsStatisticsRepository extends ElasticsearchRepository<StatisticsES, Integer>{

}
