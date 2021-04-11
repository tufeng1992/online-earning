package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.common.JsonUtils;
import com.powerboot.consts.AmountConsts;
import com.powerboot.domain.FinancialOrderDO;
import com.powerboot.domain.FinancialProductDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.FinancialOrderStatusEnum;
import com.powerboot.param.FinancialOrderParam;
import com.powerboot.param.FinancialProductParam;
import com.powerboot.request.BaseRequest;
import com.powerboot.request.financial.CalculateInterestRequest;
import com.powerboot.request.financial.PurchaseFinancialRequest;
import com.powerboot.request.financial.TakeOutFinancialRequest;
import com.powerboot.response.FinancialAssetsResponse;
import com.powerboot.response.FinancialOrderResponse;
import com.powerboot.response.FinancialProductResponse;
import com.powerboot.response.TakeOutWarningResponse;
import com.powerboot.service.FinancialOrderService;
import com.powerboot.service.FinancialProductService;
import com.powerboot.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "理财模块")
@RestController
@RequestMapping("/financial")
public class FinancialController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(FinancialController.class);
    @Autowired
    private FinancialProductService financialProductService;
    @Autowired
    private UserService userService;
    @Autowired
    private FinancialOrderService financialOrderService;

    private static final List<Integer> OK_STATUS_LIST = new ArrayList<>();
    static {
        OK_STATUS_LIST.add(FinancialOrderStatusEnum.UNEXPIRED.getCode());
        OK_STATUS_LIST.add(FinancialOrderStatusEnum.EXPIRED.getCode());
    }

    @ApiOperation(value = "获取理财产品列表")
    @PostMapping("/getFinancialProductList")
    @ResponseBody
    public BaseResponse<List<FinancialProductResponse>> getFinancialProductList() {
        BaseResponse<List<FinancialProductResponse>> response = BaseResponse.success();

        FinancialProductParam param = new FinancialProductParam();
        param.setSort(" level desc ");
        List<FinancialProductDO> productDOList = financialProductService.getByParams(param);
        if (CollectionUtils.isEmpty(productDOList)) {
            return null;
        }
        List<FinancialProductResponse> responseList = new ArrayList<>(productDOList.size());
        productDOList.forEach(o -> {
            FinancialProductResponse productResponse = new FinancialProductResponse();
            BeanUtils.copyProperties(o, productResponse);
            responseList.add(productResponse);
        });
        response.setResultData(responseList);
        return response;
    }

    @ApiOperation(value = "根据理财产品id获取理财产品详情")
    @PostMapping("/getFinancialProductById")
    @ResponseBody
    public BaseResponse<FinancialProductResponse> getFinancialProductById(
        @RequestBody @Valid @NotNull Integer productId) {
        BaseResponse<FinancialProductResponse> response = BaseResponse.success();
        response.setResultData(financialProductService.getFinancialProductResponseById(productId));
        return response;
    }

    @ApiOperation(value = "根据输入金额计算预计返回利润")
    @PostMapping("/calculateInterest")
    @ResponseBody
    public BaseResponse<BigDecimal> calculateInterest(@RequestBody @Valid CalculateInterestRequest request) {
        BaseResponse<BigDecimal> response = BaseResponse.success();
        FinancialProductResponse financialProductResponse = financialProductService.
            getFinancialProductResponseById(request.getProductId());
        if (financialProductResponse == null) {
            response = BaseResponse.fail("Please select financial product");
            return response;
        }
        BigDecimal dayRate = financialProductResponse.getDayRate();
        Integer lockDay = financialProductResponse.getLockDays();
        BigDecimal interest = request.getAmount().multiply(dayRate).
            divide(AmountConsts.AMOUNT_100, 2, BigDecimal.ROUND_DOWN);
        interest = interest.multiply(BigDecimal.valueOf(lockDay));
        response.setResultData(interest);
        return response;
    }

    @ApiOperation(value = "购买理财产品")
    @PostMapping("/purchaseFinancial")
    @ResponseBody
    public BaseResponse<Boolean> purchaseFinancial(@RequestBody @Valid PurchaseFinancialRequest request) {
        BaseResponse<Boolean> response = BaseResponse.success();
        Long userId = getUserId(request);
        FinancialProductDO financialProductDO = financialProductService.getByIdWithCache(request.getProductId());
        //未查询到理财产品
        if (financialProductDO == null) {
            //logger.error("【购买理财】未查询到理财产品，request={}，userId={}", JsonUtils.toJSONString(request), userId);
            response = BaseResponse.fail("Your system error, Please close app , Try open again");
            return response;
        }
        //起购金额
        if (request.getAmount().compareTo(financialProductDO.getStartAmount()) < 0) {
            response = BaseResponse.fail(
                "A single purchase required more than " + financialProductDO.getStartAmount().setScale(0, BigDecimal.ROUND_DOWN));
            return response;
        }
        //单笔购买上限
        if (request.getAmount().compareTo(financialProductDO.getTopAmount()) > 0) {
            response = BaseResponse.fail(
                "A single purchase required less than " + financialProductDO.getTopAmount().setScale(0, BigDecimal.ROUND_DOWN));
            return response;
        }
        UserDO userDO = userService.get(userId);
        if(userDO==null){
            //logger.error("【购买理财】未查询到用户信息，request={}，userId={}", JsonUtils.toJSONString(request), userId);
            response = BaseResponse.fail("Please login first");
            return response;
        }
        if(userDO.getBalance().compareTo(request.getAmount())<0){
            //logger.error("【购买理财】购买金额超出余额，request={}，user={}", JsonUtils.toJSONString(request), JsonUtils.toJSONString(userDO));
            response = BaseResponse.fail("Your balance is insufficient, Please recharge first");
            return response;
        }
        //起购vip等级
        if(userDO.getMemberLevel()<financialProductDO.getStartVip()){
            response = BaseResponse.fail("The product need Member VIP"
                    +financialProductDO.getStartVip()
                    +" and higher, Please purchase Member VIP first"
            );
            return response;
        }
        //产品最大限购额
        FinancialOrderParam param = new FinancialOrderParam();
        param.setUserId(userId.intValue());
        param.setProductId(financialProductDO.getId());
        param.setOrderStatusList(OK_STATUS_LIST);
        List<FinancialOrderDO> orderDOList = financialOrderService.getByParams(param);
        if(CollectionUtils.isNotEmpty(orderDOList)){
            BigDecimal totalAmount = orderDOList.stream().map(FinancialOrderDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal thisAmount = totalAmount.add(request.getAmount());
            if(thisAmount.compareTo(financialProductDO.getTotalAmount()) > 0){
                response = BaseResponse.fail(
                        financialProductDO.getName()
                        + " total purchase limit is "
                        + financialProductDO.getTotalAmount().setScale(0, BigDecimal.ROUND_DOWN)
                        +", You have purchased "
                        + totalAmount +
                        ", Please modify your input amount");
                return response;
            }
        }
        response.setResultData(financialProductService.purchaseFinancial(request, userDO, financialProductDO));
        return response;
    }

    @ApiOperation(value = "个人理财订单列表")
    @PostMapping("/financialOrderList")
    @ResponseBody
    public BaseResponse<List<FinancialOrderResponse>> financialOrderList(@RequestBody @Valid BaseRequest request) {
        BaseResponse<List<FinancialOrderResponse>> response = BaseResponse.success();
        Long userId = getUserId(request);
        response.setResultData(financialProductService.financialOrderList(userId));
        return response;
    }

    @ApiOperation(value = "获取提前赎回提示警告")
    @PostMapping("/getTakeOutWarning")
    @ResponseBody
    public BaseResponse<TakeOutWarningResponse> getTakeOutWarning(@RequestBody @Valid TakeOutFinancialRequest request) {
        BaseResponse<TakeOutWarningResponse> response = BaseResponse.success();
        response.setResultData(financialProductService.getTakeOutWarning(request.getOrderId()));
        return response;
    }

    @ApiOperation(value = "提前赎回理财产品")
    @PostMapping("/takeOutFinancial")
    @ResponseBody
    public BaseResponse<Boolean> takeOutFinancial(@RequestBody @Valid TakeOutFinancialRequest request) {
        BaseResponse<Boolean> response = BaseResponse.success();
        Long userId = getUserId(request);
        response.setResultData(financialProductService.takeOutFinancial(request.getOrderId(), userId.intValue()));
        return response;
    }

    @ApiOperation(value = "获取理财收益汇总")
    @PostMapping("/getAssets")
    @ResponseBody
    public BaseResponse<FinancialAssetsResponse> getAssets(@RequestBody @Valid BaseRequest request) {
        BaseResponse<FinancialAssetsResponse> response = BaseResponse.success();
        Long userId = getUserId(request);
        response.setResultData(financialProductService.getAssets(userId.intValue()));
        return response;
    }


}
