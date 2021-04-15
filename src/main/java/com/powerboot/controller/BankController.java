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
}
