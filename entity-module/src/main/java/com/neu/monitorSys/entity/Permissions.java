package com.neu.monitorSys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
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
}
