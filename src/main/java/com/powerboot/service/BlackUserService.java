package com.powerboot.service;

import com.powerboot.config.BaseException;
import com.powerboot.consts.BlackHelper;
import com.powerboot.consts.DictConsts;
import com.powerboot.utils.RedisUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

@Service
public class BlackUserService {

    @Autowired
    private UserService userService;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 黑名单验证
     */
    public void blackCheck(String mobile, String name,String email,Long userId){
        if(BlackHelper.blackId(String.valueOf(userId))){
            throw new BaseException(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
        }
        if(StringUtils.isNotBlank(mobile) && BlackHelper.blackMobile(mobile)){
            handleAll(userId);
            throw new BaseException(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
        }
        if(StringUtils.isNotBlank(email) && BlackHelper.blackEmail(email)){
            handleAll(userId);
            throw new BaseException(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
        }

        if(StringUtils.isNotBlank(name) && BlackHelper.blackName(name)){
            handleAll(userId);
            throw new BaseException(RedisUtils.getString(DictConsts.RISK_USER_BLACK_TIP));
        }
    }

    private void handleAll(Long userId){
        executorService.execute(() -> {
            //关闭所有开关
            userService.updateClose(userId);
            //添加用户id黑名单缓存
            BlackHelper.setBlackId(String.valueOf(userId));
        });
    }

}
