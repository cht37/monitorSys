package com.neu.monitorSys.aqi.controller;


import com.neu.monitorSys.aqi.service.IAqiService;
import com.neu.monitorSys.common.DTO.AqiDTO;
import com.neu.monitorSys.common.entity.Aqi;
import com.neu.monitorSys.common.DTO.MyResponse;
import com.neu.monitorSys.common.constants.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 空气质量指数级别表 前端控制器
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-24
 */
@RestController
@RequestMapping("/api/v1/aqi")
public class AqiController {
    @Autowired
    private IAqiService aqiService;

    /**
     * 查询全部aqi信息
     * @return MyResponse<List<Aqi>> 返回全部aqi信息
     */
    @RequestMapping("/example/all")
    public MyResponse<List<Aqi>> getAllAqi(){
         List<Aqi> aqiList;
        try {
            aqiList = aqiService.getAllAqi();
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", aqiList);

    }


    /**
     * 计算aqi
     * @param so2 二氧化硫浓度
     * @param co 一氧化碳浓度
     * @param spm 悬浮颗粒物浓度
     * @return MyResponse<int[]> 返回综合AQI值
     */
    @GetMapping("/calculate")
    public MyResponse<int[]> calculateAqi(@RequestParam int so2,@RequestParam  int co,@RequestParam int spm){
        int[] aqi;
        try {
            aqi = aqiService.calculateAqi(so2, co, spm);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", aqi);
    }

    /**
     * 获取aqi级别
     * @param value aqi值
     * @return MyResponse<AqiDTO> 返回aqi级别
     */
    @GetMapping("/level")
    public MyResponse<AqiDTO> getAqiLevel(@RequestParam int value){
        AqiDTO aqiDTO;
        try {
            aqiDTO = aqiService.getAqiLevel(value);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", aqiDTO);
    }

    /**
     * 根据污染物值获取aqi级别
     * @param SO2 so2值
     * @param CO  co值
     * @param SPM spm值
     * @return MyResponse<List<AqiDTO>> 返回aqi级别
     */
    @GetMapping("/level/list")
    public MyResponse<List<AqiDTO>> getAqiLevelByValue(@RequestParam int SO2,@RequestParam int CO,@RequestParam int SPM){
        List<AqiDTO> aqiDTO;
        try {
            aqiDTO = aqiService.getAqiLevelByValue(SO2, CO, SPM);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.NOT_FOUND.getCode(), e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", aqiDTO);
    }


    /**
     * 根据污染等级，获取污染物最低值
     * @param level 污染等级
     * @return MyResponse<int[]> 返回污染物最低值
     */
    @GetMapping("/min-value/{level}")
    public MyResponse<int[]> getMinValueByLevel(@PathVariable int level){
        int[] minValues;
        try {
            minValues = aqiService.getMinValueByLevel(level);
        } catch (Exception e) {
            return new MyResponse<>(ResultCode.FAILED.getCode(),"fail"+ e.getMessage(), null);
        }
        return new MyResponse<>(ResultCode.SUCCESS.getCode(), "success", minValues);
    }

}

