package com.neu.monitorSys.roleManage.VO;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PermissionRoleMapVO implements Serializable {
    private String permissionUrl;
    private List<String> roleNames;
}