package com.atguigu.gulimall.product.service.impl;


import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVO;
import com.atguigu.gulimall.product.vo.AttrGroupWithAttrsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrAttrgroupRelationServiceImpl attrAttrgroupRelationService;
    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        if (catelogId==0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<AttrGroupEntity>()
            );
            return new PageUtils(page);
        }else {
            String key = (String) params.get("key");
            //SQL:  select * from pms_attr_group where catelogId = ?  and (attrGroupId=? or attrGroupName like ?)
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catelogId);

            if (StringUtils.isNotEmpty(key)){
                wrapper.and((obj)->{
                    obj.eq("attr_group_id",key).or().like("attr_group_name",key);
                });
            }
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    wrapper
            );
            return new PageUtils(page);
        }
    }

    @Override
    public Long[] findCategoryPath(Long catelogId) {

        List<Long> list = new ArrayList<>();
        findCategoryPath(catelogId,list);
        Collections.reverse(list);

        return list.toArray(new Long[list.size()]);
    }

    @Override
    public List<AttrGroupWithAttrsVO> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntityList = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        List<AttrGroupWithAttrsVO> collect = attrGroupEntityList.stream().map(item -> {
            AttrGroupWithAttrsVO vo = new AttrGroupWithAttrsVO();
            BeanUtils.copyProperties(item, vo);
            List<AttrEntity> relationAttr = attrService.getRelationAttr(item.getAttrGroupId());
            vo.setAttrs(relationAttr);

            return vo;
        }).collect(Collectors.toList());


        return collect;
    }

    /**
     * 递归寻找父元素
     * @param catelogId
     * @param categoryIdPath
     */
    private void findCategoryPath(Long catelogId, List<Long> categoryIdPath) {
        categoryIdPath.add(catelogId);
        CategoryEntity category = categoryService.getById(catelogId);
        if (category!=null && category.getParentCid() != 0){
            findCategoryPath(category.getParentCid(),categoryIdPath);
        }
    }

}