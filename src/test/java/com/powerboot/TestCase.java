package com.powerboot;

import com.google.common.collect.Maps;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.Configuration;
import com.infobip.api.SendSmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsResponse;
import com.infobip.model.SmsTextualMessage;
import com.powerboot.config.BaseException;
import com.powerboot.consts.I18nEnum;
import com.powerboot.controller.OrderController;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.PayEnums;
import com.powerboot.job.PaymentTimeoutJob;
import com.powerboot.job.SummaryTableJob;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.service.ProductService;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;
import com.powerboot.utils.StringUtils;
import com.powerboot.utils.flutter.constants.FlutterConts;
import com.powerboot.utils.flutter.core.FlutterPayment;
import com.powerboot.utils.gms.core.GMSClient;
import com.powerboot.utils.gms.model.GmsCreatePayOutRes;
import com.powerboot.utils.gms.model.GmsCreatePayRes;
import com.powerboot.utils.grecash.core.GrecashClient;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.CreatePayRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.infobip.utils.VoiceMessageSendUtil;
import com.powerboot.utils.paystack.core.PaystackInline;
import com.powerboot.utils.wallyt.core.WallytClient;
import lombok.SneakyThrows;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
public class TestCase {

    @Autowired
    private FlutterPayment flutterPayment;

    @Autowired
    private SummaryTableJob summaryTableJob;

    @Autowired
    private PaymentTimeoutJob paymentTimeoutJob;

    @Autowired
    private PaystackInline paystackInline;

    @Autowired
    private WallytClient wallytClient;

    @Autowired
    private OrderController orderController;

    @Autowired
    private GrecashClient grecashClient;

    @Autowired
    private GMSClient gmsClient;

    @Autowired
    private ProductService productService;

//    @Test
//    public void test01() {
//        PaymentResult paymentResult = new PaymentResult();
//        com.alibaba.fastjson.JSONObject jsonObject = flutterPayment.getTransactions(null, null, "22027pn1c8ujw", null);
//        if (doCheck(jsonObject)) {
//            com.alibaba.fastjson.JSONArray data = jsonObject.getJSONArray("data");
//            if (null != data) {
//                data.forEach(d -> {
//                    com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) d;
//                    if (StringUtils.isNotBlank(paymentResult.getThirdOrderStatus())
//                            && FlutterConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
//                        return;
//                    }
//                    paymentResult.setThirdOrderAmount(obj.getBigDecimal("amount"));
//                    paymentResult.setDescription(jsonObject.getString("message"));
//                    paymentResult.setThirdOrderStatus(obj.getString("status"));
//                    if (FlutterConts.PAY_STATUS_SUCCESS.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
//                        paymentResult.setStatus(PayEnums.PayStatusEnum.PAID.getCode());
//                    } else if (FlutterConts.PAY_STATUS_PENDING.equalsIgnoreCase(paymentResult.getThirdOrderStatus())) {
//                        paymentResult.setStatus(PayEnums.PayStatusEnum.PAYING.getCode());
//                    } else {
//                        paymentResult.setStatus(PayEnums.PayStatusEnum.FAIL.getCode());
//                    }
//                });
//            }
//        }
//        System.out.println(paymentResult);
//    }
//
//    private Boolean doCheck(com.alibaba.fastjson.JSONObject jsonObject) {
//        Boolean check = false;
//        String status = jsonObject.getString("status");
//        if ("success".equalsIgnoreCase(status)) {
//            check = true;
//        } else {
//            throw new BaseException("The payment system is being upgraded, please wait for 1 hour");
//        }
//        return check;
//    }

    @Autowired
    private VoiceMessageSendUtil voiceMessageSendUtil;

    @Test
    public void test2() throws IOException {
//        System.out.println(paystackInline.initiateTransfer("testcaseferfern2",
//                "100", "RCP_73e8fa3lxfm18zj"));
//        System.out.println(wallytClient.createTransfer("testOrder1223", BigDecimal.TEN,
//                "测试转账2", "t name", "0058251101", "Access Bank"));
        System.out.println(wallytClient.queryTransfer("21904ocsqs4fthird", "21904ocsqs4f"));
        System.in.read();
    }

    @Test
    public void test03() throws IOException {
//        BaseGrecashRes res = grecashClient.createPay("testOrder2626",
//                BigDecimal.TEN, "tufeng1992@sina.com", "18101656358");
//        System.out.println(res);
//        System.out.println(res.getPayorder());
        BaseGrecashRes<QueryPayRes> res = grecashClient.queryPay("P202114419403000002");
        System.out.println(res);
//        System.out.println(grecashClient.createTransfer("transferTest", BigDecimal.TEN,
//                "18101656358", "tufeng1992@sina.com",
//                "t name", "0058251101", "057"));
        System.in.read();
    }

    @Test
    public void test04() throws IOException {
            System.out.println("---------------------------------");
//            System.out.println(I18nEnum.TRY_AGAIN.getMsg());
        System.out.println(productService.list(Maps.newHashMap()));
        System.in.read();
    }

    @Test
    public void test05() throws IOException {
        GmsCreatePayRes res = gmsClient.createPay("testOrder001", BigDecimal.valueOf(100), "TMB");
//        GmsCreatePayOutRes res = gmsClient.createTransfer("testTransferOrder01", BigDecimal.valueOf(100),
//                "testName", "123456123", "TMB");
        System.out.println(res);
        System.in.read();
    }

    @SneakyThrows
    public static void main(String[] args) {

    }

}
