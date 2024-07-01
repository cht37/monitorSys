package com.neu.monitorSys.user.DTO;

import lombok.Data;

@Data
public class GridManagerDTO {
    /**
     *  网格员id
     */
    private String logId;
    /**
     * 网格id
     */
    private String address;
    /**
     *反馈id
     */
    private Integer afId;


    /**
     * 状态	0：可工作状态	1：临时抽调	2：休假	3：其他
     */
    private Integer roleState;

}
