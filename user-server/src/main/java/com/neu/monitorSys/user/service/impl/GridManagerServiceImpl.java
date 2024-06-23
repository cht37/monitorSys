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
import com.neu.monitorSys.entity.AqiFeedback;
import com.neu.monitorSys.entity.GridManager;
import com.neu.monitorSys.entity.Member;
import com.neu.monitorSys.user.DTO.AssignDTO;
import com.neu.monitorSys.user.DTO.GeographyDTO;
import com.neu.monitorSys.user.DTO.GridManagerFullVO;
import com.neu.monitorSys.user.client.FeedbackClient;
import com.neu.monitorSys.user.client.GeoClient;
import com.neu.monitorSys.user.constants.UserRedisPrefix;
import com.neu.monitorSys.user.mapper.GridManagerMapper;
import com.neu.monitorSys.user.mapper.MemberMapper;
import com.neu.monitorSys.user.service.IGridManagerService;
import com.neu.monitorSys.user.util.RedisUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
//    @Autowired
//    private RoleClient roleClient;
    @Autowired
    private FeedbackClient feedbackClient;

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
    public IPage<GridManagerFullVO> findGridManagersByConditions(GridManagerFullVO gridManagerFullVO, int page, int size) {
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
        if (gridManagerFullVO.getMember() != null) {
            queryWrapper.like(GridManager::getMemberId, gridManagerFullVO.getMember().getLogid());
        }
        queryWrapper.eq(gridManagerFullVO.getAreaId() != null, GridManager::getAreaId, gridManagerFullVO.getAreaId());
        queryWrapper.eq(gridManagerFullVO.getRoleState() != null, GridManager::getState, gridManagerFullVO.getRoleState());
        queryWrapper.eq(gridManagerFullVO.getAfId() != null, GridManager::getAfId, gridManagerFullVO.getAfId());
        queryWrapper.eq(gridManagerFullVO.getRemark() != null, GridManager::getRemark, gridManagerFullVO.getRemark());
        // 创建分页对象
        Page<GridManager> gridManagerPage = new Page<>(page, size);

        // 执行分页查询
        IPage<GridManager> gridManagerIPage = gridManagerMapper.selectPage(gridManagerPage, queryWrapper);

        // 构建返回结果
        IPage<GridManagerFullVO> resultPage = new Page<>(page, size, gridManagerIPage.getTotal());
        List<GridManagerFullVO> gridManagerFullVOList = gridManagerIPage.getRecords().stream().map(gridManager -> {
            GridManagerFullVO dto = new GridManagerFullVO();
            dto.setAreaId(gridManager.getAreaId());
            dto.setRoleState(gridManager.getState());
            dto.setRemark(gridManager.getRemark());
            Member member = memberMapper.selectOne(new LambdaQueryWrapper<Member>().eq(Member::getLogid, gridManager.getMemberId()));
            member.setId(null);
            //向角色服务获取网格员的角色信息
            dto.setMember(member);
            //向地理服务获取网格员的地理信息
            if (gridManager.getState() == 0 || gridManager.getAreaId() == null) {
                return dto;
            }
            Object data = geoClient.getGeoInfo(gridManager.getAreaId()).getData();
            GeographyDTO bean = BeanUtil.toBean(data, GeographyDTO.class);
            dto.setAreaName(bean.getAreaName());
            dto.setDistrictName(bean.getDistrictName());
            dto.setCityName(bean.getCityName());
            dto.setProvinceName(bean.getProvinceName());

            //TODO 封装角色信息
//            Object roleData = roleClient.getRoleById(member.getRoleid()).getData();
//            dto.setMemberWithRole(new MemberWithRole(member, BeanUtil.toBean(roleData, Roles.class)));
            return dto;
        }).toList();

        resultPage.setRecords(gridManagerFullVOList);
        resultPage.setTotal(gridManagerIPage.getTotal());
        resultPage.setPages(gridManagerIPage.getPages());

        return resultPage;
    }

    /**
     * 更新网格员信息
     *
     * @param gridManager 网格员信息
     * @return 是否更新成功
     */
    @Override
    @Transactional
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

    @Override
    public boolean isAssign(String logId) {
        GridManager gridManager = getGridManagerByLogId(logId);
        //网格员状态不为1或2或3时（休假或其他状态），可以指派
        return gridManager != null && gridManager.getState() != 2 && gridManager.getState() != 3;
    }

    @Override
    public boolean isAssign(String logId, Integer afId) {
        GridManager gridManager = getGridManagerByLogId(logId);
        //网格员状态不为2或3时（休假或其他状态），可以指派
        if (gridManager.getState() == null || gridManager.getState() == 2 || gridManager.getState() == 3) {
            throw new RuntimeException("网格员正在休假或其他状态，不可指派任务");
        }
        if (Objects.equals(gridManager.getAfId(), afId)) {
            throw new RuntimeException("该反馈已指派给您");
        }
        return true;
    }

    /**
     * 网格员接受对某网格的指派
     *
     * @param assignDTO 网格员接受指派信息
     * @param logId    网格员id
     * @return 是否接受成功
     */
    @Override
    @Transactional
    public boolean acceptAssign(AssignDTO assignDTO, String logId) {
        //首先判断网格员是否可指派
        try {
            isAssign(logId,assignDTO.getAfId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        //判断反馈是否指派给自己
        Object data = feedbackClient.findFeedbackById(assignDTO.getAfId()).getData();
        if (data == null) {
            throw new RuntimeException("该反馈不存在");
        }
        AqiFeedback feedback = BeanUtil.toBean(data, AqiFeedback.class);
        String gmId = feedback.getGmId();
        if (gmId != null && !gmId.equals(logId)) {
            throw new RuntimeException("该反馈未指派给您");
        }
        //判断该反馈是否已经处理
        if (feedback.getState() == 0) {
            throw new RuntimeException("反馈信息异常，请联系管理员");
        }
        if (feedback.getState() == 2) {
            throw new RuntimeException("该反馈已处理，请勿提交处理申请");
        }
        Integer gridId = (Integer) geoClient.getGridIdByGridName(feedback.getAddress()).getData();
        if (gridId == null) {
            throw new RuntimeException("网格不存在");
        }
        GridManager gridManager = new GridManager();
        gridManager.setAfId(assignDTO.getAfId());
        gridManager.setAreaId(gridId);
        gridManager.setMemberId(logId);
        gridManager.setRemark(assignDTO.getRemark());
        //设置网格员状态为1（临时调用）
        gridManager.setState(1);
        // 通过AopContext.currentProxy()获取代理对象，调用updateGridManager方法，解决事务失效问题
        GridManagerServiceImpl gridManagerService = (GridManagerServiceImpl) AopContext.currentProxy();
        return gridManagerService.updateGridManager(gridManager);
    }
}
