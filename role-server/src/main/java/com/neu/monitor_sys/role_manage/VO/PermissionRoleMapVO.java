package com.neu.monitor_sys.role_manage.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PermissionRoleMapVO implements Serializable {
    private String permissionUrl;
    private List<String> roleNames;
}