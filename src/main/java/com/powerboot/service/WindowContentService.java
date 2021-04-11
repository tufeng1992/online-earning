package com.powerboot.service;

import com.powerboot.consts.DictConsts;
import com.powerboot.dao.WindowContentDao;
import com.powerboot.domain.WindowContentDO;
import com.powerboot.utils.RedisUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WindowContentService {

    @Autowired
    private WindowContentDao windowContentDao;

    /**
     * 获取弹窗内容
     */
    public List<WindowContentDO> getContent() {
        String windowContentKey = "windowContent";
        return RedisUtils.setListIfNotExists(windowContentKey, () -> {
                return windowContentDao.getContent();
            },
            DictConsts.DICT_CACHE_LIVE_TIME, WindowContentDO.class);
    }


}
