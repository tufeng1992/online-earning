package com.powerboot.controller;

import ci.bamba.regis.models.RequestToPay;
import ci.bamba.regis.models.Transfer;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.powerboot.common.JsonUtils;
import com.powerboot.config.BaseException;
import com.powerboot.consts.DictConsts;
import com.powerboot.domain.PayDO;
import com.powerboot.enums.PayEnums;
import com.powerboot.enums.PaymentChannelEnum;
import com.powerboot.enums.PaymentServiceEnum;
import com.powerboot.request.payment.FlutterPayInCallBack;
import com.powerboot.request.payment.FlutterPayInWebhook;
import com.powerboot.service.CallBackService;
import com.powerboot.service.PayNotifyService;
import com.powerboot.service.PayService;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.ServletUtils;
import com.powerboot.utils.flutter.constants.FlutterConts;
import com.powerboot.utils.gms.constants.GMSConst;
import com.powerboot.utils.gms.model.GmsPayInNotify;
import com.powerboot.utils.gms.model.GmsPayOutNotify;
import com.powerboot.utils.grecash.contants.GrecashConst;
import com.powerboot.utils.grecash.model.GrecashPayInCallBack;
import com.powerboot.utils.grecash.model.PayOutNotifyReq;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayDetailRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayOutDetailRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayOutRes;
import com.powerboot.utils.happylife.model.HappyLifeQueryPayRes;
import com.powerboot.utils.qeapay.contants.QeaPayConst;
import com.powerboot.utils.qeapay.domain.QeaPayInNotify;
import com.powerboot.utils.qeapay.domain.QeaPayOutNotify;
import com.powerboot.utils.sepro.constants.SeproConst;
import com.powerboot.utils.sepro.model.SeproPayInNotify;
import com.powerboot.utils.sepro.model.SeproPayOutNotify;
import com.powerboot.utils.thkingz.model.ThkingzBaseRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayOutRes;
import com.powerboot.utils.thkingz.model.ThkingzPayInCallback;
import com.powerboot.utils.thkingz.model.ThkingzPayOutCallback;
import com.powerboot.utils.wallyt.constants.WallytConst;
import com.powerboot.utils.wallyt.domain.dto.CreateTransferNotifyReq;
import com.powerboot.utils.wallyt.domain.dto.WallyBaseResponseObject;
import com.powerboot.utils.wallyt.domain.dto.WallytCallbackReq;
import com.powerboot.utils.wallyt.domain.dto.WallytResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/pay/callback")
@Api(tags = "????????????")
public class PayCallBackController extends BaseController{

    @Autowired
    private PayService payService;
    @Autowired
    private CallBackService callBackService;
    @Autowired
    private PayNotifyService payNotifyService;

    @Resource(name = "commonExecutor")
    private ExecutorService commonExecutor;

    @ApiOperation("wegeme ????????????")
    @PostMapping("/wegeme/payIn")
    public String darajaPayInCallBack(@RequestBody Map<String, Object> requestJson) {
        logger.info("wegeme ???????????????" + JsonUtils.toJSONString(requestJson));
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
        logger.info("wegeme ????????????,??????id--{}???{}",payDO.getUserId(), JsonUtils.toJSONString(requestJson));
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
                //???????????????
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

    @ApiOperation("wegeme ????????????")
    @PostMapping("/wegeme/payOut")
    public String darajaPayoutCallBack(@RequestBody Map<String, Object> requestJson) {
        logger.info("wegeme ???????????????" + JsonUtils.toJSONString(requestJson));
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
        logger.info("daraja ????????????,??????ID--{}???{}" ,payDO.getUserId(), JsonUtils.toJSONString(requestJson));
        payDO.setThirdStatus(String.valueOf(resultCode));
        payDO.setThirdNo(TransactionID);
        if (resultCode == 0
                && !PayEnums.PayStatusEnum.PAID.getCode().equals(payDO.getStatus())){
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO,"wegame");
        }else if (!PayEnums.PayStatusEnum.FAIL.getCode().equals(payDO.getStatus())){
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO,"wegame");
        }
        requestJson.put("payChannelBranch","wegame");
        return "SUCCESS";
    }

    @ApiOperation("payStack ????????????")
    @PostMapping("/payStack/payIn")
    public String payStackPayInCallBack(@RequestBody String requestJson) {
        logger.info("payStack ???????????????" + requestJson);
        return "SUCCESS";
    }

    @ApiOperation("payStack webhook")
    @PostMapping("/payStack/webhook")
    public String paystackWebhookCallBack(@RequestBody String requestJson) {
        logger.info("payStack webhook???{}", requestJson);
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(requestJson);
        String event = jsonObject.getString("event");
        logger.info("payStack webhook???{}", event);
        if (event.startsWith("transfer")) {
            com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("data");
            PayDO payDO = payService.getOrderNo(data.getString("reference"));
            if (payDO == null){
                return "order is not exist";
            }
            logger.info("payStack webhook,??????ID--{}???{}" ,payDO.getUserId(), requestJson);
            payService.getByPayOutOrder(payDO.getOrderNo());
            return "OK";
        } else if (event.startsWith("charge.success")) {
            com.alibaba.fastjson.JSONObject data = jsonObject.getJSONObject("data");
            PayDO payDO = payService.getOrderNo(data.getString("reference"));
            if (payDO == null){
                return "order is not exist";
            }
            logger.info("payStack webhook,??????ID--{}???{}" ,payDO.getUserId(), requestJson);
            payService.getByOrderNo(payDO.getOrderNo());
            return "OK";
        }
        return "fail";
    }

    @ApiOperation("flutter ????????????")
    @PostMapping("/flutter/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String flutterPayInCallBack(@RequestBody @Valid FlutterPayInCallBack flutterPayInCallBack) {
        logger.info("flutter ???????????????flutterPayInCallBack : {}", flutterPayInCallBack);
        PayDO payDO = payService.getOrderNo(flutterPayInCallBack.getTx_ref());
        if (null != payDO) {
            payDO.setThirdNo(flutterPayInCallBack.getTransaction_id());
            payDO.setThirdStatus(flutterPayInCallBack.getStatus());
            payService.update(payDO);
            payService.getByOrderNo(payDO.getOrderNo());
            return "SUCCESS";
        }
        return "FAIL";
    }

    @PostMapping("/flutter/pay/webhook")
    @Transactional(rollbackFor = Exception.class)
    public String flutterPayInWebhook(@RequestBody FlutterPayInWebhook resp) {
        logger.info("flutter ???????????????flutterPayInWebhook : {}", resp);
        HttpServletRequest request = ServletUtils.getRequest();
        String flutterWebhookHash = RedisUtils.getString(DictConsts.FLUTTER_WEBHOOK_HASH);
        String reqHash = request.getHeader("verif-hash");
        if (StringUtils.isBlank(reqHash) || !flutterWebhookHash.equalsIgnoreCase(reqHash)) {
            throw new BaseException("flutterPayInWebhook hash error");
        }
        String event = resp.getEvent();
        String eventType = resp.getEventType();
        Map<String, Object> data = resp.getData();
        if (!"transfer.completed".equalsIgnoreCase(event) || null == data) {
            throw new BaseException("flutterPayInWebhook error");
        }

        String txRef = null;
        String transacationId = data.get("id").toString();
        String status = data.get("status").toString();
        if ("Transfer".equalsIgnoreCase(eventType)) {
            txRef = data.get("reference").toString();
        } else {
            txRef = data.get("tx_ref").toString();
        }
        if (FlutterConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(status)) {
            PayDO payDO = payService.getOrderNo(txRef);
            if (null != payDO) {
                payDO.setThirdNo(transacationId);
                payDO.setThirdStatus(status);
                payService.update(payDO);
                if ("Transfer".equalsIgnoreCase(eventType)) {
                    payService.payoutSuccess(payDO, PaymentServiceEnum.FLUTTER_WAVE.getBeanName());
                } else {
                    payService.getByOrderNo(payDO.getOrderNo());
                }
                return "SUCCESS";
            }
        } else {
            throw new BaseException("transcation not successful");
        }
        return "FAIL";
    }

    @ApiOperation("wallyt ????????????")
    @PostMapping("/wallyt/payOut")
    public String wallytPayoutCallBack(@RequestBody Map<String, Object> requestJson) {
        logger.info("wallyt ???????????????" + requestJson);
        String jsonStr = com.alibaba.fastjson.JSONObject.toJSONString(requestJson);
        WallytCallbackReq wallytCallbackReq = com.alibaba.fastjson.JSONObject.parseObject(jsonStr, WallytCallbackReq.class);
        List list = wallytCallbackReq.getRequest();
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(list.get(0).toString());
        CreateTransferNotifyReq createTransferNotifyReq = jsonObject.toJavaObject(CreateTransferNotifyReq.class);
        com.alibaba.fastjson.JSONObject result = new com.alibaba.fastjson.JSONObject();
        WallyBaseResponseObject wallyBaseResponseObject = new WallyBaseResponseObject();
        result.put("response", wallyBaseResponseObject);
        String orderNo = createTransferNotifyReq.getOriginalMsgId();
        PayDO payDO = payService.getOrderNo(orderNo);
        if (payDO == null){
            wallyBaseResponseObject.setCode("500");
            wallyBaseResponseObject.setMessage("order is not exist");
            return result.toJSONString();
        }
        logger.info("wallyt ????????????,??????ID--{}???{}" ,payDO.getUserId(), createTransferNotifyReq);
        payDO.setThirdStatus(createTransferNotifyReq.getTrxStatus());
        payDO.setThirdNo(orderNo);
        if (WallytConst.TRANSFER_SUCCESS.equalsIgnoreCase(createTransferNotifyReq.getTrxStatus())){
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO, PaymentServiceEnum.WALLYT.getBeanName());
        }  else if (WallytConst.TRANSFER_INIT.equalsIgnoreCase(createTransferNotifyReq.getTrxStatus())
                || WallytConst.TRANSFER_PENDING.equalsIgnoreCase(createTransferNotifyReq.getTrxStatus())) {
            //do noting
        } else {
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO, PaymentServiceEnum.WALLYT.getBeanName());
        }
        wallyBaseResponseObject.setCode("200");
        wallyBaseResponseObject.setMessage("Success");
        return result.toJSONString();
    }

    @ApiOperation("grecash ????????????")
    @PostMapping("/grecash/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String grecashPayInCallBack(@RequestBody GrecashPayInCallBack grecashPayInCallBack) {
        logger.info("grecash ???????????????grecashPayInCallBack : {}", grecashPayInCallBack);
        PayDO payDO = payService.getOrderNo(grecashPayInCallBack.getMerchantOrderId());
        if (null != payDO && !payDO.getStatus().equals(PayEnums.PayStatusEnum.PAID.getCode())) {
            payDO.setThirdNo(grecashPayInCallBack.getPayorderId());
            payDO.setThirdStatus(grecashPayInCallBack.getStatus().toString());
            if (GrecashConst.PAY_STATUS_1 == grecashPayInCallBack.getStatus()) {
                payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else if (GrecashConst.PAY_STATUS_0 == grecashPayInCallBack.getStatus()) {
                //do nothing
            } else {
                payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
            payService.update(payDO);
            payService.payInComplete(payDO);
            return "SUCCESS";
        }
        return "FAIL";
    }

    @ApiOperation("grecash ????????????")
    @PostMapping("/grecash/payOut")
    public String grecashPayoutCallBack(@RequestBody Map<String, Object> requestJson) {
        String jsonStr = com.alibaba.fastjson.JSONObject.toJSONString(requestJson);
        logger.info("grecash ???????????????" + jsonStr);
        PayOutNotifyReq payOutNotifyReq = com.alibaba.fastjson.JSONObject.parseObject(jsonStr, PayOutNotifyReq.class);
        PayDO payDO = payService.getOrderNo(payOutNotifyReq.getReferenceID());
        if (payDO == null){
            return "order is not exist";
        }
        logger.info("grecash ????????????,??????ID--{}???{}" ,payDO.getUserId(), payOutNotifyReq);
        payDO.setThirdStatus(payOutNotifyReq.getStatus());
        payDO.setThirdNo(payOutNotifyReq.getId());
        if (GrecashConst.PAY_OUT_NOTIFY_STATUS_1.equalsIgnoreCase(payOutNotifyReq.getStatus())){
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO, PaymentServiceEnum.WALLYT.getBeanName());
        }  else if (GrecashConst.PAY_OUT_NOTIFY_STATUS_0.equalsIgnoreCase(payOutNotifyReq.getStatus())) {
            //do noting
        } else {
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO, PaymentServiceEnum.WALLYT.getBeanName());
        }
        return "Success";
    }

    @ApiOperation("gms ????????????")
    @PostMapping("/gms/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String gmsPayInCallBack(GmsPayInNotify gmsPayInNotify) {
        logger.info("gms ???????????????gmsPayInCallBack : {}", gmsPayInNotify);
        PayDO payDO = payService.getOrderNo(gmsPayInNotify.getMchOrderNo());
        if (null != payDO) {
            payDO.setThirdNo(gmsPayInNotify.getOrderNo());
            payDO.setThirdStatus(gmsPayInNotify.getTradeResult());
            if (GMSConst.TRADE_RES_1.equalsIgnoreCase(gmsPayInNotify.getTradeResult())) {
                payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else {
                payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
            payService.update(payDO);
            payService.payInComplete(payDO);
            return "success";
        }
        return "FAIL";
    }

    @ApiOperation("gms ????????????")
    @PostMapping("/gms/payOut")
    public String gmsPayoutCallBack(GmsPayOutNotify gmsPayOutNotify) {
        logger.info("gms ???????????????{}", gmsPayOutNotify);
        PayDO payDO = payService.getOrderNo(gmsPayOutNotify.getMerTransferId());
        if (payDO == null){
            return "order is not exist";
        }
        logger.info("gms ????????????,??????ID--{}???{}" ,payDO.getUserId(), gmsPayOutNotify);
        payDO.setThirdStatus(gmsPayOutNotify.getTradeResult());
        payDO.setThirdNo(gmsPayOutNotify.getTradeNo());
        if (GMSConst.TRADE_RES_1.equalsIgnoreCase(gmsPayOutNotify.getTradeResult())){
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO, PaymentServiceEnum.GMS.getBeanName());
        }  else if (GMSConst.TRADE_RES_0.equalsIgnoreCase(gmsPayOutNotify.getTradeResult())
                || GMSConst.TRADE_RES_4.equalsIgnoreCase(gmsPayOutNotify.getTradeResult())) {
            //do noting
        } else {
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO, PaymentServiceEnum.GMS.getBeanName());
        }
        return "success";
    }

    @ApiOperation("sepro ????????????")
    @PostMapping("/sepro/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String seproPayInCallBack(SeproPayInNotify seproPayInNotify) {
        logger.info("sepro ???????????????seproPayInCallBack : {}", seproPayInNotify);
        PayDO payDO = payService.getOrderNo(seproPayInNotify.getMchOrderNo());
        if (null != payDO) {
            payDO.setThirdNo(seproPayInNotify.getOrderNo());
            payDO.setThirdStatus(seproPayInNotify.getTradeResult());
            if (SeproConst.TRADE_RES_1.equalsIgnoreCase(seproPayInNotify.getTradeResult())) {
                payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else {
                payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
            payService.update(payDO);
            payService.payInComplete(payDO);
            return "success";
        }
        return "FAIL";
    }

    @ApiOperation("sepro ????????????")
    @PostMapping("/sepro/payOut")
    public String seproPayoutCallBack(SeproPayOutNotify seproPayOutNotify) {
        logger.info("sepro ???????????????{}", seproPayOutNotify);
        PayDO payDO = payService.getOrderNo(seproPayOutNotify.getMerTransferId());
        if (payDO == null){
            return "order is not exist";
        }
        logger.info("sepro ????????????,??????ID--{}???{}" ,payDO.getUserId(), seproPayOutNotify);
        payDO.setThirdStatus(seproPayOutNotify.getTradeResult());
        payDO.setThirdNo(seproPayOutNotify.getTradeNo());
        if (SeproConst.TRADE_RES_1.equalsIgnoreCase(seproPayOutNotify.getTradeResult())){
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO, PaymentServiceEnum.SEPRO.getBeanName());
        }  else if (SeproConst.TRADE_RES_0.equalsIgnoreCase(seproPayOutNotify.getTradeResult())
                || SeproConst.TRADE_RES_4.equalsIgnoreCase(seproPayOutNotify.getTradeResult())) {
            //do noting
        } else {
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO, PaymentServiceEnum.SEPRO.getBeanName());
        }
        return "success";
    }


    @ApiOperation("thkingz ????????????")
    @PostMapping("/thkingz/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String thkingzPayInCallBack(ThkingzPayInCallback thkingzPayInCallback) {
        logger.info("thkingz ???????????????thkingzPayInCallBack : {}", thkingzPayInCallback);
        PayDO payDO = payService.getOrderNo(thkingzPayInCallback.getOut_trade_no());
        if (null != payDO) {
            payService.getByOrderNo(payDO.getOrderNo());
            return "SUCCESS";
        }
        return "FAIL";
    }

    @ApiOperation("thkingz ????????????")
    @PostMapping("/thkingz/payOut")
    public String thkingzPayoutCallBack(HttpServletRequest request) {
        request.getParameterMap().forEach((k, v) -> logger.info("k:" + k + ":" + Arrays.toString(v)));
        ThkingzPayOutCallback thkingzBaseRes = new ThkingzPayOutCallback();
        thkingzBaseRes.setCode(request.getParameter("code"));
        thkingzBaseRes.setMsg(request.getParameter("msg"));

        ThkingzCreatePayOutRes thkingzCreatePayOutRes = new ThkingzCreatePayOutRes();
        thkingzCreatePayOutRes.setAppid(request.getParameter("data[appid]"));
        thkingzCreatePayOutRes.setOrder_no(request.getParameter("data[order_no]"));
        thkingzCreatePayOutRes.setOut_trade_no(request.getParameter("data[out_trade_no]"));
        thkingzCreatePayOutRes.setAccount(request.getParameter("data[account]"));
        thkingzCreatePayOutRes.setBank_type(request.getParameter("data[bank_type]"));
        thkingzCreatePayOutRes.setMoney(request.getParameter("data[money]"));
        thkingzBaseRes.setData(thkingzCreatePayOutRes);
        logger.info("thkingz ???????????????{}", thkingzBaseRes);
        PayDO payDO = payService.getOrderNo(thkingzCreatePayOutRes.getOut_trade_no());
        if (payDO == null){
            return "order is not exist";
        }
        logger.info("thkingz ????????????,??????ID--{}???{}" ,payDO.getUserId(), thkingzCreatePayOutRes);
        payService.getByPayOutOrder(payDO.getOrderNo());
        return "success";
    }

    @ApiOperation("qea ????????????")
    @PostMapping("/qea/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String qeaPayInCallBack(QeaPayInNotify qeaPayInNotify) {
        logger.info("qea ???????????????qeaPayInCallBack : {}", qeaPayInNotify);
        PayDO payDO = payService.getOrderNo(qeaPayInNotify.getMchOrderNo());
        if (null != payDO) {
            payDO.setThirdNo(qeaPayInNotify.getOrderNo());
            payDO.setThirdStatus(qeaPayInNotify.getTradeResult());
            if (QeaPayConst.TRADE_RES_1.equalsIgnoreCase(qeaPayInNotify.getTradeResult())) {
                payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            } else {
                payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            }
            payService.update(payDO);
            payService.payInComplete(payDO);
            return "success";
        }
        return "FAIL";
    }

    @ApiOperation("qea ????????????")
    @PostMapping("/qea/payOut")
    public String qeaPayoutCallBack(QeaPayOutNotify qeaPayOutNotify) {
        logger.info("qea ???????????????{}", qeaPayOutNotify);
        PayDO payDO = payService.getOrderNo(qeaPayOutNotify.getMerTransferId());
        if (payDO == null){
            return "order is not exist";
        }
        logger.info("qea ????????????,??????ID--{}???{}" ,payDO.getUserId(), qeaPayOutNotify);
        payDO.setThirdStatus(qeaPayOutNotify.getTradeResult());
        payDO.setThirdNo(qeaPayOutNotify.getTradeNo());
        if (QeaPayConst.TRADE_RES_1.equalsIgnoreCase(qeaPayOutNotify.getTradeResult())){
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
            payService.payoutSuccess(payDO, PaymentServiceEnum.QEAPAY.getBeanName());
        }  else if (GMSConst.TRADE_RES_0.equalsIgnoreCase(qeaPayOutNotify.getTradeResult())
                || GMSConst.TRADE_RES_4.equalsIgnoreCase(qeaPayOutNotify.getTradeResult())) {
            //do noting
        } else {
            //??????
            payDO.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
            payService.payoutFail(payDO, PaymentServiceEnum.QEAPAY.getBeanName());
        }
        return "success";
    }

    @ApiOperation("happyLife ????????????")
    @PostMapping("/happyLife/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String happyLifePayInCallBack(HappyLifeQueryPayDetailRes happyLifeQueryPayDetailRes) {
        logger.info("happyLife ???????????????happyLifePayInCallBack : {}", happyLifeQueryPayDetailRes);
        PayDO payDO = payService.getOrderNo(happyLifeQueryPayDetailRes.getMer_order_no());
        if (null != payDO) {
            payService.getByOrderNo(payDO.getOrderNo());
            return "SUCCESS";
        }
        return "FAIL";
    }

    @ApiOperation("happyLife ????????????")
    @PostMapping("/happyLife/payOut")
    public String happyLifePayoutCallBack(HappyLifeQueryPayOutDetailRes happyLifeQueryPayOutDetailRes) {
        logger.info("happyLife ???????????????{}", happyLifeQueryPayOutDetailRes);
        PayDO payDO = payService.getOrderNo(happyLifeQueryPayOutDetailRes.getOrder_no());
        if (payDO == null){
            return "order is not exist";
        }
        logger.info("happyLife ????????????,??????ID--{}???{}" ,payDO.getUserId(), happyLifeQueryPayOutDetailRes);
        payService.getByPayOutOrder(happyLifeQueryPayOutDetailRes.getOrder_no());
        return "SUCCESS";
    }

    @ApiOperation("momo ????????????")
    @PostMapping("/momo/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String momoPayInCallBack(@RequestBody RequestToPay requestToPay) {
        logger.info("momo ???????????????momoPayInCallBack : {}", com.alibaba.fastjson.JSONObject.toJSONString(requestToPay));
        PayDO payDO = payService.getOrderNo(requestToPay.getExternalId());
        if (null != payDO) {
            payService.getByOrderNo(requestToPay.getExternalId());
            return "SUCCESS";
        }
        return "FAIL";
    }

    @ApiOperation("momo ????????????")
    @PostMapping("/momo/payOut")
    public String momoPayoutCallBack(@RequestBody Transfer transfer) {
        logger.info("momo ???????????????{}", com.alibaba.fastjson.JSONObject.toJSONString(transfer));
        PayDO payDO = payService.getOrderNo(transfer.getExternalId());
        if (payDO == null){
            return "order is not exist";
        }
        logger.info("momo ????????????,??????ID--{}???{}" ,payDO.getUserId(), transfer);
        payService.getByPayOutOrder(transfer.getExternalId());
        return "SUCCESS";
    }


    @ApiOperation("theteller ????????????")
    @RequestMapping("/theteller/payIn")
    @Transactional(rollbackFor = Exception.class)
    public void thetellerPayInCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //https://redirect_url?code=000&status=successful&reason=transaction20%successful&transaction_id=000000000000
        String transactionId = request.getParameter("transaction_id");
        logger.info("theteller ???????????????thetellerPayInCallBack : {}", transactionId);
        if (StringUtils.isNotBlank(transactionId)) {
            PayDO payDO = payService.getByOutNo(transactionId);
            if (null != payDO) {
                payService.getByOrderNo(payDO.getOrderNo());
            }
        }
        response.sendRedirect(RedisUtils.getString(DictConsts.MOMO_COLLECTION_REDIRECT_URL));
    }

    @ApiOperation("offline ????????????")
    @RequestMapping("/offline/payIn")
    @Transactional(rollbackFor = Exception.class)
    public String offlinePayInCallBack(HttpServletRequest request, HttpServletResponse response) {
        //https://redirect_url?code=000&status=successful&reason=transaction20%successful&transaction_id=000000000000
        String notifyStr = request.getParameter("notifyStr");
        String phone = request.getParameter("phone");
        logger.info("offline ???????????????offlinePayInCallBack : {}, phone:{}", notifyStr, phone);
        //??????????????????
        commonExecutor.execute(() -> {
            logger.info("offline payIn Request:{}", notifyStr);
            HttpRequest httpRequest = HttpUtil.createPost(RedisUtils.getString(DictConsts.OFFLINE_PAY_FORWARD_URL));
            httpRequest.body(notifyStr);
            HttpResponse httpResponse = httpRequest.execute();
            logger.info("offline payIn Response:{}", httpResponse.body());
        });
        if (StringUtils.isNotBlank(notifyStr)) {
            payNotifyService.checkAndSaveNotify(notifyStr, phone);
            return "success";
        }
        return "fail";
    }


}
