package com.powerboot.domain;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@TableName(value = "sys_user")
public class SysUserDO implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    @TableId
    private Long userId;
    // 用户名
    private String username;
    // 用户真实姓名
    private String name;
    // 密码
    private String password;
    // 部门
    private Long deptId;
    // 邮箱
    private String email;
    // 手机号
    private String mobile;
    // 状态 0:禁用，1:正常
    private Integer status;
    // 创建用户id
    private Long userIdCreate;
    // 关联APP用户id
    private Long appUserId;
    // 创建时间
    private Date gmtCreate;
    // 修改时间
    private Date gmtModified;
    //性别
    private Long sex;
    //出身日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birth;
    //图片ID
    private Long picId;
    //现居住地
    private String liveAddress;
    //爱好
    private String hobby;
    //省份
    private String province;
    //所在城市
    private String city;
    //所在地区
    private String district;

    //是否参与团队管理 0-不参与 1-参与
    private Integer teamFlag;

    //是否团队长 0-不是 1-是
    private Integer teamLeader;

    //团队长id
    private Integer leaderSysId;

    /**
     * whatsapp通讯code
     */
    private String whatsapp;
}
