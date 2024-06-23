package com.neu.monitorSys.feedback.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.neu.monitorSys.entity.AqiFeedback;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neu.monitorSys.feedback.DTO.AqiFeedBackVO;
import com.neu.monitorSys.feedback.DTO.AqiFeedbackDTO;
import com.neu.monitorSys.feedback.DTO.AssignDTO;
import com.neu.monitorSys.feedback.DTO.FeedbackQueryDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-11
 */
public interface IAqiFeedbackService extends IService<AqiFeedback> {
    /**
     * 保存反馈信息
     */
    Boolean saveFeedback(AqiFeedbackDTO aqiFeedbackDTO);

    /**
     * 提交反馈信息
     */
    Boolean submitFeedback(AqiFeedbackDTO aqiFeedbackDTO);

    /**
     * 根据公众监督员telId查询反馈记录
     * @param telId 公众监督员telId
     * @param page 页数
     * @param size 每页大小
     * @return 反馈记录
     */
    IPage<AqiFeedBackVO> getFeedbackByTelId(String telId, int page, int size);

    /**
     * 根据网格id查询反馈记录
     * @param gridId 网格id
     * @param page 页数
     * @param size 每页大小
     * @return 反馈记录
     */
    IPage<AqiFeedBackVO> getFeedbackByGridId(String gridId, int page, int size);
    /**
     * 设置反馈网格员（指派）
     */
    Boolean setFeedbackGridMember(AssignDTO assignDTO);
    /**
     * 查询前page页反馈信息（用于信息预载）
     */
    List<AqiFeedback> findPageBackByPage(int page, int size);
    /**
     * 根据反馈id查询反馈信息
     */
    AqiFeedback getFeedbackById(Integer feedbackId);
    /**
     * 修改反馈状态
     */
    Boolean updateFeedbackState(Integer afId,Integer state);

    /**
     * 根据条件查询反馈信息
     * @param feedbackQueryDTO
     * @param page
     * @param size
     * @return
     */
    IPage<AqiFeedBackVO> getFeedBackByConditions(FeedbackQueryDTO feedbackQueryDTO, int page, int size );


}
