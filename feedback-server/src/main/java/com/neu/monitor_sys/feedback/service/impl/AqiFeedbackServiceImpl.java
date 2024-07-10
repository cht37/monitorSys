package com.neu.monitor_sys.feedback.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitor_sys.common.entity.AqiFeedback;
import com.neu.monitor_sys.feedback.DTO.*;
import com.neu.monitor_sys.feedback.client.GeoClient;
import com.neu.monitor_sys.feedback.client.UserClient;
import com.neu.monitor_sys.feedback.mapper.AqiFeedbackMapper;
import com.neu.monitor_sys.feedback.publisher.FeedbackPublisher;
import com.neu.monitor_sys.feedback.service.IAqiFeedbackService;
import com.neu.monitor_sys.feedback.util.RedisUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Time;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-11
 */
@Service
@Log4j2
public class AqiFeedbackServiceImpl extends ServiceImpl<AqiFeedbackMapper, AqiFeedback> implements IAqiFeedbackService {

    @Autowired
    private AqiFeedbackMapper aqiFeedbackMapper;
    @Autowired
    private GeoClient geoClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private FeedbackPublisher feedbackPublisher;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AqiFeedbackRepository aqiFeedbackRepository;

    /**
     * 保存反馈信息
     */
    @Override
    @Transactional
    public Boolean saveFeedback(AqiFeedbackDTO aqiFeedbackDTO) {
        //处理数据
        AqiFeedback aqiFeedback = new AqiFeedback();
        aqiFeedback.setAddress(aqiFeedbackDTO.getAddress());
        //获取省份编号
        String provinceId = (String) geoClient.getProvinceId(aqiFeedbackDTO.getProvinceName()).getData();
        if (provinceId == null || provinceId.equals("")) {
            return false;
        }
        //获取城市编号
        String cityId = (String) geoClient.getCityIdByProvinceId(aqiFeedbackDTO.getCityName(), provinceId).getData();
        if (cityId == null || cityId.equals("")) {
            return false;
        }
        //获取区域编号
        String districtId = (String) geoClient.getDistrictId(aqiFeedbackDTO.getDistrictName(), cityId).getData();
        if (districtId == null || districtId.equals("")) {
            return false;
        }
        aqiFeedback.setCityId(cityId);
        aqiFeedback.setProvinceId(provinceId);
        aqiFeedback.setDistrictId(districtId);
        try {
            //日期字符串转Date
            aqiFeedback.setAfDate(Date.valueOf(aqiFeedbackDTO.getAfDate()));
            //时间字符串转Time
            aqiFeedback.setAfTime(Time.valueOf(aqiFeedbackDTO.getAfTime()));
        } catch (Exception e) {
            throw new DateTimeParseException("日期或时间格式错误", aqiFeedbackDTO.getAfDate(), 0);
        }
        BeanUtil.copyProperties(aqiFeedbackDTO, aqiFeedback);
        //写入数据库
        int i = aqiFeedbackMapper.insert(aqiFeedback);
        //判断是否是前500条记录，如果是则写入缓存，否则不写入
        if (i == 1) {
            if (aqiFeedbackMapper.selectCount(null) <= 500) {
                aqiFeedbackRepository.saveFeedback(aqiFeedback);
            }
        }
        return i == 1;
    }

    /**
     * 异步提交反馈信息
     *
     * @param aqiFeedbackDTO 反馈信息
     * @return 是否成功
     */
    @Override
    // TODO 分布式事务 异步情况
    public Boolean submitFeedback(AqiFeedbackDTO aqiFeedbackDTO) {
        //获取当前时间
        long current = DateUtil.current();
        //赋值
        aqiFeedbackDTO.setAfDate(DateUtil.format(new Date(current), "yyyy-MM-dd"));
        aqiFeedbackDTO.setAfTime(DateUtil.format(new Date(current), "HH:mm:ss"));
        //判断省市区名称是否为空，telId是否为空
        if (aqiFeedbackDTO.getProvinceName() == null || aqiFeedbackDTO.getCityName() == null || aqiFeedbackDTO.getDistrictName() == null || aqiFeedbackDTO.getTelId() == null) {
            return false;
        }
        //提交到消息队列
        return feedbackPublisher.sendFeedback(aqiFeedbackDTO);
    }


    /**
     * 根据公众监督员telId查询反馈记录，默认逆序
     */
    @Override
    public IPage<AqiFeedBackVO> getFeedbackByTelId(String telId, int page, int size) {
        List<AqiFeedback> feedbackList = null;
        long total = 0;
        if (page * size < 500) {
            // 分页通过 telId 查询反馈记录
            feedbackList = aqiFeedbackRepository.findFeedbackByPage(telId, page, size);
            total = aqiFeedbackRepository.countFeedback(telId);

            if (feedbackList == null || feedbackList.isEmpty()) {
                return new Page<>(page, size, total);
            }
            //逆序
            feedbackList.sort((o1, o2) -> o2.getAfId().compareTo(o1.getAfId()));
        } else {
            // 数据量过大，从数据库查询
            IPage<AqiFeedback> feedbackPage = getFeedbackByTelIdDatabase(telId, page, size);
            feedbackList = feedbackPage.getRecords();
            total = feedbackPage.getTotal();
        }
//        LambdaQueryWrapper<AqiFeedback> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(AqiFeedback::getTelId, telId);
//        Page<AqiFeedback> aqiFeedbackPage = new Page<>(page, size);
//        IPage<AqiFeedback> aqiFeedbackList = aqiFeedbackMapper.selectPage(aqiFeedbackPage, wrapper);

        // 使用 CompletableFuture 并行处理每个反馈记录
        List<AqiFeedBackVO> feedbackFullDTOList = getAqiFeedBackVOS(feedbackList);

        // 构造并返回包含完整 DTO 数据的分页结果
//        IPage<AqiFeedBackDTO> feedbackFullDTOPage = new Page<>(page, size);
//        feedbackFullDTOPage.setRecords(feedbackFullDTOList);
//        feedbackFullDTOPage.setTotal(aqiFeedbackList.getTotal());
//        feedbackFullDTOPage.setCurrent(aqiFeedbackList.getCurrent());
//        feedbackFullDTOPage.setSize(aqiFeedbackList.getSize());
        // 构造并返回包含完整DTO数据的分页结果
        IPage<AqiFeedBackVO> feedbackDTOPage = new Page<>(page, size, total);
        feedbackDTOPage.setTotal(total);
        feedbackDTOPage.setRecords(feedbackFullDTOList);
        return feedbackDTOPage;
    }

    @Override
    public IPage<AqiFeedBackVO> getFeedbackByGridId(String gridId, Integer isFinished, int page, int size) {
        // 分页通过 gridId 查询反馈记录
        LambdaQueryWrapper<AqiFeedback> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<AqiFeedback> handleWrapper;
        AqiFeedback firstFeedback = null;
        if (isFinished != null) {
            if (isFinished == 0) {
                wrapper.eq(AqiFeedback::getState, 1);
                handleWrapper = new LambdaQueryWrapper<>();
                handleWrapper.eq(AqiFeedback::getState, 2);
                handleWrapper.eq(AqiFeedback::getGmId, gridId);
                firstFeedback = aqiFeedbackMapper.selectOne(handleWrapper);
            } else if (isFinished == 1) {
                wrapper.eq(AqiFeedback::getState, 3);
            }
        }
        wrapper.eq(AqiFeedback::getGmId, gridId);
        //assignDate由大到小排序
        wrapper.orderByDesc(AqiFeedback::getAssignDate);
        Page<AqiFeedback> aqiFeedbackPage = new Page<>(page, size);
        IPage<AqiFeedback> aqiFeedbackList = aqiFeedbackMapper.selectPage(aqiFeedbackPage, wrapper);
        //如果有正在处理的反馈记录，将其放在第一位
        if (firstFeedback != null) {
            List<AqiFeedback> records = aqiFeedbackList.getRecords();
            if (records.isEmpty()) {
                records = new ArrayList<>();
                records.add(firstFeedback);
            } else {
                records.add(0, firstFeedback);
            }
            aqiFeedbackList.setRecords(records);
        }
        // 使用 CompletableFuture 并行处理每个反馈记录
        List<AqiFeedBackVO> feedbackFullDTOList = getAqiFeedBackVOS(aqiFeedbackList.getRecords());
        // 构造并返回包含完整DTO数据的分页结果
        IPage<AqiFeedBackVO> feedbackDTOPage = new Page<>(page, size);
        feedbackDTOPage.setRecords(feedbackFullDTOList);
        feedbackDTOPage.setTotal(aqiFeedbackList.getTotal());
        return feedbackDTOPage;
    }

    /**
     * 异步处理feedbackList，生成VO
     *
     * @param feedbackList
     * @return
     */
    private List<AqiFeedBackVO> getAqiFeedBackVOS(List<AqiFeedback> feedbackList) {
        List<CompletableFuture<AqiFeedBackVO>> futures = feedbackList.stream()
                .map(aqiFeedback -> CompletableFuture.supplyAsync(() -> {
                            // 将 AqiFeedback 实体转换为 AqiFeedBackFullDTO DTO
                            AqiFeedBackVO aqiFeedBackVO = new AqiFeedBackVO();
                            BeanUtils.copyProperties(aqiFeedback, aqiFeedBackVO);
                            return aqiFeedBackVO;
                        }).thenCombine(
                                // 异步获取城市名称
                                CompletableFuture.supplyAsync(() -> geoClient.getCityName(aqiFeedback.getCityId()).getData()),
                                (aqiFeedBackVO, cityName) -> {
                                    aqiFeedBackVO.setCityName((String) cityName);
                                    return aqiFeedBackVO;
                                }
                        ).thenCombine(
                                // 异步获取省份名称
                                CompletableFuture.supplyAsync(() -> geoClient.getProvinceName(aqiFeedback.getProvinceId()).getData()),
                                (aqiFeedBackVO, provinceName) -> {
                                    aqiFeedBackVO.setProvinceName((String) provinceName);
                                    return aqiFeedBackVO;
                                }
                        ).thenCombine(
                                // 异步获取区名称
                                CompletableFuture.supplyAsync(() -> geoClient.getDistrictName(aqiFeedback.getDistrictId()).getData()),
                                (aqiFeedBackVO, districtName) -> {
                                    aqiFeedBackVO.setDistrictName((String) districtName);
                                    return aqiFeedBackVO;
                                }
                        ).thenCombine(

                                // aqiFeedback.getGmId()不为空或“”异步获取公众监督员姓名
                                aqiFeedback.getGmId() == null || aqiFeedback.getGmId().equals("") ?
                                        CompletableFuture.completedFuture("") :
                                        CompletableFuture.supplyAsync(() -> userClient.getName(aqiFeedback.getGmId()).getData()),
                                (aqiFeedBackVO, gmName) -> {
                                    aqiFeedBackVO.setGmName((String) gmName);
                                    return aqiFeedBackVO;
                                }

                        )

                ).toList();

        // 将所有 CompletableFuture 的结果收集到一个列表中
        List<AqiFeedBackVO> feedbackFullDTOList = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return feedbackFullDTOList;
    }

    /**
     * 设置反馈网格员（指派）
     *
     * @param assignDTO
     * @return
     */
    @Override
    @GlobalTransactional
    //指派
    public Boolean setFeedbackGridMember(AssignDTO assignDTO) {
        //首先验证feedback是否可指派（按照业务逻辑来说，所有反馈记录可以重新指派）
        //验证GridManager状态是否可用
        boolean accessible = userClient.isAssign(assignDTO.getLogId()).getData();
        if (!accessible) {
            throw new RuntimeException("GridManager状态不可用");
        }
        //查询反馈记录
        LambdaQueryWrapper<AqiFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AqiFeedback::getAfId, assignDTO.getAfId());
        AqiFeedback aqiFeedback = aqiFeedbackMapper.selectOne(wrapper);
        if (aqiFeedback == null) {
            return false;
        }
        aqiFeedback.setGmId(assignDTO.getLogId());
        //时间
        long current = DateUtil.current();
        aqiFeedback.setAssignDate(Date.valueOf(DateUtil.format(new Date(current), "yyyy-MM-dd")));
        aqiFeedback.setAssignTime(Time.valueOf(DateUtil.format(new Date(current), "HH:mm:ss")));
        //1为已指派
        aqiFeedback.setState(1);
        //更新feedback记录
        int i = aqiFeedbackMapper.updateById(aqiFeedback);
//        //去除不必要的字段
//        assignDTO.setAfId(null);
//        //设置网格地址
//        assignDTO.setAddress(aqiFeedback.getAddress());
        //不强制执行指派，由网格员自己决定是否接受指派，指派会在玩个圆名下处于待执行状态
//        //远程调用user-server,指派任务
//        boolean assignTask = userClient.assignGridManager(assignDTO).getData();
//        if (!assignTask) {
//            throw new RuntimeException("指派任务失败");
//        }
        //判断是否是前500条记录，如果是则写入缓存，否则不写入
        if (i == 1) {
            if (aqiFeedbackMapper.selectCount(null) <= 500) {
                aqiFeedbackRepository.updateFeedbackData(aqiFeedback);
            }
        }
        NotifyDTO notifyDTO = new NotifyDTO();
        notifyDTO.setRequireRefresh(true);
        notifyDTO.setMsg("反馈成功");
        feedbackPublisher.sendFeedbackNotify(JSONUtil.toJsonStr(notifyDTO));
        return i == 1;
    }

    /**
     * 获取全部反馈信息前几页
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public List<AqiFeedback> findPageBackByPage(int page, int size) {
        long num = (long) page * size;
        LambdaQueryWrapper<AqiFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AqiFeedback::getAfId);
        //查询前num条记录
        return aqiFeedbackMapper.selectList(wrapper.last("limit " + num));

    }

    @Override
    public AqiFeedback getFeedbackById(Integer feedbackId) {
        return aqiFeedbackMapper.selectById(feedbackId);
    }

    @Override
    @Transactional
    public Boolean updateFeedbackState(Integer afId, Integer state) {
        UpdateWrapper<AqiFeedback> wrapper = new UpdateWrapper<>();
        //获取当前时间
        long current = DateUtil.current();
        wrapper.eq("af_id", afId);
        wrapper.set("state", state);
        wrapper.set("confirm_datetime", DateUtil.format(new Date(current), "yyyy-MM-dd HH:mm:ss"));
        int i = 0;
        try {
            i = aqiFeedbackMapper.update(wrapper);
            //修改redis中的数据
            aqiFeedbackRepository.updateFeedbackData(aqiFeedbackMapper.selectById(afId));
        } catch (Exception e) {
            throw new RuntimeException("修改失败" + e.getMessage());
        }
        return i == 1;
    }

    /**
     * 根据条件查询反馈信息
     *
     * @param feedbackQueryDTO
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<AqiFeedBackVO> getFeedBackByConditions(FeedbackQueryDTO feedbackQueryDTO, int page, int size) {
        //缓存不容易实现条件查询，所以直接从数据库查询
        LambdaQueryWrapper<AqiFeedback> wrapper = new LambdaQueryWrapper<>();
        if (feedbackQueryDTO.getTelId() != null) {
            wrapper.like(AqiFeedback::getTelId, feedbackQueryDTO.getTelId());
        }
        //如果省市区名称不为空
        if (feedbackQueryDTO.getProvinceName() != null || feedbackQueryDTO.getCityName() != null || feedbackQueryDTO.getDistrictName() != null) {
            String provinceId = null;
            if (feedbackQueryDTO.getProvinceName() != null) {
                //获取省份编号
                provinceId = (String) geoClient.getProvinceId(feedbackQueryDTO.getProvinceName()).getData();
                if (provinceId == null || provinceId.equals("")) {
                    return null;
                }
                wrapper.eq(AqiFeedback::getProvinceId, provinceId);
            }
            String cityId = null;
            if (feedbackQueryDTO.getCityName() != null && provinceId != null) {
                //获取城市编号
                cityId = (String) geoClient.getCityIdByProvinceId(feedbackQueryDTO.getCityName(), provinceId).getData();
                if (cityId == null || cityId.equals("")) {
                    return null;
                }
                wrapper.eq(AqiFeedback::getCityId, cityId);
            }
            String districtId = null;
            if (feedbackQueryDTO.getDistrictName() != null && cityId != null) {
                //获取区域编号
                districtId = (String) geoClient.getDistrictId(feedbackQueryDTO.getDistrictName(), cityId).getData();
                if (districtId != null && !districtId.equals("")) {
                    return null;
                }
                wrapper.eq(AqiFeedback::getDistrictId, districtId);
            }
        }
        //地址
        if (feedbackQueryDTO.getAddress() != null) {
            wrapper.like(AqiFeedback::getAddress, feedbackQueryDTO.getAddress());
        }
        //预估等级
        if (feedbackQueryDTO.getEstimatedGrade() != null) {
            wrapper.eq(AqiFeedback::getEstimatedGrade, feedbackQueryDTO.getEstimatedGrade());
        }
        //指派网格员id
        if (feedbackQueryDTO.getGridManager_id() != null) {
            wrapper.eq(AqiFeedback::getGmId, feedbackQueryDTO.getGridManager_id());
        }
        //反馈日期
        if (feedbackQueryDTO.getAfDateStart() != null && feedbackQueryDTO.getAfDateEnd() == null) {
            wrapper.ge(AqiFeedback::getAfDate, feedbackQueryDTO.getAfDateStart());
        } else if (feedbackQueryDTO.getAfDateStart() == null && feedbackQueryDTO.getAfDateEnd() != null) {
            wrapper.le(AqiFeedback::getAfDate, feedbackQueryDTO.getAfDateEnd());
        } else if (feedbackQueryDTO.getAfDateStart() != null) {
            wrapper.between(AqiFeedback::getAfDate, feedbackQueryDTO.getAfDateStart(), feedbackQueryDTO.getAfDateEnd());
        }
        //指派日期
        if (feedbackQueryDTO.getAssignDateStart() != null && feedbackQueryDTO.getAssignDateEnd() == null) {
            wrapper.ge(AqiFeedback::getAfTime, feedbackQueryDTO.getAssignDateStart());
        } else if (feedbackQueryDTO.getAssignDateStart() == null && feedbackQueryDTO.getAssignDateEnd() != null) {
            wrapper.le(AqiFeedback::getAfTime, feedbackQueryDTO.getAssignDateEnd());
        } else if (feedbackQueryDTO.getAssignDateStart() != null) {
            wrapper.between(AqiFeedback::getAfTime, feedbackQueryDTO.getAssignDateStart(), feedbackQueryDTO.getAssignDateEnd());
        }
        //反馈日期排序
        if (feedbackQueryDTO.getAfDateAscending() != null && feedbackQueryDTO.getAfDateAscending()) {
            wrapper.orderByAsc(AqiFeedback::getAfDate);
        } else if (feedbackQueryDTO.getAfDateAscending() != null) {
            wrapper.orderByDesc(AqiFeedback::getAfDate);
        }
        //指派日期排序
        if (feedbackQueryDTO.getAssignDateAscending() != null && feedbackQueryDTO.getAssignDateAscending()) {
            wrapper.orderByAsc(AqiFeedback::getAssignDate);
        } else if (feedbackQueryDTO.getAssignDateAscending() != null) {
            wrapper.orderByDesc(AqiFeedback::getAssignDate);
        }
        //同时满足条件
        if (feedbackQueryDTO.getAfDateAscending() != null && feedbackQueryDTO.getAssignDateAscending() != null) {
            //抛出异常
            throw new RuntimeException("不能同时排序");
        }

        //指派状态
        if (feedbackQueryDTO.getAssignStatus() != null) {
            wrapper.eq(AqiFeedback::getState, feedbackQueryDTO.getAssignStatus());
        }
        //分页查询
        Long total = aqiFeedbackMapper.selectCount(wrapper);
        int pages = (int) (total / size) + 1;
        //判断页数是否合法
        if (page < 0 || page > pages) {
            throw new RuntimeException("页数不合法");
        }

        Page<AqiFeedback> aqiFeedbackPage = new Page<>(page, size);
        IPage<AqiFeedback> aqiFeedbackList = aqiFeedbackMapper.selectPage(aqiFeedbackPage, wrapper);
        //查询出VO
        List<AqiFeedBackVO> aqiFeedBackVOS = getAqiFeedBackVOS(aqiFeedbackList.getRecords());
        //构造并返回包含完整DTO数据的分页结果
        IPage<AqiFeedBackVO> feedbackDTOPage = new Page<>(page, size);
        feedbackDTOPage.setRecords(aqiFeedBackVOS);
        feedbackDTOPage.setTotal(total);
        feedbackDTOPage.setPages(pages);
        return feedbackDTOPage;
    }

    @Override
    public AqiFeedBackVO getCurrentFeedback(String logId) {
        Integer afId = userClient.getAfIdByLogId(logId).getData();
        if (afId == null) {
            throw new RuntimeException("当前无正在处理的指派");
        }
        AqiFeedback aqiFeedback = aqiFeedbackMapper.selectById(afId);
        List<AqiFeedback> aqiFeedbacks = new ArrayList<>();
        aqiFeedbacks.add(aqiFeedback);
        List<AqiFeedBackVO> aqiFeedBackVOS = getAqiFeedBackVOS(aqiFeedbacks);
        return aqiFeedBackVOS.get(0);
    }


    /**
     * 从数据库获取反馈信息
     *
     * @param telId
     * @param page
     * @param size
     * @return
     */
    public IPage<AqiFeedback> getFeedbackByTelIdDatabase(String telId, int page, int size) {
        // 分页通过 telId 查询反馈记录
        LambdaQueryWrapper<AqiFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AqiFeedback::getTelId, telId);
        //afid由大到小排序
        wrapper.orderByDesc(AqiFeedback::getAfId);
        Page<AqiFeedback> aqiFeedbackPage = new Page<>(page, size);
        return aqiFeedbackMapper.selectPage(aqiFeedbackPage, wrapper);
    }
}
