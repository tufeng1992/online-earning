package com.powerboot.utils.momo.core;

import ci.bamba.regis.Collections;
import ci.bamba.regis.Environment;
import ci.bamba.regis.MoMo;
import ci.bamba.regis.Remittances;
import ci.bamba.regis.models.RequestToPay;
import ci.bamba.regis.models.Transfer;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@Lazy
public class MoMoClient {

    private MoMo momo;

    private Boolean inited = false;

    private void init() {
        momo = new MoMo(Environment.SANDBOX.getEnv().equalsIgnoreCase(RedisUtils.getString(DictConsts.MOMO_EVN))
                ? Environment.SANDBOX : Environment.PRODUCTION);
        collections = momo.createCollections(getCollectionSubKey(), getApiCollectionUser(), getApiCollectionKey());
        remittances = momo.createRemittances(getRemittanceSubKey(), getApiRemittanceUser(), getApiRemittanceKey());
    }

    private String getApiCollectionUser() {
        return RedisUtils.getString(DictConsts.MOMO_COLLECTION_API_ID);
    }

    private String getApiCollectionKey() {
        return RedisUtils.getString(DictConsts.MOMO_COLLECTION_API_KEY);
    }

    private String getApiRemittanceUser() {
        return RedisUtils.getString(DictConsts.MOMO_REMITTANCE_API_ID);
    }

    private String getApiRemittanceKey() {
        return RedisUtils.getString(DictConsts.MOMO_REMITTANCE_API_KEY);
    }

    private String getCollectionSubKey() {
        return RedisUtils.getString(DictConsts.MOMO_COLLECTION_SUB_KEY);
    }

    private String getRemittanceSubKey() {
        return RedisUtils.getString(DictConsts.MOMO_REMITTANCE_SUB_KEY);
    }

    private String getRemittanceCallBackUrl() {
        return RedisUtils.getString(DictConsts.MOMO_REMITTANCE_CALLBACKURL);
    }

    private String getCollectionCallBackUrl() {
        return RedisUtils.getString(DictConsts.MOMO_COLLECTION_CALLBACKURL);
    }

    private String getCurrency() {
        return RedisUtils.getString(DictConsts.MOMO_CURRENCY);
    }

    private Collections collections;

    private Remittances remittances;

    /**
     * 创建token
     */
    private String getCollectionToken() {
        if (!inited) {
            synchronized (inited) {
                init();
                inited = true;
            }
        }
        String cacheToken = RedisUtils.getString(DictConsts.MOMO_COLLECTION_TOKEN);
        if (StringUtils.isNotBlank(cacheToken)) {
            return cacheToken;
        }
        AtomicReference<String> res = new AtomicReference<>();
        collections.createToken().subscribe(
                token -> {
                    log.info("getCollectionToken, accessToken:{}, expiresIn:{}, tokenType:{}",
                            token.getAccessToken(), token.getExpiresIn(), token.getTokenType());
                    res.set(token.getAccessToken());
                    RedisUtils.setValue(DictConsts.MOMO_COLLECTION_TOKEN, token.getAccessToken(), token.getExpiresIn());
                }
        );
        return res.get();
    }

    /**
     * 创建token
     */
    private String getRemittanceToken() {
        if (!inited) {
            synchronized (inited) {
                init();
                inited = true;
            }
        }
        String cacheToken = RedisUtils.getString(DictConsts.MOMO_REMITTANCE_TOKEN);
        if (StringUtils.isNotBlank(cacheToken)) {
            return cacheToken;
        }

        AtomicReference<String> res = new AtomicReference<>();
        remittances.createToken().subscribe(
                token -> {
                    log.info("getRemittanceToken, accessToken:{}, expiresIn:{}, tokenType:{}",
                            token.getAccessToken(), token.getExpiresIn(), token.getTokenType());
                    res.set(token.getAccessToken());
                    RedisUtils.setValue(DictConsts.MOMO_REMITTANCE_TOKEN, token.getAccessToken(), token.getExpiresIn());
                }
        );
        return res.get();
    }

    /**
     * 创建支付
     * @param orderNo
     * @param amount
     * @param payerPartyId
     * @return
     */
    public String createPay(String orderNo, BigDecimal amount, String payerPartyId) {
        String payerMessage = "User payment description: goods payment"; // Avoid special characters as it causes Error 500 from the API.
        String payeeNote = "User payment description: goods payment";                 // Avoid special characters as it causes Error 500 from the API.

        AtomicReference<String> thirdId = new AtomicReference<>();
        String token = getCollectionToken();
        collections.requestToPay(token, getCollectionCallBackUrl(), amount.floatValue(), getCurrency(), orderNo, payerPartyId, payerMessage, payeeNote)
                .subscribe(referenceId -> {
                            log.info(referenceId); // e0c04c5b-e591-46fa-b3f9-92276fdfda4d
                            thirdId.set(referenceId);
                        }
                );
        return thirdId.get();
    }

    /**
     * 查询支付信息
     * @param referenceId
     */
    public RequestToPay queryPay(String referenceId) {
        AtomicReference<RequestToPay> res = new AtomicReference<>();
        String token = getCollectionToken();
        collections.getRequestToPay(token, referenceId).subscribe(
                requestToPay -> {
                    log.info(requestToPay.getFinancialTransactionId());    // 521734614
                    log.info(requestToPay.getStatus());                    // SUCCESSFUL
                    res.set(requestToPay);
                }
        );
        return res.get();
    }

    /**
     * 创建转账
     * @param orderNo
     * @param payeePartyId
     * @param amount
     * @return
     */
    public String createTransfer(String orderNo, String payeePartyId, BigDecimal amount) {
        String payerMessage = "Transfer to user: Merchant service fee"; // Avoid special characters as it causes Error 500 from the API.
        String payeeNote = "Transfer to user: Merchant service fee";                 // Avoid special characters as it causes Error 500 from the API.

        AtomicReference<String> thirdId = new AtomicReference<>();
        String token = getRemittanceToken();
        remittances.transfer(token, getRemittanceCallBackUrl(), amount.floatValue(), getCurrency(), orderNo, payeePartyId, payerMessage, payeeNote)
                .subscribe(referenceId -> {
                            log.info("createTransfer, referenceId:{}", referenceId); // e0c04c5b-e591-46fa-b3f9-92276fdfda4d
                            thirdId.set(referenceId);
                        }
                );
        return thirdId.get();
    }

    /**
     * 查询转账
     * @param thirdOrderNo
     * @return
     */
    public Transfer queryTransfer(String thirdOrderNo) {
        AtomicReference<Transfer> res = new AtomicReference<>();
        String token = getRemittanceToken();
        remittances.getTransfer(token, thirdOrderNo).subscribe(
                transfer -> {
                    log.info("queryTransfer, FinancialTransactionId:{}, status:{}", transfer.getFinancialTransactionId(), transfer.getStatus());
                    res.set(transfer);
                }
        );
        return res.get();
    }
}
