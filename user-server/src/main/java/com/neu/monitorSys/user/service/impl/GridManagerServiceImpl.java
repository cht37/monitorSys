package com.neu.monitorSys.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.user.DTO.GeographyDTO;
import com.neu.monitorSys.user.DTO.GridManagerFullDTO;
import com.neu.monitorSys.user.DTO.MemberWithRole;
import com.neu.monitorSys.user.DTO.MyResponse;
import com.neu.monitorSys.user.client.GeoClient;
import com.neu.monitorSys.user.client.RoleClient;
import com.neu.monitorSys.user.constants.UserRedisPrefix;
import com.neu.monitorSys.user.entity.GridManager;
import com.neu.monitorSys.user.entity.Member;
import com.neu.monitorSys.user.entity.Roles;
import com.neu.monitorSys.user.mapper.GridManagerMapper;
import com.neu.monitorSys.user.mapper.MemberMapper;
import com.neu.monitorSys.user.service.IGridManagerService;
import com.neu.monitorSys.user.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 网格员表 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-04
 */
@Service
public class GridManagerServiceImpl extends ServiceImpl<GridManagerMapper, GridManager> implements IGridManagerService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private GridManagerMapper gridManagerMapper;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private GeoClient geoClient;
    @Autowired
    private RoleClient roleClient;
    /**
     * 1.根据网格员memberId(logId)获取网格员信息
     */
    @Override
    public GridManager getGridManagerByLogId(String logId) {
        //先在redis中获取网格员信息
        String gridManagerStr = StrUtil.toStringOrNull(redisUtil.get(UserRedisPrefix.GM_INFO_PREFIX + logId));
        if (gridManagerStr != null) {
            return JSONUtil.toBean(gridManagerStr, GridManager.class);
        }
        GridManager gridManager = lambdaQuery().eq(GridManager::getMemberId, logId).one();
        if (!ObjectUtil.isEmpty(gridManager)) {
            //去除网格员id
            gridManager.setGmId(null);
            //将网格员信息存入redis，有效期为5小时
            redisUtil.set(UserRedisPrefix.GM_INFO_PREFIX + logId, JSONUtil.toJsonStr(gridManager), 60 * 60 * 5);
            return gridManager;
        }
        return null;
    }

    /**
     * 多条件查询网格员信息
     */
    public IPage<GridManagerFullDTO> findGridManagersByConditions(GridManagerFullDTO gridManagerFullDTO, int page, int size) {
        //redis实现分页+多条件查询
        /*
          首先我们可以采用多条件模糊查询章节所说的方式，将我们所涉及到的条件字段作为hash的field，
          而数据的内容则作为对应value进行存储(一般以json格式存储，方便反序列化)。
          我们需要实现约定好查询的格式，用前面一节的例子来说，field中的命名规则为<id>:<姓名>:<性别>，
          我们每次可以通过"*"来实现我们希望的模糊匹配条件，比如“*：*：男”就是匹配所有男性数据，
          “100*：*：*”就是匹配所有id前缀为100的用户。当我们拿到了匹配串后我们先去Redis中寻找是否存在以该匹配串为key的ZSet，
          如果没有则通过Redis提供的HSCAN遍历所有hash的field，得到所有符合条件的field，并将其放入一个ZSet集合，同时将这个集合的key设置为我们的条件匹配串。
          如果已经存在了，则直接对这个ZSet进行分页查询即可。对ZSet进行分页的方式已经在前面叙述过了。通过这样的方式我们就实现了最简单的分页+多条件模糊查询。
         */
        //TODO 考虑使用es实现分页+多条件查询
        //多条件查询
        LambdaQueryWrapper<GridManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(GridManager::getMemberId, GridManager::getAreaId, GridManager::getState, GridManager::getRemark);
        if (gridManagerFullDTO.getMemberWithRole() != null) {
            queryWrapper.like(GridManager::getMemberId, gridManagerFullDTO.getMemberWithRole().getMember().getLogid());
        }
        queryWrapper.eq(gridManagerFullDTO.getAreaId() != null, GridManager::getAreaId, gridManagerFullDTO.getAreaId());
        queryWrapper.eq(gridManagerFullDTO.getRoleState() != null, GridManager::getState, gridManagerFullDTO.getRoleState());
        queryWrapper.eq(gridManagerFullDTO.getRemark() != null, GridManager::getRemark, gridManagerFullDTO.getRemark());
        // 创建分页对象
        Page<GridManager> gridManagerPage = new Page<>(page, size);

        // 执行分页查询
        IPage<GridManager> gridManagerIPage = gridManagerMapper.selectPage(gridManagerPage, queryWrapper);

        // 构建返回结果
        IPage<GridManagerFullDTO> resultPage = new Page<>(page, size, gridManagerIPage.getTotal());
        List<GridManagerFullDTO> gridManagerFullDTOList = gridManagerIPage.getRecords().stream().map(gridManager -> {
            GridManagerFullDTO dto = new GridManagerFullDTO();
            dto.setAreaId(gridManager.getAreaId());
            dto.setRoleState(gridManager.getState());
            dto.setRemark(gridManager.getRemark());
            //向地理服务获取网格员的地理信息
            Object data = geoClient.getGeoInfo(gridManager.getAreaId()).getData();
            GeographyDTO bean = BeanUtil.toBean(data, GeographyDTO.class);
            dto.setAreaName(bean.getAreaName());
            dto.setCityName(bean.getCityName());
            dto.setProvinceName(bean.getProvinceName());
            Member member = memberMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getLogid, gridManager.getMemberId()));
            member.setId(null);
            //向角色服务获取网格员的角色信息
            Object roleData = roleClient.getRoleById(member.getRoleid()).getData();
            dto.setMemberWithRole(new MemberWithRole(member, BeanUtil.toBean(roleData, Roles.class)));
            return dto;
        }).toList();

        resultPage.setRecords(gridManagerFullDTOList);


        return resultPage;
    }

    /**
     * 更新网格员信息
     * @param gridManager
     * @return
     */
    @Override
    public boolean updateGridManager(GridManager gridManager) {
        //去除网格员id
        gridManager.setGmId(null);
        // 更新数据库中的网格员信息
        UpdateWrapper<GridManager> gridManagerQueryWrapper = new UpdateWrapper<>();
        gridManagerQueryWrapper.eq("member_id", gridManager.getMemberId());
        //根据业务可知，网格员在指派结束或者未指派时，区域id可以为空
        if (gridManager.getAreaId() == null) {
            gridManagerQueryWrapper.set("area_id", null);
        }
        boolean result = update(gridManager, gridManagerQueryWrapper);
        if (result) {
            // 删除redis中的网格员信息
            redisUtil.del(UserRedisPrefix.GM_INFO_PREFIX + gridManager.getMemberId());
        }
        return result;
    }
}
