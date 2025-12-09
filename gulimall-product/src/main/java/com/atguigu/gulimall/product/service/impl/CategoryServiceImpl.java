package com.atguigu.gulimall.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        //2.组装成父子的树形结构
        //获取一级菜单
        List<CategoryEntity> collect = categoryEntities.stream()
                .filter(item -> item.getParentCid() == 0)
                .map(item -> {
                    item.setChildren(getChildren(item,categoryEntities));
                    return item;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0:menu1.getSort())  - (menu2.getSort() == null ? 0:menu2.getSort());
                }).collect(Collectors.toList());


        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> list) {
        //TODO  1.检查当前删除的菜单，是否被其他地方引用

        baseMapper.deleteBatchIds(list);
    }

    public List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> childrenList = all.stream()
                .filter(item -> root.getCatId() == item.getParentCid())
                .map(item -> {
                    item.setChildren(getChildren(item, all));
                    return item;
                })
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0:menu1.getSort())  - (menu2.getSort() == null ? 0:menu2.getSort());
                }).collect(Collectors.toList());


        return  childrenList;
    }
}