package com.neu.monitor_sys.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-19
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permissions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 权限id
     */
    private Integer id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 父级权限id
     */
    private Integer parentId;

    /**
     * 对应接口
     */
    private String api;

    /**
     * 权限描述
     */
    private String permissionDescription;

    /**
     * 权限排序数
     */
    private Integer orderNum;

    /**
     * 是否启用，默认启用1，禁用为0
     */
    private Integer isEnabled;


    //不是数据库字段，用于存储子菜单
    @TableField(exist = false)
    private List<Permissions> childSysMenu;

    @Override
    public String toString() {
        return "Permissions{" +
                "id=" + id +
                ", permissionName='" + permissionName + '\'' +
                ", parentId=" + parentId +
                ", api='" + api + '\'' +
                ", permissionDescription='" + permissionDescription + '\'' +
                ", orderNum=" + orderNum +
                ", isEnabled=" + isEnabled +
                '}';
    }

     /**
     * 递归设置api为null
     */
    public void setApiToNullRecursively() {
        this.api = null;
        if (childSysMenu != null) {
            for (Permissions child : childSysMenu) {
                child.setApiToNullRecursively();
            }
        }
    }
}
