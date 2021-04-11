package com.powerboot.service;

import com.powerboot.common.JsonUtils;
import com.powerboot.consts.DictConsts;
import com.powerboot.dao.DictDao;
import com.powerboot.domain.DictDO;
import com.powerboot.utils.RedisUtils;
import com.powerboot.utils.MobileUtil;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DictService{

    private static Logger logger = LoggerFactory.getLogger(DictService.class);

    @Autowired
	private DictDao dictDao;

    /**
     * 初始化数据字典至缓存
     */
	public void initDict2Cache(){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sort","weight");
        paramMap.put("order","desc");
        List<DictDO> list = this.list(paramMap);
        Map<String, List<DictDO>> collect = list.stream().collect(Collectors.groupingBy(DictDO::getKey));
        collect.forEach((k, v) -> {
            String value;
            if(v.size()>1){
                value = JsonUtils.toJSONString(v.stream().map(DictDO::getValue).collect(Collectors.toList()));
            }else{
                value = v.get(0).getValue();
            }
            RedisUtils.setValue(k,value, DictConsts.DICT_CACHE_LIVE_TIME + Integer.parseInt(MobileUtil.getRandom(10,1000)));
        });
        logger.info("数据字典已被加载至缓存.........");
    }
	
	public DictDO get(Long id){
		return dictDao.get(id);
	}
	
	public List<DictDO> list(Map<String, Object> map){
		return dictDao.list(map);
	}

	public List<DictDO> listByKey(String key){
		return dictDao.listByKey(key);
	}

	public List<DictDO> listByKeys(List<String> keys){
		return dictDao.listByKeys(keys);
	}
	
	public int count(Map<String, Object> map){
		return dictDao.count(map);
	}
	
	public int save(DictDO dict){
		return dictDao.save(dict);
	}
	
	public int update(DictDO dict){
		return dictDao.update(dict);
	}
	
	public int remove(Long id){
		return dictDao.remove(id);
	}
	
	public int batchRemove(Long[] ids){
		return dictDao.batchRemove(ids);
	}

	/**
	 * 找出权重最大的key
	 * @param key
	 * @return
	 */
	public DictDO selectOneByKey(String key){
		return dictDao.selectOneByKey(key);
	}

	public Integer parseInt(DictDO dictDO){
		return dictDO != null ?  (dictDO.getValue() !=null ? Integer.parseInt(dictDO.getValue()) : null) : null;
	}
	
}
