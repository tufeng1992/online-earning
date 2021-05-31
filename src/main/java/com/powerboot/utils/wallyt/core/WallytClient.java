package com.powerboot.utils.wallyt.core;

import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.opay.HttpUtil;
import com.powerboot.utils.wallyt.constants.WallytConst;
import com.powerboot.utils.wallyt.domain.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class WallytClient {

    private String getBaseUrl() {
        return RedisUtils.getValue(DictConsts.WALLYT_GATEWAY_URL, String.class);
    }

    private String getPartnerId() {
        return RedisUtils.getValue(DictConsts.WALLYT_PARTNER_ID, String.class);
    }

    private String getSignPriK() {
        return RedisUtils.getValue(DictConsts.WALLYT_SIGN_PRIK, String.class);
    }

    private String getPayOutNotifyUrl() {
        return RedisUtils.getValue(DictConsts.WALLYT_PAYOUT_NOTIFY_URL, String.class);
    }

    private String getSignPubK() {
        return RedisUtils.getValue(DictConsts.WALLYT_SIGN_PUBK, String.class);
    }

    private String getSignature(String jsonStr) {
        return WallytRSAUtil.signBySHA256WithRSA(jsonStr, getSignPriK());
    }

    /**
     * 创建转账
     * @param orderNo
     * @param amount
     * @param attach
     * @param name
     * @param accountNumber
     * @param bankName
     * @return
     */
    public WallytResponse<CreateTransferResponse> createTransfer(String orderNo, BigDecimal amount, String attach, String name, String accountNumber, String bankName) {
        JSONObject body = new JSONObject();
        JSONObject request = new JSONObject();
        request.put("msgId", orderNo);
        request.put("partnerId", getPartnerId());
        request.put("reqDateTime", new Date());

        JSONObject receivingAcct = new JSONObject();
        receivingAcct.put("acctNo", accountNumber);
        receivingAcct.put("acctName", name);
        receivingAcct.put("receivingBank", bankName);
        request.put("receivingAcct", receivingAcct);
        request.put("trxAmount", amount);
        request.put("disburseINST", "disburse.nigeria.paystack");
        request.put("notifyUrl", getPayOutNotifyUrl());
        request.put("attach", attach);
        body.put("request", request);
        body.put("signature", getSignature(request.toJSONString()));
        String url = getBaseUrl() + "/v3/disbursement";
        JSONObject j = HttpUtil.post4JsonObj(url, Maps.newHashMap(), body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        WallytResponse<CreateTransferResponse> wallytResponse = new WallytResponse<>();
        wallytResponse.setResponse(j.getObject("response", CreateTransferResponse.class));
        wallytResponse.setSignature(j.getString("signature"));
        return wallytResponse;
    }

    /**
     * 查询转账
     * @param thirdOrderNo
     * @param orderNo
     * @return
     */
    public WallytResponse<QueryTransferResponse> queryTransfer(String thirdOrderNo, String orderNo) {
        JSONObject body = new JSONObject();
        JSONObject request = new JSONObject();
        request.put("msgId", thirdOrderNo);
        request.put("originalMsgId", orderNo);
        request.put("reqDateTime", new Date());
        request.put("partnerId", getPartnerId());
        request.put("disburseINST", "disburse.nigeria.paystack");
        body.put("request", request);
        body.put("signature", getSignature(request.toJSONString()));
        String url = getBaseUrl() + "/v3/disbursement/query/transaction";
        JSONObject j = HttpUtil.post4JsonObj(url, Maps.newHashMap(), body).orElseThrow(() -> new RuntimeException("请求响应为空"));
        WallytResponse<QueryTransferResponse> wallytResponse = new WallytResponse<>();
        wallytResponse.setResponse(j.getObject("response", QueryTransferResponse.class));
        wallytResponse.setSignature(j.getString("signature"));
        return wallytResponse;
    }

    public static void main(String[] args) throws Exception {
//        String pk = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC24ndrKP5/BuCB1ZKsHKqY8PV2FRv9XrkBeArTAdlodSHfeyZ9tceOIM0RZGJJgKh9LPonB7rCCrcYeBS7kAp55DiqhCcvBcpBpCsB5Spf0xAsspFHUcSsK0htJ/OVsjVoW8BuSa16uyt6TTV6cOMsW/xvebk1CG6KZwK+WdEBKQySQSUZXnXa2RUBAvjpA8t2QU3fA2qVGBXY/LhwiMg9XDjtwUrV1l4w2/8BU+QXNJ7rUq6q4cd5e5no8N7/LaxZS3AWGi4pfQas/GNAbVDki0qcnmX9V1B0uDVBYfSjBgJiFo5s4lavO8jEgaYvcvMMb5QBYqIji+rnlncTTF3lAgMBAAECggEAb1tr/WsDs5MbZtMFsRjvY5rsy07FMTzZB6TxalQo7irMSkixQkBKVydv1lQ6aKkXbsjRNKzHMdnWbObJMP/e8SNewa0gMsiiw99TdJ72c0yP/7dnMHcAexkLy7NU5WSSko2zmTPxltaa/ag/lONh4CPOjI4gkMMmk81vZTpJQhuVveVtQR1pjLfE4KbxAB9QqrcBVahQUol/nSUWDluWq/GofIgn8I0Bm7fpzX3x4m3eqFkIUq8GGBZtl/Z5Q73fu8kqjIrFIqR5EA+sVlvpCbv56EDsd7FhWEBOjMWp5O8QZxpUdCZ02LLpe/915+XT2cUOlCSO9BDjGZOy9vBgTQKBgQDjma/pvcYhVCyuYLVIwDhsdo2M0Aa/6l++qi8ouyQpqYDvrU5FjYBeLos2+v494ZNqLfjgpARxxOn3hLPhIEvWaQBmw/Q1ritPe3y3y96pcq1RJpeeyaD1ZhvejepyQDqLn4nqKA9VPdjEURlQaGutQ4qyS1ZcwBTwXFWrholPfwKBgQDNtGknqNfRSEbc9XxzdT4lvh8OGEjwApc5YUK/R/bXe9RUSolN3CmeUCyGh2xvCt+tUaxM6L/Oj68w/ksVoZv/E0hOyxlhLPw6Yap+zHNn8VGIztpZFkEpc051gAWNG71pkbSa4v4SNhrqVcASCWOqm/MTjMCOzO0EmZ48aLTEmwKBgCL32NwrqQYz3Y2JFDEyPzkC2eqZh3/y7XToxsgWQI8aGNI6KT1saR2PPxpaY7d0MebtW0CAUqJ8I4pA3DYpdR1jEom3b4zTzpmumlHOw8367X/7Uu41gkJRxtRhr3SQQ65x+4l7UCfDkctPvnjpIioDp4qPIpyrckwfk5X/3JbXAoGAEn8neXZ4OYxbOuEkGKmxNembl0Pdwx8og/NX8iVBb+zlkdLF9+dVIrQo3MvulymOJWKTsLK5FJTc9vXWwJIwmcYdGWCv3ooSABnqH864jd8oKroW7i3PHDAPHQeu+BpHdOeyKr9Ag1i0IOAnDjgj6yJswBRG+FA6+8/oZ5wurQMCgYEAjFhFY67JfgaTZnzznLrrYX4D79BwPs01oSIXk+2ObxDVH3riXwtBhVNKxRhjCKS/0My6uJl7Kzhh+Dh86uyYgFqwD2kIFcXfKmXHVP757soduyh8iZpQc65UQe232JDQoKUi/lPllZcHantInV7Rw0Rn+EcTvEW1zfI2CCN/HH0=";
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("msgId", "1614155388294");
//        jsonObject.put("partnerId", "100540000048");
//        jsonObject.put("disburseINST", "disburse.nigeria.paystack");
//        jsonObject.put("reqDateTime", "2020-08-24T03:48:10+08:00");
//        System.out.println(jsonObject.toJSONString());
//        System.out.println(WallytRSAUtil.signBySHA256WithRSA(jsonObject.toJSONString(), pk));
//        JSONObject j = JSONObject.parseObject("{\"response\":{\"code\":\"200\",\"message\":\"Success\",\"msgId\":\"21904oy6f8tyt\",\"trxDate\":\"2021-05-09T13:04:27.000Z\",\"trxAmount\":820,\"trxStatus\":\"SUCCESS\",\"originalMsgId\":\"21904oy6f8ty\",\"attach\":\"TaskP:21904oy6f8ty\",\"partnerId\":\"100540000048\",\"currency\":\"NGN\"},\"signature\":\"b8NFDXUZ7yI9XnN3rS3yJ8qt554IN913a2F7OAtZ4incc1GOh4HswnU/0dre6R0TuGLf3snb4sLJWNeSXBU2OpLFELPvRcCdJEABNksjXJi3Ir3uerDs3atw2DSbhhnrFJVd3tNpuvENftklQcZwHvnU6HJcirWW0+tjRYpsGsfx6xXCD1PfY9v8dO01cQYhevooKo6kkdyvjeBQB62eha96b4dEk9eY/vUkMXtd9JgD2+VmmRq5idFiB+zjH8BieyXzN490eaRkA5r81wTUun27d+OfKUM37doRxiKkQUMdRv7fXejQTXtXnS8oIuAQaonQnRp7EUBQ9R/iX7/UGg==\"}");
//        WallytResponse<QueryTransferResponse> wallytResponse = new WallytResponse<>();
//        wallytResponse.setResponse(j.getObject("response", QueryTransferResponse.class));
//        wallytResponse.setSignature(j.getString("signature"));
//        System.out.println(wallytResponse);
//        System.out.println(WallytConst.CODE_SUCCESS.equalsIgnoreCase(wallytResponse.getResponse().getCode()));
        Map<String, Object> requestJson = Maps.newHashMap();
        List<CreateTransferNotifyReq> req = new ArrayList<>();
        CreateTransferNotifyReq c = new CreateTransferNotifyReq();
        c.setAttach("test");
        req.add(c);
        List<String> reqss = new ArrayList<>();
        reqss.add("test");
        requestJson.put("request", req);
        requestJson.put("signature", reqss);

        String jsonStr = JSONObject.toJSONString(requestJson);
        System.out.println(jsonStr);
        WallytCallbackReq wallytCallbackReq = JSONObject.parseObject(jsonStr, WallytCallbackReq.class);
        System.out.println(((JSONObject) wallytCallbackReq.getRequest().get(0)).toJavaObject(CreateTransferNotifyReq.class));
        System.out.println(requestJson);
//        String json = "{request=[{\"originalMsgId\":\"21904oqtfpg2\",\"partnerId\":\"100540000048\",\"trxAmount\":820,\"trxStatus\":\"SUCCESS\",\"trxDate\":\"2021-05-09T14:16:41.000Z\",\"currency\":\"NGN\",\"attach\":\"TaskP:21904oqtfpg2\"}], signature=[NfC6wFZvvk31mMktxteliKZOfJcCa6Omzi+7mPBHfQFteTg1xoHQvoVMSw4VNXX6VAl1kQB6oXXZBnfgkrJBy4FqdqCQf6zhrUCR7VBopvhW+1ND91JV6GeLpy8BUm3CZ+1mBFYeGaUgoj+dXYjAOjW9ysXZ9CV5X4nzK8SHPz9WzVigvnhH8CManU7XebE9DItDVms3nIta3r3cOvqRmWhcqnaoifMHARzdU/y6wgX4SLBJym1rkvuQsC55fCYKwSIOlq46+Lz0Erk74sE2eSwe/ofGQuxIrmVPCh3YyGL3lcxB/WFFMWSvOBFVnJuRpVF6qJPskb5aGnddbCzEkg==]}";
//        JSONObject j = JSONObject.parseObject(json);
//        WallytCallbackReq<CreateTransferNotifyReq> wallytCallbackReq = null;
//        System.out.println();
    }


}
