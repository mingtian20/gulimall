package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.to.SkuReductionTO;
import com.atguigu.common.to.SpuBoundsTO;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuImagesDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private SpuImagesServiceImpl spuImagesService;

    @Autowired
    private AttrService    attrService;

    @Autowired
    private ProductAttrValueService   productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesServiceImpl skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );
        return new PageUtils(page);
    }

    /**
     * TODO  高级部分完善
     *
     * @param vo
     */
    @Transient
    @Override
    public void saveSupInfo(SpuSaveVO vo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);

//2、保存Spu的描述图片 pms_spu_info_desc
        SpuInfoDescEntity  spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",",vo.getDecript()));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);


//3、保存spu的图片集 pms_spu_images

        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(),images);

//4、保存spu的规格参数;pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity pae = new ProductAttrValueEntity();
            pae.setAttrId(item.getAttrId());
            pae.setSpuId(spuInfoEntity.getId());
            AttrEntity id = attrService.getById(item.getAttrId());
            pae.setAttrName(id.getAttrName());
            pae.setAttrValue(item.getAttrValues());
            pae.setQuickShow(item.getShowDesc());
            return pae;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

//5、保存spu的积分信息; gulimall_sms->sms_spu_bounds
        Bounds bounds = vo.getBounds();
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(bounds,spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundsTO);
        if(r.getCode() !=0 ){
            log.error("远程保存spu积分信息失败");
        }


//5、保存当前spu对应的所有sku信息;
//5.1)、sku的基本信息; pms_sku_info
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach(item -> {

                String defaultImage = "";

                List<Images> images1 = item.getImages();

                for (Images image : images1) {
                    if (image.getDefaultImg() == 1){
                        defaultImage = image.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item,skuInfoEntity);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSkuDefaultImg(defaultImage);

                skuInfoService.saveSkuInfo(skuInfoEntity);


                List<SkuImagesEntity> skuImagesEntityList = images1.stream().map(image -> {

                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    skuImagesEntity.setImgUrl(image.getImgUrl());
                    skuImagesEntity.setDefaultImg(image.getDefaultImg());
                    return skuImagesEntity;

                }).filter(entity -> {
                    return !StringUtils.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //5.2)、sku的图片信息; pms_sku_images
                skuImagesService.saveBatch(skuImagesEntityList);


                //5.3)、sku的销售属性信息: pms_sku_sale_attr_value
                List<Attr> attrList = item.getAttr();

                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attrList.stream().map(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(attr, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());

                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());

                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);

//5.4)、sku的优惠、满减等信息; gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                SkuReductionTO skuReductionTO = new SkuReductionTO();
                BeanUtils.copyProperties(item,skuReductionTO);
                skuReductionTO.setSkuId(skuInfoEntity.getSkuId());
                if (skuReductionTO.getFullCount() >0  ||  skuReductionTO.getFullPrice().compareTo(BigDecimal.ZERO) >0){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTO);

                    if(r1.getCode() !=0 ){
                        log.error("远程保存sku优惠信息失败");
                    }
                }

            });
        }




    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         * key:
         *
         * catalogId
         *
         * brandId:
         *
         * status
         */
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> w.eq("id", key).or().like("spu_name", key));
        }

        String catalogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catalogId) && !"0".equals(catalogId)){
            wrapper.eq("catalog_id",catalogId);
        }

        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equals(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status) ){
            wrapper.eq("publish_status",status);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

}