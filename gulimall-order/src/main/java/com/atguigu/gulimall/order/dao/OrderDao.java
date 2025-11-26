package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author shibh
 * @email 2572182545@qq.com
 * @date 2025-11-26 10:53:48
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
