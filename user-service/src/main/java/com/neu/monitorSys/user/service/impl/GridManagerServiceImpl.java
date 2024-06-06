package com.neu.monitorSys.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitorSys.user.DTO.GridManagerDTO;
import com.neu.monitorSys.user.constants.UserRedisPrefix;
import com.neu.monitorSys.user.entity.GridManager;
import com.neu.monitorSys.user.entity.Member;
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

    /**
     * 1.根据网格员memberId(logId)获取网格员信息
     */
    @Override
    public GridManager getGridManagerByLogId(String logId){
        //先在redis中获取网格员信息
        String gridManagerStr = StrUtil.toStringOrNull(redisUtil.get(UserRedisPrefix.GM_INFO_PREFIX +logId));
        if (gridManagerStr != null) {
            return JSONUtil.toBean(gridManagerStr, GridManager.class);
        }
        GridManager gridManager = lambdaQuery().eq(GridManager::getMemberId, logId).one();
        if (!ObjectUtil.isEmpty(gridManager)) {
            //去除网格员id
            gridManager.setGmId(null);
            //将网格员信息存入redis，有效期为5小时
            redisUtil.set(UserRedisPrefix.GM_INFO_PREFIX+logId, JSONUtil.toJsonStr(gridManager), 60 * 60 * 5);
            return gridManager;
        }
        return null;
    }

    /**
     * 多条件查询网格员信息
     */
    public IPage<GridManagerDTO> findGridManagersByConditions(GridManagerDTO gridManagerDTO, int page, int size) {
        //多条件查询
        LambdaQueryWrapper<GridManager> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(GridManager::getMemberId, GridManager::getAreaId, GridManager::getState, GridManager::getRemark);
        queryWrapper.eq(GridManager::getMemberId, gridManagerDTO.getMember().getLogid());
        queryWrapper.eq(gridManagerDTO.getAreaId() != null, GridManager::getAreaId, gridManagerDTO.getAreaId());
        queryWrapper.eq(gridManagerDTO.getRoleState() != null, GridManager::getState, gridManagerDTO.getRoleState());
        queryWrapper.eq(gridManagerDTO.getRemark() != null, GridManager::getRemark, gridManagerDTO.getRemark());
        // 创建分页对象
        Page<GridManager> gridManagerPage = new Page<>(page, size);

        // 执行分页查询
        IPage<GridManager> gridManagerIPage = gridManagerMapper.selectPage(gridManagerPage, queryWrapper);

        // 构建返回结果
        IPage<GridManagerDTO> resultPage = new Page<>(page, size, gridManagerIPage.getTotal());
        List<GridManagerDTO> gridManagerDTOList = gridManagerIPage.getRecords().stream().map(gridManager -> {
            GridManagerDTO dto = new GridManagerDTO();
            dto.setAreaId(gridManager.getAreaId());
            dto.setRoleState(gridManager.getState());
            dto.setRemark(gridManager.getRemark());

            Member member = memberMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getLogid, gridManager.getMemberId()));
            dto.setMember(member);

            return dto;
        }).toList();

        resultPage.setRecords(gridManagerDTOList);
        //Redis实现分页+多条件模糊查询


        return resultPage;
    }

}
