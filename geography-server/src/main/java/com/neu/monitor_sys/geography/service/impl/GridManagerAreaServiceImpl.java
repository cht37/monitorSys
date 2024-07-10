package com.neu.monitor_sys.geography.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.neu.monitor_sys.geography.DTO.AreaMQDTO;
import com.neu.monitor_sys.geography.DTO.AreaDTO;
import com.neu.monitor_sys.geography.DTO.GeographyVO;
import com.neu.monitor_sys.common.entity.GridManagerArea;
import com.neu.monitor_sys.geography.mapper.GridManagerAreaMapper;
import com.neu.monitor_sys.geography.service.ICitiesService;
import com.neu.monitor_sys.geography.service.IDistrictService;
import com.neu.monitor_sys.geography.service.IGridManagerAreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neu.monitor_sys.geography.service.IProvincesService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chen hua teng
 * @since 2024-06-10
 */
@Service
public class GridManagerAreaServiceImpl extends ServiceImpl<GridManagerAreaMapper, GridManagerArea> implements IGridManagerAreaService {
    @Autowired
    private GridManagerAreaMapper gridManagerAreaMapper;
    @Autowired
    private ICitiesService citiesService;
    @Autowired
    private IDistrictService districtService;
    @Autowired
    private IProvincesService provincesService;

    @Override
    public GeographyVO getAreDetailById(Integer id) {
        return gridManagerAreaMapper.getAreDetailById(id);
    }

    /**
     * 写入管理区域信息
     * @param areaDTO
     * @return
     */
    @Override
    public Boolean saveArea(AreaDTO areaDTO) {
        GridManagerArea gridManagerArea = new GridManagerArea();
        // 通过AopContext.currentProxy()获取代理对象，调用saveData方法，解决事务失效问题
        GridManagerAreaServiceImpl gridManagerAreaService = (GridManagerAreaServiceImpl) AopContext.currentProxy();
        return gridManagerAreaService.saveData(areaDTO, areaDTO.getProvinceId(), gridManagerArea);

    }

    /**
     * 通过MQ写入管理区域信息
     * @param area
     * @return
     */
    @Override
    public Boolean saveAreaByMQ(AreaMQDTO area) {
        GridManagerArea gridManagerArea = new GridManagerArea();
        String provinceId = provincesService.getProvinceId(area.getProvinceName());
        if(provinceId == null||provinceId.equals("")){
            return false;
        }
        AreaDTO areaDTO = new AreaDTO();
        BeanUtil.copyProperties(area, areaDTO);
        // 通过AopContext.currentProxy()获取代理对象，调用saveData方法，解决事务失效问题
        GridManagerAreaServiceImpl gridManagerAreaService = (GridManagerAreaServiceImpl) AopContext.currentProxy();
        return gridManagerAreaService.saveData(areaDTO, provinceId, gridManagerArea);
    }

    /**
     * 通过网格名称（地址）获取网格id
     * @param gridName
     * @return
     */
    @Override
    public Integer getGridIdByGridName(String gridName) {
        LambdaQueryWrapper<GridManagerArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GridManagerArea::getAreaName, gridName);
        GridManagerArea gridManagerArea = gridManagerAreaMapper.selectOne(wrapper);
        if (gridManagerArea == null) {
            return null;
        }
        return gridManagerArea.getId();
    }

    /**
     * 通过网格地址模糊查询获取网格Id
     * @param gridName 网格名称
     * @return 网格Id
     */
    @Override
    public Integer getGridIdByGridNameLike(String gridName) {
        LambdaQueryWrapper<GridManagerArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(GridManagerArea::getAreaName, gridName);
        GridManagerArea gridManagerArea = gridManagerAreaMapper.selectOne(wrapper);
        if (gridManagerArea == null) {
            return null;
        }
        return gridManagerArea.getId();
    }

    /**
     * 保存数据到数据库
     * @param area
     * @param provinceId
     * @param gridManagerArea
     * @return
     */
    @Transactional
    public boolean saveData(AreaDTO area, String provinceId, GridManagerArea gridManagerArea) {
        String cityId = citiesService.getCityIdByProvinceId(area.getCityName(), provinceId);
        if(cityId == null||cityId.equals("")){
            return false;
        }
        String districtId = districtService.getDistrictIdByCityId(area.getDistrictName(), cityId);
        //从数据库中查询区id相同的网格信息，如果有address相同则不插入
       if(isGridNameExist(area.getAddress(),districtId)){
           return false;
       }
        gridManagerArea.setAreaName(area.getAddress());
        gridManagerArea.setDistrictId(districtId);
        return gridManagerAreaMapper.insert(gridManagerArea) > 0;
    }

     private boolean isGridNameExist(String gridName,String districtId){
        LambdaQueryWrapper<GridManagerArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GridManagerArea::getAreaName, gridName);
        wrapper.eq(GridManagerArea::getDistrictId, districtId);
        return gridManagerAreaMapper.selectCount(wrapper) > 0;
    }
}
