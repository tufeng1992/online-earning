package com.powerboot.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.paystack.core.PaystackInline;
import com.powerboot.utils.paystack.domain.dto.PayStackBank;
import com.powerboot.utils.paystack.domain.dto.PayStackResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bank")
@Api(tags = "银行服务")
public class BankController extends BaseController {

    /**
     * 获取银行列表信息
     * @return
     */
    @PostMapping("/banks")
    @ApiOperation("获取银行列表信息")
    public BaseResponse<List<PayStackBank>> selectBankList() {
        List<PayStackBank> res = RedisUtils.getList(DictConsts.PAY_STACK_BANK_LIST, PayStackBank.class);
        if (CollectionUtils.isNotEmpty(res)) {
            return BaseResponse.success(res);
        }
        PaystackInline paystackInline = new PaystackInline();
        JSONObject jsonObject = paystackInline.selectBankList("ghana");
        PayStackResponse response = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.toString(), PayStackResponse.class);
        if (response.getStatus()) {
            res = JSONArray.parseArray(response.getData().toString(), PayStackBank.class);
        }
        List<PayStackBank> finalRes = res;
        RedisUtils.setListIfNotExists(DictConsts.PAY_STACK_BANK_LIST, () -> finalRes,
                DictConsts.DICT_CACHE_LIVE_TIME, PayStackBank.class);
        return BaseResponse.success(res);
    }

    public static void main(String[] args) {
        String json = "\n" +
                "  [    \n" +
                "    {\n" +
                "      \"code\": \"ZAR10010\",\n" +
                "      \"name\": \"Absa\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10011\",\n" +
                "      \"name\": \"NedBank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10012\",\n" +
                "      \"name\": \"Capitec\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10013\",\n" +
                "      \"name\": \"Standard\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10014\",\n" +
                "      \"name\": \"Fnb\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10015\",\n" +
                "      \"name\": \"African Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10016\",\n" +
                "      \"name\": \"Bidvest Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10017\",\n" +
                "      \"name\": \"Discovery\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10018\",\n" +
                "      \"name\": \"First National Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10019\",\n" +
                "      \"name\": \"FirstRand Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10020\",\n" +
                "      \"name\": \"Grindrod Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10021\",\n" +
                "      \"name\": \"Imperial Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10022\",\n" +
                "      \"name\": \"Investec Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10023\",\n" +
                "      \"name\": \"Sasfin Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10024\",\n" +
                "      \"name\": \"Standard Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10025\",\n" +
                "      \"name\": \"Ubank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10026\",\n" +
                "      \"name\": \"TymeBank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10027\",\n" +
                "      \"name\": \"Mercantile Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10028\",\n" +
                "      \"name\": \"Albaraka Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10029\",\n" +
                "      \"name\": \"HBZ Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10030\",\n" +
                "      \"name\": \"Habib Overseas Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10031\",\n" +
                "      \"name\": \"Wesbank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10032\",\n" +
                "      \"name\": \"Rand Merchant Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ZAR10033\",\n" +
                "      \"name\": \"Bank of Athens\"\n" +
                "    }\n" +
                "  ]\n";
        JSONArray j = JSONArray.parseArray(json);
        System.out.println(j.toJavaList(PayStackBank.class));
        System.out.println(j.toJSONString());
    }
}
