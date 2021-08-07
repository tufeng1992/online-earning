package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.domain.UserPrizeListDO;
import com.powerboot.request.BaseRequest;
import com.powerboot.request.PrizeListRequest;
import com.powerboot.service.UserPrizeListService;
import com.powerboot.utils.paystack.domain.dto.PayStackBank;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/prizeList")
@Api(tags = "奖励服务")
public class UserPrizeListController extends BaseController {

    @Autowired
    private UserPrizeListService userPrizeListService;

    /**
     * 获取抽奖列表
     * @return
     */
    @PostMapping("/prizes")
    @ApiOperation("获取抽奖列表")
    public BaseResponse<List<UserPrizeListDO>> selectPrizeList(@RequestBody @Valid PrizeListRequest param) {
        Long userId = getUserId(param);
        BaseResponse<List<UserPrizeListDO>> response = BaseResponse.success();
        List<UserPrizeListDO> data = userPrizeListService.selectPrizeList(userId, param);
        response.setResultData(data);
        return response;
    }

    /**
     * 获取抽奖列表-轮播
     * @return
     */
    @PostMapping("/prizes/banner")
    @ApiOperation("获取抽奖列表-轮播")
    public BaseResponse<List<UserPrizeListDO>> selectBannerPrizeList() {
        BaseResponse<List<UserPrizeListDO>> response = BaseResponse.success();
        List<UserPrizeListDO> data = userPrizeListService.selectBannerPrizeList();
        response.setResultData(data);
        return response;
    }

    /**
     * 获取抽奖数量
     * @return
     */
    @PostMapping("/prizeCount")
    @ApiOperation("获取抽奖数量")
    public BaseResponse<Integer> selectPrizeCount(@RequestBody @Valid PrizeListRequest param) {
        Long userId = getUserId(param);
        BaseResponse<Integer> response = BaseResponse.success();
        Integer data = userPrizeListService.selectPrizeListCount(userId, param);
        response.setResultData(data);
        return response;
    }

    /**
     * 获取抽奖结果
     * @return
     */
    @PostMapping("/comsumerPrize/{prizeId}")
    @ApiOperation("获取抽奖结果")
    public BaseResponse<BigDecimal> comsumerPrize(@RequestBody @Valid PrizeListRequest param, @PathVariable Long prizeId) {
        Long userId = getUserId(param);
        BaseResponse<BigDecimal> response = BaseResponse.success();
        BigDecimal data = userPrizeListService.comsumerUserPrize(userId, prizeId);
        response.setResultData(data);
        return response;
    }

}
