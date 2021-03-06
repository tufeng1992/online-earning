package com.powerboot.utils.paystack.core;

import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Iyanu Adelekan on 18/07/2016.
 */
public class Keys {

    private String TEST_SECRET_KEY;
    private String TEST_PUBLIC_KEY;
    private String LIVE_SECRET_KEY;
    private String LIVE_PUBLIC_KEY;
    String KEY_IN_USE;

    /**
     * Used to initialise all necessary API keys
     *
     * @throws FileNotFoundException
     */
    void initKeys() throws FileNotFoundException {
        this.TEST_SECRET_KEY = "sk_test_36a93b8d982c74f440095385b8c98c8bc2cc057b";
        this.TEST_PUBLIC_KEY = "pk_test_8c08eea9d2243324b05bf5c6aa34ccad92bf702d";
        this.LIVE_SECRET_KEY = RedisUtils.getValue(DictConsts.PAY_STACK_SK, String.class);
        this.LIVE_PUBLIC_KEY = RedisUtils.getValue(DictConsts.PAY_STACK_PK, String.class);
        this.KEY_IN_USE = LIVE_SECRET_KEY;
    }

    /**
     * Used to set test secret key
     *
     * @param key
     */
    protected void setTest_SECRET_KEY(String key) {
        this.TEST_SECRET_KEY = key;
    }

    /**
     * Used to get test secret key
     *
     * @return
     */
    protected String getTEST_SECRET_KEY() {
        return this.TEST_SECRET_KEY;
    }

    /**
     * Used to set test public key
     *
     * @param key
     */
    protected void setTEST_PUBLIC_KEY(String key) {
        this.TEST_PUBLIC_KEY = key;
    }

    /**
     * Used to get test public key
     *
     * @return
     */
    protected String getTEST_PUBLIC_KEY() {
        return this.TEST_PUBLIC_KEY;
    }

    /**
     * Used to set live secret key
     *
     * @param key
     */
    protected void setLIVE_SECRET_KEY(String key) {
        this.LIVE_SECRET_KEY = key;
    }

    /**
     * Used to get live secret key
     *
     * @return
     */
    protected String getLIVE_SECRET_KEY() {
        return this.LIVE_SECRET_KEY;
    }

    /**
     * Used to set live public key
     *
     * @param key
     */
    protected void setLIVE_PUBLIC_KEY(String key) {
        this.LIVE_PUBLIC_KEY = key;
    }

    /**
     * Used to get live public key
     *
     * @return
     */
    protected String getLIVE_PUBLIC_KEY() {
        return this.LIVE_PUBLIC_KEY;
    }

}
