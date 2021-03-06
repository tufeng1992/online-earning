package com.powerboot;

import ci.bamba.regis.models.RequestToPay;
import ci.bamba.regis.models.Transfer;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.base.BaseResponse;
import com.powerboot.common.StringCommonUtils;
import com.powerboot.config.SmsSendConfig;
import com.powerboot.consts.DictConsts;
import com.powerboot.consts.I18nEnum;
import com.powerboot.controller.OrderController;
import com.powerboot.dao.BalanceDao;
import com.powerboot.dao.MemberInfoDao;
import com.powerboot.dao.SmsSendResponse;
import com.powerboot.dao.UserDao;
import com.powerboot.domain.BalanceDO;
import com.powerboot.domain.UserDO;
import com.powerboot.enums.BalanceTypeEnum;
import com.powerboot.enums.StatusTypeEnum;
import com.powerboot.job.PaymentTimeoutJob;
import com.powerboot.job.SummaryTableJob;
import com.powerboot.response.pay.PaymentResult;
import com.powerboot.service.*;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringRandom;
import com.powerboot.utils.adjustevent.core.AdjustEventClient;
import com.powerboot.utils.flutter.core.FlutterPayment;
import com.powerboot.utils.gms.core.GMSClient;
import com.powerboot.utils.grecash.core.GrecashClient;
import com.powerboot.utils.grecash.model.BaseGrecashRes;
import com.powerboot.utils.grecash.model.QueryPayRes;
import com.powerboot.utils.happylife.core.HappyLifeClient;
import com.powerboot.utils.happylife.model.HappyLifePayOutRes;
import com.powerboot.utils.infobip.utils.InfobMessageSendUtil;
import com.powerboot.utils.momo.core.MoMoClient;
import com.powerboot.utils.payful.core.PayfulClient;
import com.powerboot.utils.paystack.core.PaystackInline;
import com.powerboot.utils.qeapay.core.QeaPayClient;
import com.powerboot.utils.qeapay.domain.QeaPayCreatePayRes;
import com.powerboot.utils.sepro.core.SeproClient;
import com.powerboot.utils.sepro.model.SeproCreatePayRes;
import com.powerboot.utils.theteller.core.ThetellerClient;
import com.powerboot.utils.thkingz.core.ThkingzClient;
import com.powerboot.utils.thkingz.model.ThkingzBaseRes;
import com.powerboot.utils.thkingz.model.ThkingzCreatePayRes;
import com.powerboot.utils.wallyt.core.WallytClient;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
//import ug.sparkpl.momoapi.network.MomoApiException;
//import ug.sparkpl.momoapi.network.RequestOptions;
//import ug.sparkpl.momoapi.network.collections.CollectionsClient;

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

    @Autowired
    private UserTaskOrderMissionService userTaskOrderMissionService;

    @Autowired
    private HappyLifeClient happyLifeClient;

    @Autowired
    private MoMoClient moMoClient;

    @Autowired
    private ThetellerClient thetellerClient;

    @Test
    public void test01() throws IOException {
        PaymentResult paymentResult = new PaymentResult();
        com.alibaba.fastjson.JSONObject jsonObject = flutterPayment.createTransfer("testOrder9991", "1",
                "0546093073", "testName", "MTN", "testName");
        System.out.println(jsonObject.toJSONString());
        System.in.read();
    }
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
    private InfobMessageSendUtil infobMessageSendUtil;

    @Autowired
    private SmsSendConfig smsSendConfig;

    @Test
    public void test2() throws IOException {
//        System.out.println(paystackInline.initiateTransfer("testcaseferfern4",
//                "164.02", "RCP_mtdwbluu7emcurn"));
//        System.out.println(paystackInline.verifyTransfer("testcaseferfern2"));
//        System.out.println(paystackInline.selectBankList("ghana"));
//        System.out.println(paystackInline.createTransferRecipient("ZEXX GAMING", "0559239172", "MTN"));
        System.out.println(paystackInline.paystackStandard("testOrder03", new BigDecimal("100.00").intValue(),
                "tufeng1992@sina.com", "", "https://www.baidu.com"));
//        System.out.println(wallytClient.createTransfer("testOrder1223", BigDecimal.TEN,
//                "????????????2", "t name", "0058251101", "Access Bank"));
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

    @Test
    public void test11() throws IOException {
        userTaskOrderMissionService.addMissionLog(646L, 10, new BigDecimal("1001"));
        System.in.read();
    }

    @Test
    public void test12() throws IOException {
        String verCode = StringRandom.getStringRandom(6);
        String msg = StringCommonUtils.buildString("[TaskP] your verification code is {}. To ensure information security, please do not tell others.", verCode);
        BaseResponse<SmsSendResponse> resp = smsSendConfig.sendKenya("233112233445", msg);
        System.out.println(resp);
        System.in.read();
    }

    @Test
    public void test13() throws IOException {
//        System.out.println(happyLifeClient.createPay("testOrder50", new BigDecimal("50"), 123L));
//        System.out.println(happyLifeClient.queryPay("2267prpctf5f"));
        System.out.println(happyLifeClient.createTransfer("testTransfer02", new BigDecimal("150"), "Shadrack",
                "0546093073", "MTN", 123L));
//        System.out.println(JSONObject.parseObject("{\"msg\":\"SUCCESS\",\"data\":{\"identify\":\"Beeearning897\",\"order_no\":\"361oajmcwckb\",\"platform_no\":\"2021080817151854515352\",\"money\":164,\"status\":\"pending\",\"add_time\":1628442918},\"code\":0}", HappyLifePayOutRes.class));
        System.in.read();
    }

    @Test
    public void test14() throws IOException {
//        System.out.println(moMoClient.createPay("test14OrderC1", BigDecimal.TEN, "0022505777777"));
//        System.out.println(moMoClient.queryPay("bf01bc01-fa2f-4548-9436-af5f39ff2256"));
        System.out.println(moMoClient.createTransfer("test14OrderT2", "0022505777777", BigDecimal.TEN));
//        System.out.println(moMoClient.queryTransfer("260a60a1-602f-4ef9-a55e-be10f19acc56"));
        System.in.read();
    }

    @Test
    public void test15() throws IOException {
//        System.out.println(thetellerClient.createPay(new BigDecimal("5"), "tufeng1992@sina.com"));
//        System.out.println(thetellerClient.queryPay("294910040519"));
        System.out.println(thetellerClient.createTransfer("testOrder99", new BigDecimal("1"),
                "0546093073", "MTN"));
        System.in.read();
    }

    @Autowired
    private PayfulClient payfulClient;

    @Test
    public void test16() throws IOException {
        System.out.println(payfulClient.createPay("test03", new BigDecimal("5")));
//        System.out.println(RandomUtil.randomString(100));
        System.in.read();
    }

    @Autowired
    private PayNotifyService payNotifyService;

    @Test
    public void test17() throws IOException {
        payNotifyService.checkAndSaveNotify("test", "123");
//        System.out.println(RandomUtil.randomString(100));
        System.in.read();
    }

    @Test
    public void test18() throws IOException {
        System.out.println(RedisUtils.getHash(DictConsts.USER_INVITE_FLAG_HALF, "20"));
        System.out.println(RedisUtils.getHash(DictConsts.USER_INVITE_FLAG_HALF, "22"));
        System.in.read();
    }

    @Autowired
    private EhcacheService ehcacheService;

    @Test
    public void test19() throws IOException {
        System.out.println(ehcacheService.getVipInfo());
        System.out.println(ehcacheService.getVipInfo());
        HashMap<Integer, List<Integer>> m = ehcacheService.getVipInfo();
        System.out.println(m.get(1));

        System.in.read();
    }

    @SneakyThrows
    public static void main(String[] args) {
        String json = "{\"amount\":\"10.0\",\"currency\":\"EUR\",\"financialTransactionId\":\"1628814832\",\"externalId\":\"test14Order26\",\"payee\":{\"partyIdType\":\"MSISDN\",\"partyId\":\"0022505777777\"},\"payeeNote\":\"Transfer to user: Merchant service fee\",\"status\":\"SUCCESSFUL\"}";

//        ci.bamba.regis.models.Transfer requestToPay = JSONObject.parseObject(json, Transfer.class);
//        System.out.println(requestToPay);
        System.out.println(new BigDecimal("99").abs());

//        RequestOptions opts = RequestOptions.builder()
//                .setCollectionApiSecret("665ca5dc1d8542249e22c2eb31e406aa")
//                .setCollectionPrimaryKey("51fa5f92859c4632bddde72aef94d03c")
//                .setCollectionUserId("37582505-f2f0-4b84-a02a-6a5c332521e5")
//                .setBaseUrl("https://sandbox.momodeveloper.mtn.com")  // Override the default base url
//                .setCurrency("GHS") // Override default currency
//                .setTargetEnvironment("sandbox").build();// Override default target environment
//        // Make a request to pay call
////        RequestOptions opts = RequestOptions.builder()
////                .setCollectionApiSecret("MY_SECRET_API_KEY")
////                .setCollectionPrimaryKey("MY_SECRET_SUBSCRIPTION_KEY")
////                .setCollectionUserId("MYSECRET_USER_ID").build();
//
//        HashMap<String, String> collMap = new HashMap<String, String>();
//        collMap.put("amount", "100");
//        collMap.put("mobile", "1234");
//        collMap.put("externalId", "ext123");
//        collMap.put("payeeNote", "testNote");
//        collMap.put("payerMessage", "testMessage");
//        CollectionsClient client = new CollectionsClient(opts);
//        try {
//            String transactionRef = client.requestToPay(collMap);
//            System.out.println(transactionRef);
//        } catch (MomoApiException e) {
//            e.printStackTrace();
//        }
        System.in.read();
    }

}
