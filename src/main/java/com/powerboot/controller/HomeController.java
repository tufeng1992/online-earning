package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictAccount;
import com.powerboot.consts.DictConsts;
import com.powerboot.domain.UserDO;
import com.powerboot.domain.WindowContentDO;
import com.powerboot.request.BaseRequest;
import com.powerboot.response.HomeDetailsDto;
import com.powerboot.response.HomeResponse;
import com.powerboot.response.TodayResultDto;
import com.powerboot.service.OrderService;
import com.powerboot.service.UserService;
import com.powerboot.service.WindowContentService;
import com.powerboot.utils.MobileUtil;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "首页")
@RestController
@RequestMapping("/home")
public class HomeController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private WindowContentService windowContentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @ApiOperation("首页资产详情")
    @PostMapping("/homePageDetails")
    public BaseResponse<HomeDetailsDto> homePageDetails(@RequestBody @Valid BaseRequest param) {
        BaseResponse<HomeDetailsDto> response = BaseResponse.success();
        HomeDetailsDto homeDetailsDto = new HomeDetailsDto();
        Long userId = getUserId(param);
        UserDO userDO = userService.get(userId);

        homeDetailsDto.setNickName(userDO.getNikeName());
        homeDetailsDto.setTotalAssets(userDO.getBalance());

        BigDecimal virtualBalance = new BigDecimal(
            Objects.requireNonNull(RedisUtils.getValue(DictAccount.VIRTUAL_BALANCE, String.class)));
        homeDetailsDto.setVirtualCurrency(virtualBalance);
        homeDetailsDto.setVipLevel("VIP" + userDO.getMemberLevel());
        TodayResultDto todayResultDto = orderService.getTodayResult(userDO);
        homeDetailsDto.setTotalEarnings(todayResultDto.getTotalEarnings());
        homeDetailsDto.setYesterdayEarnings(todayResultDto.getYesterdayEarnings());

        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userDO.getId());

        homeDetailsDto.setCumulativeIncome(orderService.sumAmount(map));
        response.setResultData(homeDetailsDto);
        return response;
    }

    @ApiOperation("获取弹窗信息")
    @PostMapping("/windowContent")
    public BaseResponse<List<WindowContentDO>> getWindowContent() {
        return BaseResponse.success(windowContentService.getContent());
    }

    @ApiOperation("获取跑马灯信息")
    @PostMapping("/run")
    public BaseResponse<HomeResponse> homeRun(@RequestBody BaseRequest baseRequest) {
        HomeResponse homeResponse = new HomeResponse();
        Integer applyCount = RedisUtils.getValue(DictConsts.APPLY_SHOW_COUNT, Integer.class);
        Integer applyAmountMin = RedisUtils.getValue(DictConsts.APPLY_AMOUNT_MIN, Integer.class);
        Integer applyAmountMax = RedisUtils.getValue(DictConsts.APPLY_AMOUNT_MAX, Integer.class);
        List<String> msgList = new ArrayList<>();
        List<String> mobileList = MobileUtil.getIndiaMobileList(applyCount);
        for (int i = 0; i < applyCount; i++) {
            msgList.add(mobileList.get(i) + " : aaa Revenue " + MobileUtil
                .getRandom(applyAmountMin, applyAmountMax) + " ");
        }
        homeResponse.setSuccessMessageList(msgList);
        homeResponse.setColor(RedisUtils.getString(DictConsts.HOME_TEXT_COLOR));
        return BaseResponse.success(homeResponse);
    }
}
