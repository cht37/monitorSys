package com.neu.monitorSys.statistics.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "aqi_statistics")
public class AqiStatisticsPercent {
    @Id
    @Field(type = FieldType.Long,name = "aqi_id")
    private Long aqiId;

    @Field(type = FieldType.Long,name = "aqi_count")
    private Long aqiCount;

    @Field(type = FieldType.Float,name = "aqi_percentage")
    private Float aqiPercentage;

    @Field(type = FieldType.Text,name = "chinese_explain", analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String chineseExplain;

}
