package com.neu.monitorSys.user.DTO;

import com.neu.monitorSys.user.entity.Member;
import lombok.Data;

@Data
public class GridManagerFullDTO {
    private MemberWithRole memberWithRole;
    private Integer areaId;
    private String provinceName;
    private String cityName;
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
