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
        JSONObject jsonObject = paystackInline.selectBankList("");
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
                "      \"code\": \"KBANK\",\n" +
                "      \"name\": \"KASIKORNBANK PCL\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"BBL\",\n" +
                "      \"name\": \"BANGKOK BANK PUBLIC COMPANY LTD.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"BAAC\",\n" +
                "      \"name\": \"BANK FOR AGRICULTURE AND AGRICULTURAL CO-OPERATIVES\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"BOA\",\n" +
                "      \"name\": \"BANK OF AMERICA NT&SA\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"BAY\",\n" +
                "      \"name\": \"BANK OF AYUDHAYA PUBLIC COMPANY LTD.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"BOC\",\n" +
                "      \"name\": \"Bank of China (Thai) PCL\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"BNPP\",\n" +
                "      \"name\": \"BNP PARIBAS BANGKOK BRANCH\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"CIMB\",\n" +
                "      \"name\": \"CIMB THAI BANK PUBLIC COMPANY LTD.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"CITI\",\n" +
                "      \"name\": \"CITI BANK N.A.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"DB\",\n" +
                "      \"name\": \"Deutsche Bank AG\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"GHB\",\n" +
                "      \"name\": \"GOVERNMENT HOUSING BANK\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"ICBC\",\n" +
                "      \"name\": \"INDUSTRIAL AND COMMERCIAL BANK OF CHINA (THAI) PCL\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"TIBT\",\n" +
                "      \"name\": \"ISLAMIC BANK OF THAILAND\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"CHAS\",\n" +
                "      \"name\": \"JPMorgan Chase Bank, Bangkok Branch\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"KKB\",\n" +
                "      \"name\": \"KIATNAKIN BANK PCL\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"KTB\",\n" +
                "      \"name\": \"KRUNG THAI BANK PUBLIC COMPANY LTD.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"LHBA\",\n" +
                "      \"name\": \"Land and Houses Bank\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"MEGA\",\n" +
                "      \"name\": \"MEGA INTERNATIONAL COMMERCIAL BANK\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"MHCB\",\n" +
                "      \"name\": \"MIZUHO CORPORATE BANK\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"SCBT\",\n" +
                "      \"name\": \"STANDARD CHARTERED BANK THAI PCL.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"SMTB\",\n" +
                "      \"name\": \"Sumitomo Mitsui Trust Bank (Thai) PCL.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"TBNK\",\n" +
                "      \"name\": \"Thanachart Bank Public Company Limited\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"GSB\",\n" +
                "      \"name\": \"THE GOVERNMENT SAVING BANK\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"HSBC\",\n" +
                "      \"name\": \"THE HONGKONG & SHANGHAI CORPORATION LTD.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"SCB\",\n" +
                "      \"name\": \"THE SIAM COMMERCIAL BANK PUBLIC COMPANY\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"SMBC\",\n" +
                "      \"name\": \"THE SUMITOMO MITSU BANKING CORPORATION\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"TCRB\",\n" +
                "      \"name\": \"THE THAI CREDIT RETAIL BANK\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"TISCO\",\n" +
                "      \"name\": \"TISCO Bank PCL\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"TMB\",\n" +
                "      \"name\": \"TMB BANK PUBLIC COMPANY LTD.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"UOB\",\n" +
                "      \"name\": \"UNITED OVERSEAS BANK (THAI) PUBLIC COMPANY LTD.\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"EXIM\",\n" +
                "      \"name\": \"EXPORT–IMPORT BANK OF THAILAND\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"code\": \"BOT\",\n" +
                "      \"name\": \"BANK OF THAILAND\"\n" +
                "    }\n" +
                "  ]\n";
        JSONArray j = JSONArray.parseArray(json);
        System.out.println(j.toJavaList(PayStackBank.class));
        System.out.println(j.toJSONString());
    }
}
