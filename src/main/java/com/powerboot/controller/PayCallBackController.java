package com.powerboot.controller;

import com.powerboot.common.JsonUtils;
import com.powerboot.domain.PayDO;
import com.powerboot.enums.PayEnums;
import com.powerboot.service.CallBackService;
import com.powerboot.service.PayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pay/callback")
@Api(tags = "支付回调")
public class PayCallBackController extends BaseController{

    @Autowired
    private PayService payService;
    @Autowired
    private CallBackService callBackService;

    @ApiOperation("wegeme 支付回调")
    @PostMapping("/wegeme/payIn")
    public String darajaPayInCallBack(@RequestBody Map<String, Object> requestJson) {
        logger.info("wegeme 支付回调：" + JsonUtils.toJSONString(requestJson));
        JSONObject jsonObject = new JSONObject(JsonUtils.toJSONString(requestJson));
        requestJson.put("payChannelBranch","wegeme");
        if (!(jsonObject.has("Body") &&
                jsonObject.getJSONObject("Body").has("stkCallback"))){
            return "FAIL";
        }
        JSONObject stkCallback = jsonObject.getJSONObject("Body").getJSONObject("stkCallback");
        int resultCode = stkCallback.getInt("ResultCode");
        String merchantRequestID = stkCallback.getString("MerchantRequestID");
        String checkoutRequestID = stkCallback.getString("CheckoutRequestID");
        String resultDesc = stkCallback.getString("ResultDesc");
        PayDO payDO = payService.getByOutNo(checkoutRequestID);
        if (payDO == null){
            return "FAIL";
        }
        logger.info("wegeme 支付回调,用户id--{}：{}",payDO.getUserId(), JsonUtils.toJSONString(requestJson));
        payDO.setThirdNo(checkoutRequestID);
        payDO.setThirdStatus(String.valueOf(resultCode));
        if (!stkCallback.has("CallbackMetadata")){
            payDO.setThirdResponse(stkCallback.getString("ResultDesc"));
            payService.updatePay(payDO);
            return "SUCCESS";
        }
        JSONArray jsonArray = stkCallback.getJSONObject("CallbackMetadata").getJSONArray("Item");
        for (int i=0;i < jsonArray.length();i++){
            JSONObject jsonObjectIndex = jsonArray.getJSONObject(i);
            if ("MpesaReceiptNumber".equals(jsonObjectIndex.getString("Name"))){
                //支付订单号
                payDO.setThirdResponse(jsonObjectIndex.getString("Value"));
            }
        }
        payService.updatePay(payDO);
        if (resultCode == 0
                && !PayEnums.PayStatusEnum.PAID.getCode().equals(payDO.getStatus())){
            payService.getByOrderNo(payDO.getOrderNo());
        }
        return "SUCCESS";
    }

    @ApiOperation("wegeme 提现回调")
    @PostMapping("/wegeme/payOut")
    public String darajaPayoutCallBack(@RequestBody Map<String, Object> requestJson) {
        logger.info("wegeme 提现回调：" + JsonUtils.toJSONString(requestJson));
        JSONObject jsonObject = new JSONObject(JsonUtils.toJSONString(requestJson));
        if (!(jsonObject.has("Result") &&
                jsonObject.getJSONObject("Result").has("ResultCode"))){
            return "SUCCESS";
        }
        JSONObject result = jsonObject.getJSONObject("Result");
        int resultCode = result.getInt("ResultCode");
        String ConversationID = result.getString("ConversationID");
        String TransactionID = result.getString("TransactionID");
        PayDO payDO = payService.getByOutNo(ConversationID);
        if (payDO == null){
            return "FAIL";
        }
        logger.info("daraja 提现回调,用户ID--{}：{}" ,payDO.getUserId(), JsonUtils.toJSONString(requestJson));
        payDO.setThirdStatus(String.valueOf(resultCode));
        payDO.setThirdNo(TransactionID);
        if (resultCode == 0
                && !PayEnums.PayStatusEnum.PAID.getCode().equals(payDO.getStatus())){
            //成功
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO,"wegame");
        }else if (!PayEnums.PayStatusEnum.FAIL.getCode().equals(payDO.getStatus())){
            //失败
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO,"wegame");
        }
        requestJson.put("payChannelBranch","wegame");
        return "SUCCESS";
    }

    @ApiOperation("payStack 支付回调")
    @PostMapping("/payStack/payIn")
    public String payStackPayInCallBack(@RequestBody String requestJson) {
        logger.info("payStack 支付回调：" + requestJson);
        return "SUCCESS";
    }

    @ApiOperation("flutter 支付回调")
    @PostMapping("/flutter/payIn")
    public String flutterPayInCallBack(String tx_ref, String transaction_id, String status) {
        logger.info("flutter 支付回调：tx_ref : {}, transaction_id : [}. status : {}",
                tx_ref, transaction_id, status);
        if (StringUtils.isBlank(tx_ref)) {
            return "FAIL";
        }
        PayDO payDO = payService.getOrderNo(tx_ref);
        if (null != payDO) {
            payDO.setThirdNo(transaction_id);
            payDO.setThirdStatus(status);
            payService.update(payDO);
        }
        return "SUCCESS";
    }

}
