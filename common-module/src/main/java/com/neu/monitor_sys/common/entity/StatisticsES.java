package com.neu.monitor_sys.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Document(indexName = "statistics_es")
public class StatisticsES implements Serializable {

    @Id
    @Field(type = FieldType.Long, store = true)
    private Long id;

    @Field(type = FieldType.Long, name = "af_id")
    private Long afId;

    @Field(type = FieldType.Keyword, name = "province_id")
    private String provinceId;

    @Field(type = FieldType.Keyword, name = "city_id")
    private String cityId;

    @Field(type = FieldType.Keyword, name = "district_id")
    private String districtId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;

    @Field(type = FieldType.Long, name = "so2_value")
    private Long so2Value;

    @Field(type = FieldType.Long, name = "so2_aqi")
    private Long so2Aqi;

    @Field(type = FieldType.Long, name = "co_value")
    private Long coValue;

    @Field(type = FieldType.Long, name = "co_aqi")
    private Long coAqi;

    @Field(type = FieldType.Long, name = "spm_value")
    private Long spmValue;

    @Field(type = FieldType.Long, name = "spm_aqi")
    private Long spmAqi;

    @Field(type = FieldType.Long)
    private Long aqi;

    @Field(type = FieldType.Date, name = "confirm_datetime", format = DateFormat.date_hour_minute_second)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime confirmDatetime;

    @Field(type = FieldType.Keyword, name = "gm_id")
    private String gmId;

    @Field(type = FieldType.Keyword, name = "fd_tel")
    private String fdTel;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String information;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String remarks;
}
