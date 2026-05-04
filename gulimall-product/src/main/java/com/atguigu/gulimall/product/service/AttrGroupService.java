package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author shibh
 * @email 2572182545@qq.com
 * @date 2025-11-25 15:41:22
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    Long[] findCategoryPath(Long catelogId);

    List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

