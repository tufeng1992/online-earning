package com.powerboot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.google.common.collect.Maps;
import com.infobip.ApiClient;
import com.infobip.ApiException;
import com.infobip.Configuration;
import com.infobip.api.SendSmsApi;
import com.infobip.model.SmsAdvancedTextualRequest;
import com.infobip.model.SmsDestination;
import com.infobip.model.SmsResponse;
import com.infobip.model.SmsTextualMessage;
import com.powerboot.base.BaseResponse;
import com.powerboot.config.BaseException;
import com.powerboot.config.SmsSendConfig;
import com.powerboot.consts.I18nEnum;
import com.powerboot.controller.OrderController;
import com.powerboot.dao.BalanceDao;
import com.powerboot.dao.MemberInfoDao;
import com.powerboot.dao.UserDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.MemberInfoDO;
import com.powerboot.domain.PayDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.PayEnums;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.job.PaymentTimeoutJob;
import com.powerboot.job.SummaryTableJob;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.service.PayService;
import com.powerboot.service.ProductService;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;
import com.powerboot.utils.StringUtils;
import com.powerboot.utils.adjustevent.core.AdjustEventClient;
import com.powerboot.utils.flutter.constants.FlutterConts;
import com.powerboot.utils.flutter.core.FlutterPayment;
import com.powerboot.utils.gms.core.GMSClient;
import com.powerboot.utils.gms.model.GmsCreatePayOutRes;
import com.powerboot.utils.gms.model.GmsCreatePayRes;
import com.powerboot.utils.grecash.core.GrecashClient;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.CreatePayOutRes;
import com.powerboot.utils.grecash.model.CreatePayRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.infobip.utils.VoiceMessageSendUtil;
import com.powerboot.utils.paystack.core.PaystackInline;
import com.powerboot.utils.qeapay.core.QeaPayClient;
import com.powerboot.utils.qeapay.domain.QeaPayCreatePayRes;
import com.powerboot.utils.sepro.core.SeproClient;
import com.powerboot.utils.sepro.model.SeproCreatePayRes;
import com.powerboot.utils.thkingz.core.ThkingzClient;
import com.powerboot.utils.thkingz.model.ThkingzBaseRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayOutRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayRes;
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
import java.util.*;

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
    private PayService payService;

    @Autowired
    private WallytClient wallytClient;

    @Autowired
    private OrderController orderController;

    @Autowired
    private GrecashClient grecashClient;

    @Autowired
    private GMSClient gmsClient;

    @Autowired
    private SeproClient seproClient;

    @Autowired
    private ThkingzClient thkingzClient;

    @Autowired
    private ProductService productService;

    @Autowired
    private BalanceDao balanceDao;

    @Autowired
    private MemberInfoDao memberInfoDao;

    @Autowired
    private AdjustEventClient adjustEventClient;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QeaPayClient qeaPayClient;

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

    @Autowired
    private SmsSendConfig smsSendConfig;

    @Test
    public void test2() throws IOException {
//        System.out.println(paystackInline.initiateTransfer("testcaseferfern2",
//                "1", "RCP_nradnsju5o8ovgx"));
        System.out.println(paystackInline.verifyTransfer("testcaseferfern2"));
//        System.out.println(paystackInline.selectBankList("ghana"));
//        System.out.println(paystackInline.createTransferRecipient("ZEXX GAMING", "0559239172", "MTN"));
//        System.out.println(paystackInline.paystackStandard("testOrder02", 10000,
//                "tufeng1992@sina.com", "", "https://www.baidu.com"));
//        System.out.println(wallytClient.createTransfer("testOrder1223", BigDecimal.TEN,
//                "测试转账2", "t name", "0058251101", "Access Bank"));
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
//        GmsCreatePayRes res = gmsClient.createPay("testOrder001", BigDecimal.valueOf(100), "TMB");
//        GmsCreatePayOutRes res = gmsClient.createTransfer("testTransferOrder01", BigDecimal.valueOf(100),
//                "testName", "123456123", "TMB");
//        System.out.println(res);
        UserDO userDO = new UserDO();
        userDO.setId(21884L);
        userDO.setParentId(21884l);
//        payService.addParentBalance(1, userDO, new Date(), "testOrdert");
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userDO.getId());
        map.put("type", BalanceTypeEnum.F.getCode());
        map.put("status", StatusTypeEnum.SUCCESS.getCode());
        List<BalanceDO> balanceDOList = balanceDao.list(map);
        System.out.println(balanceDOList.stream().map(BalanceDO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        System.in.read();
    }

    @Test
    public void test06() throws IOException {
        SeproCreatePayRes res = seproClient.createPay("testOrder004", BigDecimal.valueOf(100), "TMB");
//        GmsCreatePayOutRes res = gmsClient.createTransfer("testTransferOrder01", BigDecimal.valueOf(100),
//                "testName", "123456123", "TMB");
        System.out.println(res);
    }


    @Test
    public void test07() throws IOException {
        ThkingzBaseRes res = thkingzClient.createPay("testOrder008", BigDecimal.valueOf(100), "test111");
        System.out.println(res);
        System.out.println(res.getUrl());
        ThkingzCreatePayRes data = ((com.alibaba.fastjson.JSONObject) res.getData()).toJavaObject(ThkingzCreatePayRes.class);
        System.out.println(data);
    }

    @Test
    public void test08() throws IOException {
//        SeproCreatePayRes res = thkingzClient.createPay("testOrder003", BigDecimal.valueOf(100), "test111");
//        ThkingzBaseRes res = thkingzClient.queryPay("33pxjdqjwdma");
//        System.out.println(res);
//        JSONArray jsonArray = (JSONArray) res.getData();
//        ThkingzCreatePayRes createPayRes = ((JSONObject)jsonArray.get(0)).toJavaObject(ThkingzCreatePayRes.class);
//        thkingzClient.createTransfer("testTransferOrder02", BigDecimal.valueOf(100),
//                "testName", "123456123", "TMB");
//        MemberInfoDO m = new MemberInfoDO();
//        m.setType(2);
//        MemberInfoDO memberInfoDO = memberInfoDao.selectOne(m);
//        System.out.println(memberInfoDO);
        System.out.println(BaseResponse.fail(I18nEnum.PAY_BIND_CARD_FAIL.getCode(), I18nEnum.PAY_BIND_CARD_FAIL.getMsg()));
    }

    @Test
    public void test09() throws IOException {
        adjustEventClient.vipSuccess("9624782f34f274da63fa0a47ad1685de");
        adjustEventClient.rechargeSuccess("9624782f34f274da63fa0a47ad1685de");
        System.in.read();
    }

    @Test
    public void test10() throws IOException {
        QeaPayCreatePayRes res = qeaPayClient.createPay("testorder2", BigDecimal.TEN, "");
        System.out.println(res);
        System.in.read();
    }




    @SneakyThrows
    public static void main(String[] args) {
        String json = "{\"code\":1000,\"info\":\"请求成功\",\"result\":{\"id\":\"PO202116519020200001\"," +
                "\"merchantId\":\"de5ad6ec-0fa9-4580-9940-f2eb2304f784\",\"merchantPayoutId\":\"159ocwf2s4dm\"," +
                "\"bizType\":2,\"amount\":1646.56,\"channelId\":\"5\",\"channelPayoutId\":null,\"countryCode\":\"GS\"," +
                "\"currency\":\"ZAR\",\"payType\":2,\"rate\":0.0500,\"singleCharge\":10.0000," +
                "\"callBack\":\"https://www.fixmyptwallet.com/pay/callback/grecash/payOut\"," +
                "\"deductionMethod\":1,\"status\":4,\"approver\":\"System Auto\",\"comment\":null," +
                "\"createTime\":\"2021-06-14 19:02:02\",\"updateTime\":\"2021-06-14 19:02:02\"}}";
        JSONObject j = JSONObject.parseObject(json);
        BaseGrecashRes res = j.toJavaObject(BaseGrecashRes.class);
        CreatePayOutRes createPayOutRes = ((com.alibaba.fastjson.JSONObject) res.getResult()).toJavaObject(CreatePayOutRes.class);

        System.out.println(createPayOutRes);

    }

}
