package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺状态接口")
@Slf4j

public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 查询店铺状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("查询店铺营业状态")
    public Result<Integer> getStatus(){
        Integer shopStatus = (Integer)redisTemplate.opsForValue().get("shopStatus");
        log.info("获取店铺营业状态:{}" ,shopStatus == 1?"营业中":"打样中");
        return Result.success(shopStatus);
    }
}
