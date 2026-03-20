package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.ListValue;
import com.atguigu.common.valid.UpdateGroup;
import com.atguigu.common.valid.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author shibh
 * @email 2572182545@qq.com
 * @date 2025-11-25 15:41:22
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
    @NotNull(message = "修改时ID不能为空", groups = {UpdateGroup.class})
    @Null(message = "新增时ID必须为空", groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
    @NotEmpty(message = "品牌名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
    @NotEmpty(message = "logo地址不能为空",groups = {AddGroup.class})
    @URL(message = "必须是合法地址", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
    @NotEmpty(message = "介绍不能为空", groups = {AddGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
    @ListValue(vals = {0,1}, groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
    @NotEmpty(message = "首字母不能为空", groups = {AddGroup.class})
    @Pattern(regexp = "^[a-zA-Z$]$",message = "检索首字母必须为一个字母",groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
    @NotNull(groups = {AddGroup.class})
    @Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
