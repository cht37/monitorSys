package com.neu.monitor_sys.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 空气质量指数级别表
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-24
 */
@Getter
@Setter
  public class Aqi implements Serializable {

    private static final long serialVersionUID = 1L;

      /**
     * 空气质量指数类别（共六级）
     */
        @TableId(value = "aqi_id", type = IdType.AUTO)
      private Integer aqiId;

      /**
     * 空气质量指数类别中文描述
     */
      private String chineseExplain;

      /**
     * 空气质量指数类别描述
     */
      private String aqiExplain;

      /**
     * 空气质量指数类别表示颜色
     */
      private String color;

      /**
     * 对健康的影响
     */
      private String healthImpact;

      /**
     * 建议采取的措施
     */
      private String takeSteps;

      /**
     * 本级别二氧化硫浓度最小限制值
     */
      private Integer so2Min;

      /**
     * 本级别二氧化硫浓度最大限制值
     */
      private Integer so2Max;

      /**
     * 本级别一氧化碳浓度最小限制值
     */
      private Integer coMin;

      /**
     * 本级别一氧化碳浓度最大限制值
     */
      private Integer coMax;

      /**
     * 本级别悬浮颗粒物浓度最小限制值
     */
      private Integer spmMin;

      /**
     * 本级别悬浮颗粒物浓度最大限制值
     */
      private Integer spmMax;

      /**
     * 备注
     */
      private String remarks;


}
