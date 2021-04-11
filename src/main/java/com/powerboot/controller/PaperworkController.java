package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.common.JsonUtils;
import com.powerboot.consts.AboutUsConsts;
import com.powerboot.consts.CacheConsts;
import com.powerboot.domain.IncomeMethodsDO;
import com.powerboot.domain.UserDO;
import com.powerboot.request.BaseRequest;
import com.powerboot.response.AboutUsResponse;
import com.powerboot.response.InviteFriendResponse;
import com.powerboot.response.ServiceCenterResponse;
import com.powerboot.response.ServiceMobileResponse;
import com.powerboot.service.IncomeMethodsService;
import com.powerboot.service.UserService;
import com.powerboot.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paperwork")
@Api(tags = "提示文案")
public class PaperworkController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private IncomeMethodsService incomeMethodsService;

    @ApiOperation(value = "刷单记录数据提供方提示")
    @PostMapping("/orderRecordProvided")
    @ResponseBody
    public BaseResponse<String> orderRecordProvided() {
        return BaseResponse.success(RedisUtils.getValue("orderRecordProvided", String.class));
    }

    @ApiOperation(value = "客服中心提示")
    @PostMapping("/serviceCenter")
    @ResponseBody
    public BaseResponse<ServiceCenterResponse> serviceCenter(@RequestBody @Valid BaseRequest request) {
        ServiceCenterResponse response = new ServiceCenterResponse();
        response.setTop(RedisUtils.getValue("ServiceCenterTop", String.class));
        response.setCustomerServiceHour(RedisUtils.getValue("CustomerServiceHour", String.class));
        response.setWithdrawalTimeDay(RedisUtils.getValue("WithdrawalTimeDay", String.class));
        response.setWithdrawalTimeHour(RedisUtils.getValue("WithdrawalTimeHour", String.class));
        response.setLast(RedisUtils.getValue("ServiceCenterLast", String.class));
        Long userId = getUserId(request);
        UserDO userDO = userService.get(userId);
        if(userDO!=null){
            String mobileStr = RedisUtils.getValue("CUSTOMER_SERVICE_"+userDO.getTeamFlag(), String.class);
            response.setMobile(JsonUtils.parseArray(mobileStr, ServiceMobileResponse.class));
        }
        return BaseResponse.success(response);
    }

    @ApiOperation(value = "功能简介提示")
    @PostMapping("/incomeMethods")
    @ResponseBody
    public BaseResponse<List<IncomeMethodsDO>> incomeMethods() {
        return BaseResponse.success(incomeMethodsService.getContent());
    }

    @ApiOperation(value = "关于我们提示")
    @PostMapping("/aboutUs")
    @ResponseBody
    public BaseResponse<AboutUsResponse> aboutUs() {
        return BaseResponse.success(AboutUsConsts.aboutUsResponse);
    }

    @ApiOperation(value = "邀请好友提示")
    @PostMapping("/inviteFriend")
    @ResponseBody
    public BaseResponse<InviteFriendResponse> inviteFriend(@RequestBody @Valid BaseRequest request) {
        InviteFriendResponse response = new InviteFriendResponse();
        response.setTopOneList(RedisUtils.getList("InviteFriendTopOne", String.class));
        response.setTopTwoList(RedisUtils.getList("InviteFriendTopTwo", String.class));
        response.setVipTable(RedisUtils.getList("InviteFriendVipTable", String.class));
        Long userId = getUserId(request);
        UserDO userDO = userService.get(userId);
        if(userDO==null ||  (0 == userDO.getShareFlag())){
            response.setReferralCode("");
            response.setInvitationUrl("");
        }else{
            String preUrl = RedisUtils.getString(CacheConsts.INVITATION_PRE_URL);
            response.setReferralCode(userDO.getReferralCode());
            response.setInvitationUrl(preUrl + userDO.getReferralCode());
        }
        response.setBottomList(RedisUtils.getList("InviteFriendBottom", String.class));
        return BaseResponse.success(response);
    }

}
