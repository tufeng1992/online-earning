package com.powerboot.utils.paystack.core;

import com.powerboot.utils.paystack.constants.Definitions;
import com.razorpay.Transfer;
import org.bouncycastle.cms.Recipient;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;


/**
 * @author Iyanu Adelekan on 19/07/2016.
 */
@Component
public class PaystackInline {

    private ApiConnection apiConnection;

    /**
     *
     * @param queryMap
     * @return
     */
    public JSONObject paystackStandard(HashMap<String, Object> queryMap) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INLINE_PAYSTACK_STANDARD);
        return this.apiConnection.connectAndQuery(queryMap);
    }

    /**
     *
     * @param query
     * @return
     */
    public JSONObject paystackStandard(ApiQuery query) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INLINE_PAYSTACK_STANDARD);
        return this.apiConnection.connectAndQuery(query);
    }

    /**
     *
     * @param reference
     * @param amount
     * @param email
     * @param plan
     * @param callback_url
     * @return
     */
    public JSONObject paystackStandard(String reference, int amount, String email, String plan, String callback_url) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INLINE_PAYSTACK_STANDARD);
        ApiQuery apiQuery = new ApiQuery();

        apiQuery.putParams("reference", reference);
        apiQuery.putParams("amount", amount);
        apiQuery.putParams("email", email);
        apiQuery.putParams("plan", plan);
        apiQuery.putParams("callback_url", callback_url);
        apiQuery.putParams("currency", "GHS");

        return apiConnection.connectAndQuery(apiQuery);
    }

    /**
     *
     * @param reference
     * @return
     */
    public JSONObject verifyTransactions(String reference) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INLINE_VERIFY_TRANSACTIONS.concat(reference));
        return this.apiConnection.connectAndQueryWithGet();
    }

    /**
     *
     * @param queryMap
     * @return
     */
    public JSONObject chargeReturningCustomer(HashMap<String, Object> queryMap) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INLINE_CHARGE_AUTHORIZATION);
        return this.apiConnection.connectAndQuery(queryMap);
    }

    /**
     *
     * @param query
     * @return
     */
    public JSONObject chargeReturningCustomer(ApiQuery query) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INLINE_CHARGE_AUTHORIZATION);
        return this.apiConnection.connectAndQuery(query);
    }

    /**
     *
     * @param authorization_code
     * @param email
     * @param amount
     * @param reference
     * @return
     */
    public JSONObject chargeReturningCustomer(String authorization_code, String email, String amount, String reference) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INLINE_CHARGE_AUTHORIZATION);
        ApiQuery apiQuery = new ApiQuery();

        apiQuery.putParams("authorization_code", authorization_code);
        apiQuery.putParams("email", email);
        apiQuery.putParams("amount", amount);
        apiQuery.putParams("reference", reference);

        return this.apiConnection.connectAndQuery(apiQuery);
    }

    /**
     * 发起转账
     * @param reference 订单号
     * @param amount 转账金额
     * @param recipient 转账接收者代码
     * @return
     */
    public JSONObject initiateTransfer(String reference, String amount, String recipient) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_INITIATE_TRANSFER);
        ApiQuery apiQuery = new ApiQuery();
        apiQuery.putParams("recipient", recipient);
        apiQuery.putParams("amount", amount);
        apiQuery.putParams("reference", reference);
        apiQuery.putParams("source", "balance");
        apiQuery.putParams("currency", "GHS");
        return this.apiConnection.connectAndQuery(apiQuery);
    }

    public JSONObject verifyTransfer(String reference) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_VERIFY_TRANSFER.concat(reference));
        return this.apiConnection.connectAndQueryWithGet();
    }

    /**
     * 创建转账接收者
     * @param name
     * @param accountNumber
     * @param bankCode
     * @return
     */
    public JSONObject createTransferRecipient(String name, String accountNumber, String bankCode) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_CREATE_TRANSFER_RECIPIENT);
        ApiQuery apiQuery = new ApiQuery();
        apiQuery.putParams("type", "nuban");
        apiQuery.putParams("account_number", accountNumber);
        apiQuery.putParams("bank_code", bankCode);
        apiQuery.putParams("name", name);
        apiQuery.putParams("currency", "GHS");
        return this.apiConnection.connectAndQuery(apiQuery);
    }

    /**
     * 查询银行列表
     * @param country
     * @return
     */
    public JSONObject selectBankList(String country) {
        this.apiConnection = new ApiConnection(Definitions.PAYSTACK_BANK_LIST);
        ApiQuery apiQuery = new ApiQuery();
        apiQuery.putParams("country", country);
        apiQuery.putParams("perPage", 1000);
        return this.apiConnection.connectAndQueryWithGet(apiQuery);
    }

}
