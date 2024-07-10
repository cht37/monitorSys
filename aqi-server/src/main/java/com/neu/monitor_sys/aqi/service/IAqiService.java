package com.neu.monitor_sys.aqi.service;

import com.neu.monitor_sys.common.DTO.AqiDTO;
import com.neu.monitor_sys.common.entity.Aqi;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 空气质量指数级别表 服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-24
 */
public interface IAqiService extends IService<Aqi> {
        //查询全部aqi信息
        List<Aqi> getAllAqi();

        //计算aqi
        int[] calculateAqi(int so2,int co,int spm);

        /**
         * 根据aqi获取aqi级别
         */
        AqiDTO getAqiLevel(int value);

        /**
         * 根据污染物值获取aqi级别
         * @param value 污染物值
         * @param type 污染物类型
         * @return aqi级别
         */
        List<AqiDTO> getAqiLevelByValue(int SO2,int CO,int SPM);


        /**
         * 根据污染等级，获取污染物最低值
         * @param level 污染等级
         * @return 污染物最低值
         */
        int[] getMinValueByLevel(int level);
}
