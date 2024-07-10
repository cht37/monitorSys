package com.neu.monitor_sys.user.consumer;

import com.neu.monitor_sys.user.client.GeoClient;
import com.neu.monitor_sys.user.service.IGridManagerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignConsumer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private GeoClient geoClient;
    @Autowired
    private IGridManagerService gridManagerService;


    /**
     * 接收分配任务消息，停用
     * @param assignDTO
     */
//    @RabbitListener(queues = "assignTaskQueue")
//    public void receiveAssign(AssignDTO assignDTO) {
//
//        Integer gridId = (Integer) geoClient.getGridIdByGridName(assignDTO.getAddress()).getData();
//        if(gridId == null) {
//            return;
//        }
//        GridManager gridManager = new GridManager();
//        gridManager.setAreaId(gridId);
////        gridManager.setMemberId(assignDTO.getLogId());
//        gridManager.setRemark(assignDTO.getRemark());
//        //设置网格员状态为1（临时调用）
//        gridManager.setState(1);
//        gridManagerService.updateGridManager(gridManager);
//
//    }
}
