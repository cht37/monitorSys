package com.neu.monitor_sys.common.DTO;

public class LogoutMsg {
    private String logId;
    private String message;

    public LogoutMsg(String logId, String message) {
        this.logId = logId;
        this.message = message;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
