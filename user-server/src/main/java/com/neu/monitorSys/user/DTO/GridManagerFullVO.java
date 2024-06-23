package com.neu.monitorSys.user.DTO;

import com.neu.monitorSys.entity.DTO.MemberWithRole;
import com.neu.monitorSys.entity.Member;
import lombok.Data;

import java.sql.Date;

@Data
public class GridManagerFullVO {
    private Member member;
    private Integer areaId;
    private Integer afId;
    //省
    private String provinceName;
    //市
    private String cityName;
    //区
    private String districtName;
    //区域
    private String areaName;

    /**
     * 状态	0：可工作状态	1：临时抽调	2：休假	3：其他
     */
    private Integer roleState;

    /**
     * 网格员个人信息
     */
    private String remark;

}
