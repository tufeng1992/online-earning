package com.powerboot.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.powerboot.dao.SysUserDao;
import com.powerboot.domain.SysUserDO;
import com.powerboot.domain.UserDO;
import com.powerboot.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SysUserService extends ServiceImpl<SysUserDao, SysUserDO> {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private UserService userService;

    /**
     * 获取whatsapp
     * @param saleId
     * @return
     */
    public String getWhatsapp(Long saleId) {
        UserDO userDO = userService.get(saleId);
        if (null == userDO || StringUtils.isBlank(userDO.getMobile())) {
            return "";
        }
        SysUserDO sysUserDO = sysUserDao.selectById(Long.valueOf(userDO.getMobile()));
        return null == sysUserDO || StringUtils.isBlank(sysUserDO.getWhatsapp()) ? "" : sysUserDO.getWhatsapp();
    }

}
