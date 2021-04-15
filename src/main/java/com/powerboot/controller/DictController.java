package com.powerboot.controller;

import com.powerboot.base.BaseResponse;
import com.powerboot.consts.DictConsts;
import com.powerboot.domain.DictDO;
import com.powerboot.service.DictService;

import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.MobileUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping("/get/{key}")
	@ApiOperation("根据key查询指定配置信息")
	public BaseResponse<String> getByKey(@PathVariable("key") String key){
		String res = RedisUtils.getString(key);
		if (StringUtils.isBlank(res)) {
			List<DictDO> dictDOS = dictService.listByKey(key);
			if (!CollectionUtils.isEmpty(dictDOS)){
				DictDO dictDO = dictDOS.get(0);
				res = dictDO.getValue();
				RedisUtils.setValue(key, dictDO.getValue(),
						DictConsts.DICT_CACHE_LIVE_TIME + Integer.parseInt(MobileUtil.getRandom(10,1000)));
			}
		}
		return BaseResponse.success(res);
	}

}
