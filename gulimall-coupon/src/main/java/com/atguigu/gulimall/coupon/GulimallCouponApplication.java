package com.atguigu.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1.如何使用Nacos作为配置中心
 * 1）引入依赖
 * <dependency>
 *     <groupId>com.alibaba.cloud</groupId>
 *     <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 * </dependency>
 * 2）创建一个bootstrap.properties
 * # 应用名称
 * spring.application.name=nacos-config-example
 *
 * # 配置中心地址
 * spring.cloud.nacos.config.server-addr=127.0.0.1:8848
 *
 * 3) 需要给配置中心默认添加一个 数据集(Data Id) 规则：应用名称.properties
 * 4) 给数据集添加任何配置
 * 5）动态获取配置
 *  在对应类中增加 @RefreshScope  动态获取并刷新配置
 *   在对应参数中增加 @Value(“${配置项的名称}”)
 *   配置中心文件 优先级 大于 当前应用的配置
 *
 */

@MapperScan("com.atguigu.gulimall.coupon.dao")
@SpringBootApplication
@EnableDiscoveryClient
public class GulimallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
