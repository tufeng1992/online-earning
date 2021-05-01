package com.powerboot.utils.chuanglan.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author tianyh
 * @Description:国际短信发送实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsSendGJRequest {
    /**
     * 用户账号，必填
     */
    private String account;
    /**
     * 用户密码，必填
     */
    private String password;
    /**
     * 短信内容。长度不能超过536个字符，必填
     */
    private String msg;
    /**
     * 机号码。多个手机号码使用英文逗号分隔，必填
     */
    private String mobile;

}
