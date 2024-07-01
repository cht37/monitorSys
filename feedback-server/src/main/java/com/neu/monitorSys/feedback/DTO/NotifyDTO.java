package com.neu.monitorSys.feedback.DTO;

import lombok.Data;

@Data
public class NotifyDTO {
    //是否需要刷新
    private Boolean requireRefresh;
    //通知内容
    private String msg;
}
