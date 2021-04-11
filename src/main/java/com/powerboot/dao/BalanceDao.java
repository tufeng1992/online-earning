package com.powerboot.dao;

import com.powerboot.domain.BalanceDO;
import com.powerboot.response.PayVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 余额流水表
 */
@Mapper
public interface BalanceDao {

	BalanceDO get(Long id);

	BalanceDO getByOrderNo(@Param("orderNo") String orderNo);

	List<BalanceDO> list(Map<String,Object> map);

	List<BalanceDO> listByDate(Map<String,Object> map);

	int count(Map<String,Object> map);
	
	int save(BalanceDO balance);
	
	int update(BalanceDO balance);
	
	int remove(Long id);
	
	int batchRemove(Long[] ids);

	int updateStatusByOrderNo(@Param("orderNo") String orderNo,@Param("status") Integer status);

	List<BalanceDO> listByTypeAndUserId(@Param("userId")Long userId,@Param("type")Long type);

	PayVO getCountByTypeStatus(@Param("typeList") List<Integer> typeList, @Param("status") Integer status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
