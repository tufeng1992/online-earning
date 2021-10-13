package com.powerboot.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.powerboot.domain.OrderDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 刷单任务订单表
 * @author system
 * @email system@163.com
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderDO> {

	OrderDO get(Long id);

	BigDecimal sumAmount(Map<String,Object> map);

	List<OrderDO> list(Map<String,Object> map);

	List<Long> getRiskUser();

	List<OrderDO> teamList(Map<String,Object> map);

	int count(Map<String,Object> map);
	
	int save(OrderDO order);
	
	int update(OrderDO order);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);

	List<OrderDO>  getByIds(List<Long> ids);
}
