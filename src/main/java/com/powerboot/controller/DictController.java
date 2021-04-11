package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictConsts;
import com.powerboot.domain.DictDO;
import com.powerboot.service.DictService;

import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.MobileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据配置表
 */
 
@RestController
@RequestMapping("/dict")
public class DictController {
	@Autowired
	private DictService dictService;

	/**
	 * 刷新指定key缓存
	 * @param key
	 * @return
	 */
	@GetMapping("/{key}")
	public BaseResponse refresh(@PathVariable("key") String key){
		List<DictDO> dictDOS = dictService.listByKey(key);
		if (!CollectionUtils.isEmpty(dictDOS)){
			dictDOS.forEach(o->{
				RedisUtils.setValue(o.getKey(),o.getValue(), DictConsts.DICT_CACHE_LIVE_TIME + Integer.parseInt(MobileUtil.getRandom(10,1000)));
			});
		}
		return BaseResponse.success();
	}

}
